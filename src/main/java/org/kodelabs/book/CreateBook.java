package org.kodelabs.book;

import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

public class CreateBook {
    @NotBlank
    @Pattern(regexp = "[0-9a-zA-Z\\s]+", message = "title can contain only letters and numbers")
    private String title;
    @PastOrPresent
    private LocalDate datePublished;
    @Positive
    private int numberOfPages;
    @NotEmpty
    @UniqueElements
    private Set<@NotNull Genre> genres;
    @NotNull
    @NotEmpty
    @UniqueElements
    private Set<@NotNull Languages> languages;
    @NotBlank
    private String publisherId;
    @NotEmpty
    @UniqueElements
    private Set<@NotNull String> authorIds;
    @Positive
    private int numberOfBooksAvailable;

    public CreateBook() {
    }

    public CreateBook(String title, LocalDate datePublished, int numberOfPages, Set<Genre> genres, Set<Languages> languages,
                      String publisherId, Set<String> authorIds, int numberOfBooksAvailable) {
        this.title = title;
        this.datePublished = datePublished;
        this.numberOfPages = numberOfPages;
        this.genres = genres;
        this.languages = languages;
        this.publisherId = publisherId;
        this.authorIds = authorIds;
        this.numberOfBooksAvailable =  numberOfBooksAvailable;
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

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public Set<String> getAuthors() {
        return authorIds;
    }

    public void setAuthors(Set<String> authorId) {
        this.authorIds = authorId;
    }



}
