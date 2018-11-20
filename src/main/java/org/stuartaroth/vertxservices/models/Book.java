package org.stuartaroth.vertxservices.models;

public class Book implements Media {
    private String title;
    private String author;
    private String genre;

    public Book(String title, String author, String genre) {
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getCreator() {
        return author;
    }

    @Override
    public String getGenre() {
        return genre;
    }
}
