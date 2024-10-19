package org.kodelabs.borrow;

import org.kodelabs.book.Genre;
import org.kodelabs.book.Languages;

import java.util.List;

public class BookInfoAndCount {
    private String id;
    private String title;
    private List<Genre> genres;
    private List<Languages> languages;
    private int count;

    public BookInfoAndCount() {
    }

    public BookInfoAndCount(String id, String title, List<Genre> genres, List<Languages> languages, int count) {
        this.id = id;
        this.title = title;
        this.genres = genres;
        this.languages = languages;
        this.count = count;
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

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Languages> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Languages> languages) {
        this.languages = languages;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
