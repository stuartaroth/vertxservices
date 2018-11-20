package org.stuartaroth.vertxservices.models;

public class Movie implements Media {
    private String title;
    private String director;
    private String genre;

    public Movie(String title, String director, String genre) {
        this.title = title;
        this.director = director;
        this.genre = genre;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getCreator() {
        return director;
    }

    @Override
    public String getGenre() {
        return genre;
    }
}
