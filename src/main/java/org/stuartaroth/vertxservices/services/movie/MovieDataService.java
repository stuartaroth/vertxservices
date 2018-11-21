package org.stuartaroth.vertxservices.services.movie;

import io.reactivex.Observable;
import org.stuartaroth.vertxservices.models.Movie;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MovieDataService {
    private List<Movie> movies;

    public MovieDataService() {
        movies = Arrays.asList(
                new Movie("The Shining", "Stanley Kubrick", "Horror"),
                new Movie("2001: A Space Odyssey", "Stanley Kubrick", "Science Fiction"),
                new Movie("Close Encounters of the Third Kind", "Steven Spielberg", "Science Fiction"),
                new Movie("The Fellowship of the Ring", "Peter Jackson", "Fantasy"),
                new Movie("It Follows", "David Robert Mitchell", "Horror")
        );
    }

    Observable<List<Movie>> getAllMovies() {
        return Observable.fromArray(movies);
    }

    Observable<List<Movie>> searchMoviesByCreator(String search) {
        String loweredSearch = search.toLowerCase();
        return Observable.fromArray(
                movies
                        .stream()
                        .filter(item -> item.getCreator().toLowerCase().contains(loweredSearch))
                        .collect(Collectors.toList())
        );
    }

    Observable<List<Movie>> searchMoviesByGenre(String search) {
        String loweredSearch = search.toLowerCase();
        return Observable.fromArray(
                movies
                        .stream()
                        .filter(item -> item.getGenre().toLowerCase().contains(loweredSearch))
                        .collect(Collectors.toList())
        );
    }
}
