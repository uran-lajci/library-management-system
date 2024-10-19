package org.kodelabs.book;

import org.kodelabs.author.AuthorInfo;
import org.kodelabs.book.models.CommentAndUserInfo;
import org.kodelabs.entities.BaseEntity;
import org.kodelabs.publisher.PublisherInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class Book extends BaseEntity {
    public static final transient String FIELD_TITLE = "title";
    public static final transient String FIELD_DATE_PUBLISHED = "datePublished";
    public static final transient String FIELD_GENRES = "genres";
    public static final transient String FIELD_LANGUAGES = "languages";
    public static final transient String FIELD_NUMBER_OF_PAGES = "numberOfPages";
    public static final transient String FIELD_PUBLISHER = "publisher";
    public static final transient String FIELD_PUBLISHER_ID = "publisher._id";
    public static final transient String FIELD_PUBLISHER_NAME = "publisher.name";
    public static final transient String FIELD_AUTHORS = "authors";
    public static final transient String FIELD_AUTHORS_AUTHOR_ID = "authors._id";
    public static final transient String FIELD_AUTHORS_FIRST_NAME = "authors.firstName";
    public static final transient String FIELD_AUTHORS_LAST_NAME = "authors.lastName";
    public static final transient String FIELD_NUMBER_OF_BOOKS_AVAILABLE = "numberOfBooksAvailable";
    public static final transient String FIELD_NUMBER_OF_COPIES = "numberOfCopies;";
    public static final transient String FIELD_RATING = "averageRating";
    public static final transient String FIELD_COMMENTS = "comments";

    private String title;
    private LocalDate datePublished;
    private Set<Genre> genres;
    private Set<Languages> languages;
    private int numberOfPages;
    private PublisherInfo publisher;
    private List<AuthorInfo> authors;
    private int numberOfBooksAvailable;
    private int numberOfCopies;
    private double averageRating;
    private List<CommentAndUserInfo> comments;

    public Book() {
    }

    public Book(String title, LocalDate datePublished, int numberOfPages, Set<Genre> genres, Set<Languages> languages, PublisherInfo publisher,
                List<AuthorInfo> authors, int numberOfBooksAvailable, double averageRating, List<CommentAndUserInfo> comments, int numberOfCopies) {
        this.title = title;
        this.datePublished = datePublished;
        this.genres = genres;
        this.languages = languages;
        this.numberOfPages = numberOfPages;
        this.publisher = publisher;
        this.authors = authors;
        this.numberOfBooksAvailable = numberOfBooksAvailable;
        this.averageRating = averageRating;
        this.comments = comments;
        this.numberOfCopies = numberOfCopies;
    }

    public static Book mapToBook(PublisherInfo publisherInfo, CreateBook createBook, List<AuthorInfo> authors) {
        return new Book(
                createBook.getTitle(), createBook.getDatePublished(), createBook.getNumberOfPages(),
                createBook.getGenres(), createBook.getLanguages(), publisherInfo,
                authors, createBook.getNumberOfBooksAvailable(),0.0, List.of(), createBook.getNumberOfBooksAvailable()
        );
    }

    public int getNumberOfCopies() {
        return numberOfCopies;
    }

    public void setNumberOfCopies(int numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public List<CommentAndUserInfo> getComments() {
        return comments;
    }

    public void setComments(List<CommentAndUserInfo> comments) {
        this.comments = comments;
    }

    public int getNumberOfBooksAvailable() {
        return numberOfBooksAvailable;
    }

    public void setNumberOfBooksAvailable(int numberOfBooksAvailable) {
        this.numberOfBooksAvailable = numberOfBooksAvailable;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
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

    public List<AuthorInfo> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorInfo> authors) {
        this.authors = authors;
    }

    public Set<Languages> getLanguages() {
        return languages;
    }

    public void setLanguages (Set<Languages> languages) {
        this.languages = languages;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public PublisherInfo getPublisher() {
        return publisher;
    }

    public void setPublisher(PublisherInfo publisher) {
        this.publisher = publisher;
    }

}
