package org.stuartaroth.vertxservices;

import io.vertx.core.Vertx;

public class Application {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle("org.stuartaroth.vertxservices.services.book.BookVerticle");
        vertx.deployVerticle("org.stuartaroth.vertxservices.services.movie.MovieVerticle");
        vertx.deployVerticle("org.stuartaroth.vertxservices.services.media.MediaVerticle");
    }
}
