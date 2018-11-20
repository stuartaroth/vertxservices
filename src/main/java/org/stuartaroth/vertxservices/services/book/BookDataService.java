package org.stuartaroth.vertxservices.services.book;

import io.reactivex.Observable;
import org.stuartaroth.vertxservices.models.Book;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BookDataService {
    private List<Book> books;

    public BookDataService() {
        books = Arrays.asList(
                new Book("It", "Stephen King", "Horror"),
                new Book("A Game of Thrones", "George R.R. Martin", "Fantasy"),
                new Book("A Scanner Darkly", "Philip K. Dick", "Science Fiction")
        );
    }

    Observable<List<Book>> searchBooksByCreator(String search) {
        String loweredSearch = search.toLowerCase();
        return Observable.fromArray(
                books
                        .stream()
                        .filter(item -> item.getCreator().toLowerCase().contains(loweredSearch))
                        .collect(Collectors.toList())
        );
    }

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
