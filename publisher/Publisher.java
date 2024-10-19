package org.kodelabs.publisher;

import org.kodelabs.entities.BaseEntity;
import org.kodelabs.users.models.UserInfo;

public class Publisher extends BaseEntity {
    public static final transient String FIELD_NAME = "name";
    public static final transient String FIELD_USER = "createdBy";
    public static final transient String FIELD_USER_USER_ID = "createdBy._id";
    public static final transient String FIELD_USER_FIRSTNAME = "createdBy.firstName";
    public static final transient String FIELD_USER_LASTNAME = "createdBy.lastName";
    public static final transient String FIELD_NUMBER_OF_BOOKS = "numberOfBooks";

    private String name;
    private UserInfo createdBy;
    private int numberOfBooks;

    public Publisher() {
    }

    public Publisher(String name, UserInfo createdBy, int numberOfBooks) {
        this.name = name;
        this.createdBy = createdBy;
        this.numberOfBooks = numberOfBooks;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfBooks() {
        return numberOfBooks;
    }

    public void setNumberOfBooks(int numberOfBooks) {
        this.numberOfBooks = numberOfBooks;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserInfo getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserInfo createdBy) {
        this.createdBy = createdBy;
    }
}
