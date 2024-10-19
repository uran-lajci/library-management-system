package org.kodelabs.book.models;

import org.kodelabs.author.AuthorInfo;

import java.util.Date;
import java.util.List;

public class BookInfoForGrouping {
    private String id;
    private String title;
    private List<AuthorInfo> authors;
    private int numberOfPages;
    private Date datePublished;

    public BookInfoForGrouping() {
    }

    public BookInfoForGrouping(String id, String title, List<AuthorInfo> authors, int numberOfPages, Date datePublished) {
        this.title = title;
        this.authors = authors;
        this.numberOfPages = numberOfPages;
        this.datePublished = datePublished;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<AuthorInfo> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorInfo> authors) {
        this.authors = authors;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(Date datePublished) {
        this.datePublished = datePublished;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
