package org.kodelabs.book.models;

import java.util.List;

public class GenresOfBooks {
    private List<String> genres;

    public GenresOfBooks() {
    }

    public GenresOfBooks(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}
