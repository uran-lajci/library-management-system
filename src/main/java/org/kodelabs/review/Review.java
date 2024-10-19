package org.kodelabs.review;

import org.kodelabs.book.models.BookInfo;
import org.kodelabs.entities.BaseEntity;
import org.kodelabs.users.models.UserInfo;

public class Review extends BaseEntity {
    public static final transient String FIELD_ID = "averageRating";
    public static final transient String FIELD_COMMENT = "comment";
    public static final transient String FIELD_BOOK = "book";
    public static final transient String FIELD_BOOK_TITLE = "book.title";
    public static final transient String FIELD_BOOK_ID = "book._id";
    public static final transient String FIELD_USER = "user";
    public static final transient String FIELD_USER_ID = "user._id";
    public static final transient String FIELD_USER_FIRSTNAME = "user.firstName";
    public static final transient String FIELD_USER_LASTNAME = "user.lastName";

    private int averageRating;
    private String comment;
    private BookInfo book;
    private UserInfo user;

    public Review() {
    }

    public Review(int averageRating, String comment, BookInfo book, UserInfo user) {
        this.averageRating = averageRating;
        this.comment = comment;
        this.book = book;
        this.user = user;
    }

    public int getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(int averageRating) {
        this.averageRating = averageRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
