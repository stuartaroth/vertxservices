package org.stuartaroth.vertxservices;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class Application {
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        logger.info("starting to deploy verticles...");

        logger.info(.56f * 100f);

        vertx.deployVerticle("org.stuartaroth.vertxservices.services.book.BookVerticle");
        vertx.deployVerticle("org.stuartaroth.vertxservices.services.movie.MovieVerticle");
        vertx.deployVerticle("org.stuartaroth.vertxservices.services.media.MediaVerticle");
    }
}
