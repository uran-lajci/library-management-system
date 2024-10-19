package org.kodelabs.book.models;

import org.kodelabs.author.AuthorInfoForGrouping;

import java.util.List;

public class BookInfoForGroupingByAuthorNationality {
    public static final transient String FIELD_TITLE = "title";
    public static final transient String FIELD_AUTHOR_INFO_ID = "authorInfo._id";
    public static final transient String FIELD_AUTHOR_INFO_FIRSTNAME = "authorInfo.firstName";
    public static final transient String FIELD_AUTHOR_INFO_LASTNAME = "authorInfo.lastName";
    public static final transient String FIELD_AUTHOR_INFO_NATIONALITY = "authorInfo.nationality";
    public static final transient String FIELD_AUTHOR_INFO = "authorInfo";

    private String title;
    private List<AuthorInfoForGrouping> authorInfo;

    public BookInfoForGroupingByAuthorNationality() {
    }

    public BookInfoForGroupingByAuthorNationality(String title, List<AuthorInfoForGrouping> authorInfo) {
        this.title = title;
        this.authorInfo = authorInfo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<AuthorInfoForGrouping> getAuthorInfo() {
        return authorInfo;
    }

    public void setAuthorInfo(List<AuthorInfoForGrouping> authorInfo) {
        this.authorInfo = authorInfo;
    }
}
