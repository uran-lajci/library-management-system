package org.kodelabs.book.models;

import java.util.List;

public class BooksByGenre {
    private String genres;
    private List<BookInfoForGrouping> books;

    public BooksByGenre() {}

    public BooksByGenre(String genres, List<BookInfoForGrouping> books) {
        this.genres = genres;
        this.books = books;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public List<BookInfoForGrouping> getBooks() {
        return books;
    }

    public void setBooks(List<BookInfoForGrouping> books) {
        this.books = books;
    }
}
