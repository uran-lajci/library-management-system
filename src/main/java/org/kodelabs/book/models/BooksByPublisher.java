package org.kodelabs.book.models;

import java.util.List;

public class BooksByPublisher {
    private String publisher;
    private List<BookInfoForGrouping> books;

    public BooksByPublisher() {
    }

    public BooksByPublisher(String publisher, List<BookInfoForGrouping> books) {
        this.publisher = publisher;
        this.books = books;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public List<BookInfoForGrouping> getBooks() {
        return books;
    }

    public void setBooks(List<BookInfoForGrouping> books) {
        this.books = books;
    }
}
