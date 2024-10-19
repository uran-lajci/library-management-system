package org.kodelabs.borrow;

import org.kodelabs.book.models.BookInfo;
import org.kodelabs.entities.BaseEntity;
import org.kodelabs.users.models.UserInfo;

import java.time.LocalDate;

public class Borrow extends BaseEntity {
    public static final transient String FIELD_BOOK = "book";
    public static final transient String FIELD_BOOK_ID = "book._id";
    public static final transient String FIELD_USER = "user";
    public static final transient String FIELD_USER_ID = "user._id";
    public static final transient String FIELD_START_DATE = "startDate";
    public static final transient String FIELD_END_DATE = "endDate";
    public static final transient String FIELD_STATE = "state";

    private BookInfo book;
    private UserInfo user;
    private LocalDate startDate;
    private LocalDate endDate;
    private State state;

    public Borrow() {}

    public Borrow(BookInfo book, UserInfo user, LocalDate startDate, LocalDate endDate, State state) {
        this.book = book;
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.state = state;
    }

    public BookInfo getBook() {
        return book;
    }

    public void setBook(BookInfo book) {
        this.book = book;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
