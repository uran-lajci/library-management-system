package org.kodelabs.request.books.models;

import java.util.List;

public class IdsOfRequestedBooksFromUser {
    private List<String> ids;

    public IdsOfRequestedBooksFromUser() {
    }

    public IdsOfRequestedBooksFromUser(List<String> ids) {
        this.ids = ids;
    }

    public List<String> getBooks() {
        return ids;
    }

    public void setBooks(List<String> booksIds) {
        this.ids = booksIds;
    }
}
