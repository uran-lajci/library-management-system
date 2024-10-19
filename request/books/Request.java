package org.kodelabs.request.books;

import org.kodelabs.book.models.BookInfo;
import org.kodelabs.entities.BaseEntity;
import org.kodelabs.users.models.UserInfo;

import java.time.LocalDate;

public class Request extends BaseEntity {
    public static final transient String FIELD_START_DATE = "startDate";
    public static final transient String FIELD_END_DATE = "endDate";
    public static final transient String FIELD_STATUS = "status";
    public static final transient String FIELD_BOOK = "book";
    public static final transient String FIELD_BOOK_TITLE = "book.title";
    public static final transient String FIELD_BOOK_ID = "book._id";
    public static final transient String FIELD_USER = "user";
    public static final transient String FIELD_USER_ID = "user._id";
    public static final transient String FIELD_USER_FIRSTNAME = "user.firstName";
    public static final transient String FIELD_USER_LASTNAME = "user.lastName";

    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;
    private BookInfo book;
    private UserInfo user;

    public Request() {}

    public Request(LocalDate startDate, LocalDate endDate, Status status, BookInfo book, UserInfo user) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.book = book;
        this.user = user;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
}
