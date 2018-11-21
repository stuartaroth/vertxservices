package org.stuartaroth.vertxservices.services.book;

import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;
import io.vertx.core.Vertx;
import io.vertx.core.Context;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLClient;
import org.stuartaroth.vertxservices.staticservices.JsonService;

import static org.stuartaroth.vertxservices.staticservices.AddressService.BOOK_ALL;
import static org.stuartaroth.vertxservices.staticservices.AddressService.BOOK_CREATOR;
import static org.stuartaroth.vertxservices.staticservices.AddressService.BOOK_GENRE;
import static org.stuartaroth.vertxservices.staticservices.PortService.BOOK_PORT;

public class BookVerticle extends AbstractVerticle {
    private static Logger logger = LoggerFactory.getLogger(BookVerticle.class);

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        BookDataService bookDataService = new BookDataService(this.vertx);

        EventBus eventBus = this.vertx.eventBus();

        eventBus.consumer(BOOK_ALL, event -> {
            bookDataService.getAllBooks().subscribe(results -> {
                event.reply(JsonService.write(results));
            });
        });

        eventBus.consumer(BOOK_CREATOR, event -> {
            String creator = event.body().toString();
            bookDataService.searchBooksByCreator(creator).subscribe(results -> {
                event.reply(JsonService.write(results));
            });
        });

        eventBus.consumer(BOOK_GENRE, event -> {
            String genre = event.body().toString();
            bookDataService.searchBooksByGenre(genre).subscribe(results -> {
                event.reply(JsonService.write(results));
            });
        });

        PublishSubject<HttpServerRequest> requestPublishSubject = PublishSubject.create();
        requestPublishSubject.subscribe(request -> {
            MultiMap params = request.params();

            String creator = params.get("creator");
            String genre = params.get("genre");

            if (creator == null && genre == null) {

                eventBus.send(BOOK_ALL, null, handler -> {
                    request.response().putHeader("Content-Type", "application/json").end(handler.result().body().toString());
                });

            } else if (creator != null) {

                eventBus.send(BOOK_CREATOR, creator, handler -> {
                    request.response().putHeader("Content-Type", "application/json").end(handler.result().body().toString());
                });

            } else {

                eventBus.send(BOOK_GENRE, genre, handler -> {
                    request.response().putHeader("Content-Type", "application/json").end(handler.result().body().toString());
                });

            }
        });

        Single<HttpServer> single = this.vertx
                .createHttpServer()
                .requestHandler(requestPublishSubject::onNext)
                .rxListen(BOOK_PORT);

        single.subscribe(
                server -> {
                    logger.info("started on http://localhost:" + BOOK_PORT);
                }, failure -> {
                    logger.error("error: {}", failure.getMessage());
                });
    }
}
