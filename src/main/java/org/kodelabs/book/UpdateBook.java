package org.kodelabs.book;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Set;

public class UpdateBook {
    @Pattern(regexp = "[a-zA-Z\\s]+", message = "can contain only letters")
    private String title;
    @PastOrPresent
    private LocalDate datePublished;
    @Positive
    private int numberOfPages;
    @Positive
    private int numberOfBooksAvailable;
    @NotEmpty
    private String publisherId;
    private Set<Genre> genres;
    private Set<Languages> languages;

    public UpdateBook(String title, LocalDate datePublished, int numberOfPages, int numberOfBooksAvailable, String publisherId, Set<Genre> genres, Set<Languages> languages) {
        this.title = title;
        this.datePublished = datePublished;
        this.numberOfPages = numberOfPages;
        this.numberOfBooksAvailable = numberOfBooksAvailable;
        this.publisherId = publisherId;
        this.genres = genres;
        this.languages = languages;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public int getNumberOfBooksAvailable() {
        return numberOfBooksAvailable;
    }

    public void setNumberOfBooksAvailable(int numberOfBooksAvailable) {
        this.numberOfBooksAvailable = numberOfBooksAvailable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(LocalDate datePublished) {
        this.datePublished = datePublished;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public Set<Languages> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<Languages> languages) {
        this.languages = languages;
    }

}
