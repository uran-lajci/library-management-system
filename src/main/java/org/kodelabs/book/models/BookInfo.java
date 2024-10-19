package org.kodelabs.book.models;

public class BookInfo {
    private String id;
    private String title;
    private int count;
    
    public BookInfo() {
    }

    public BookInfo(String id, String title, int count) {
        this.id = id;
        this.title = title;
        this.count = count;
    }

    public BookInfo(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
