package org.stuartaroth.vertxservices.services.media;

import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;
import io.vertx.core.Vertx;
import io.vertx.core.Context;

import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.CompositeFuture;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.stuartaroth.vertxservices.models.Book;
import org.stuartaroth.vertxservices.models.Media;
import org.stuartaroth.vertxservices.models.Movie;
import org.stuartaroth.vertxservices.staticservices.JsonService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.stuartaroth.vertxservices.staticservices.PortService.BOOK_PORT;
import static org.stuartaroth.vertxservices.staticservices.PortService.MEDIA_PORT;
import static org.stuartaroth.vertxservices.staticservices.PortService.MOVIE_PORT;

public class MediaVerticle extends AbstractVerticle {

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        EventBus eventBus = this.vertx.eventBus();

        WebClient webClient = WebClient.create(this.vertx);

        PublishSubject<HttpServerRequest> requestPublishSubject = PublishSubject.create();
        requestPublishSubject.subscribe(request -> {
            MultiMap params = request.params();

            String creator = params.get("creator");
            String genre = params.get("genre");

            if (creator == null && genre == null) {

                request.response().putHeader("Content-Type", "application/json").end("[]");

            } else if (creator != null) {

                Future<HttpResponse<Buffer>> bookFuture = Future.future();
                webClient.get(BOOK_PORT, "localhost", "/").addQueryParam("creator", creator).send(bookFuture.completer());

                Future<HttpResponse<Buffer>> movieFuture = Future.future();
                webClient.get(MOVIE_PORT, "localhost", "/").addQueryParam("creator", creator).send(movieFuture.completer());

                CompositeFuture.all(bookFuture, movieFuture).setHandler(result -> {
                    String bookJson = bookFuture.result().bodyAsString();
                    String movieJson = movieFuture.result().bodyAsString();

                    List<Book> books = JsonService.read(bookJson, JsonService.LIST_OF_BOOK);
                    List<Movie> movies = JsonService.read(movieJson, JsonService.LIST_OF_MOVIE);

                    List<Media> medias = Stream.concat(books.stream(), movies.stream()).collect(Collectors.toList());

                    request.response().putHeader("Content-Type", "application/json").end(JsonService.write(medias));
                });

            } else {

                Future<Message<String>> bookFuture = Future.future();
                eventBus.send("book.genre", genre, bookFuture.completer());

                Future<Message<String>> movieFuture = Future.future();
                eventBus.send("movie.genre", genre, movieFuture.completer());

                CompositeFuture.all(bookFuture, movieFuture).setHandler(result -> {
                    List<Book> books = JsonService.read(bookFuture.result().body(), JsonService.LIST_OF_BOOK);
                    List<Movie> movies = JsonService.read(movieFuture.result().body(), JsonService.LIST_OF_MOVIE);

                    List<Media> medias = Stream.concat(books.stream(), movies.stream()).collect(Collectors.toList());

                    request.response().putHeader("Content-Type", "application/json").end(JsonService.write(medias));
                });

            }
        });

        Single<HttpServer> single = this.vertx
                .createHttpServer()
                .requestHandler(requestPublishSubject::onNext)
                .rxListen(MEDIA_PORT);

        single.subscribe(
                server -> {
                    System.out.println(this.getClass().toString() + " started on http://localhost:" + MEDIA_PORT);
                }, failure -> {
                    System.out.println("error: " + failure.getMessage());
                });
    }
}
