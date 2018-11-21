package org.stuartaroth.vertxservices.services.book;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLClient;
import io.vertx.reactivex.ext.sql.SQLConnection;
import org.stuartaroth.vertxservices.models.Book;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BookDataService {
    private static Logger logger = LoggerFactory.getLogger(BookDataService.class);


    private SQLClient sqlClient;
    private List<Book> books;

    public BookDataService(Vertx vertx) {
        JsonObject postgresConfig = new JsonObject()
                .put("url", "jdbc:postgresql://localhost:5432/book")
                .put("driver_class", "org.postgresql.postgresql")
                .put("user", "postgres")
                .put("password", "mysecretpassword");

        sqlClient = JDBCClient.createNonShared(vertx, postgresConfig);

        books = Arrays.asList(
                new Book("It", "Stephen King", "Horror"),
                new Book("A Game of Thrones", "George R.R. Martin", "Fantasy"),
                new Book("A Scanner Darkly", "Philip K. Dick", "Science Fiction")
        );


    }

    // select * from books;
    Observable<List<Book>> getAllBooks() {
//        sqlClient.rxGetConnection().subscribe(sqlConnection -> {
//            sqlConnection.rxQuery("select * from book.books;").subscribe(resultSet -> {
//                resultSet.getRows().stream().map(result -> {
//                    logger.info(result.toString());
//                    return result.toString();
//                });
//            });
//        });

        return Observable.fromArray(books);
    }

    // select * from books where lower(data ->> 'author') like  lower('%?%');
    Observable<List<Book>> searchBooksByCreator(String search) {
        String loweredSearch = search.toLowerCase();
        return Observable.fromArray(
                books
                        .stream()
                        .filter(item -> item.getCreator().toLowerCase().contains(loweredSearch))
                        .collect(Collectors.toList())
        );
    }

    // select * from books where lower(data ->> 'genre') like  lower('%?%');
    Observable<List<Book>> searchBooksByGenre(String search) {
        String loweredSearch = search.toLowerCase();
        return Observable.fromArray(
                books
                        .stream()
                        .filter(item -> item.getGenre().toLowerCase().contains(loweredSearch))
                        .collect(Collectors.toList())
        );
    }
}
