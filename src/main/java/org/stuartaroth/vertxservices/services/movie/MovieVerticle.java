package org.stuartaroth.vertxservices.services.movie;

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
import org.stuartaroth.vertxservices.models.Movie;
import org.stuartaroth.vertxservices.staticservices.JsonService;

import java.util.List;

import static org.stuartaroth.vertxservices.staticservices.PortService.MOVIE_PORT;

public class MovieVerticle extends AbstractVerticle {

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        MovieDataService movieDataService = new MovieDataService();

        EventBus eventBus = this.vertx.eventBus();

        Handler<Message<String>> searchMoviesByGenre = event -> {
            String genre = event.body();
            movieDataService.searchMoviesByGenre(genre).subscribe(results -> {
                event.reply(JsonService.write(results));
            });
        };

        eventBus.consumer("movie.genre", searchMoviesByGenre);

        PublishSubject<HttpServerRequest> requestPublishSubject = PublishSubject.create();
        requestPublishSubject.subscribe(request -> {
            MultiMap params = request.params();

            String creator = params.get("creator");
            String genre = params.get("genre");

            if (creator == null && genre == null) {

                request.response().putHeader("Content-Type", "application/json").end("[]");

            } else if (creator != null) {

                Observable<List<Movie>> movies = movieDataService.searchMoviesByCreator(creator);
                movies.subscribe(results -> {
                    request.response().putHeader("Content-Type", "application/json").end(JsonService.write(results));
                });

            } else {

                Observable<List<Movie>> books = movieDataService.searchMoviesByGenre(genre);
                books.subscribe(results -> {
                    request.response().putHeader("Content-Type", "application/json").end(JsonService.write(results));
                });

            }
        });

        Single<HttpServer> single = this.vertx
                .createHttpServer()
                .requestHandler(requestPublishSubject::onNext)
                .rxListen(MOVIE_PORT);

        single.subscribe(
                server -> {
                    System.out.println(this.getClass().toString() + " started on http://localhost:" + MOVIE_PORT);
                }, failure -> {
                    System.out.println("error: " + failure.getMessage());
                });
    }
}
