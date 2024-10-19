package org.kodelabs.borrow;

import java.util.List;

public class IdsOfBorrowedBooksOfUser {
    private List<String> ids;

    public IdsOfBorrowedBooksOfUser() {
    }

    public IdsOfBorrowedBooksOfUser(List<String> ids) {
        this.ids = ids;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
