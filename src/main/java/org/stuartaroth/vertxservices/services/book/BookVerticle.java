package org.stuartaroth.vertxservices.services.book;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.Context;

import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import org.stuartaroth.vertxservices.models.Book;
import org.stuartaroth.vertxservices.staticservices.JsonService;

import java.util.List;

import static org.stuartaroth.vertxservices.staticservices.PortService.BOOK_PORT;

public class BookVerticle extends AbstractVerticle {

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        BookDataService bookDataService = new BookDataService();

        EventBus eventBus = this.vertx.eventBus();

        Handler<Message<String>> searchBooksByGenre = event -> {
            String genre = event.body();
            bookDataService.searchBooksByGenre(genre).subscribe(results -> {
                event.reply(JsonService.write(results));
            });
        };

        eventBus.consumer("book.genre", searchBooksByGenre);

        PublishSubject<HttpServerRequest> requestPublishSubject = PublishSubject.create();
        requestPublishSubject.subscribe(request -> {
            MultiMap params = request.params();

            String creator = params.get("creator");
            String genre = params.get("genre");

            if (creator == null && genre == null) {

                request.response().putHeader("Content-Type", "application/json").end("[]");

            } else if (creator != null) {

                Observable<List<Book>> books = bookDataService.searchBooksByCreator(creator);
                books.subscribe(results -> {
                    request.response().putHeader("Content-Type", "application/json").end(JsonService.write(results));
                });

            } else {

                Observable<List<Book>> books = bookDataService.searchBooksByGenre(genre);
                books.subscribe(results -> {
                    request.response().putHeader("Content-Type", "application/json").end(JsonService.write(results));
                });

            }
        });

        Single<HttpServer> single = this.vertx
                .createHttpServer()
                .requestHandler(requestPublishSubject::onNext)
                .rxListen(BOOK_PORT);

        single.subscribe(
                server -> {
                    System.out.println(this.getClass().toString() + " started on http://localhost:" + BOOK_PORT);
                }, failure -> {
                    System.out.println("error: " + failure.getMessage());
                });
    }
}
