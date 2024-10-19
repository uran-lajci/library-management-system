package org.kodelabs.author;

import org.kodelabs.book.models.BookInfo;
import org.kodelabs.entities.BaseEntity;

import java.time.LocalDate;
import java.util.List;

public class Author extends BaseEntity {
    public static final transient String FIELD_FIRST_NAME = "firstName";
    public static final transient String FIELD_LAST_NAME = "lastName";
    public static final transient String FIELD_BOOKS_WRITTEN = "booksWritten";
    public static final transient String FIELD_BOOKS_WRITTEN_ID = "booksWritten._id";
    public static final transient String FIELD_BOOKS_WRITTEN_TITLE = "booksWritten.title";
    public static final transient String FIELD_BORN = "born";
    public static final transient String FIELD_DIED = "died";
    public static final transient String FIELD_PLACE_BORN = "placeBorn";
    public static final transient String FIELD_PLACE_DIED = "placeDied";
    public static final transient String FIELD_NATIONALITY = "nationality";
    public static final transient String FIELD_CURRENT_PROFESSION = "currentProfession";

    private String firstName;
    private String lastName;
    private List<BookInfo> booksWritten;
    private LocalDate born;
    private LocalDate died;
    private String placeBorn;
    private String placeDied;
    private Nationality nationality;
    private String currentProfession;

    public Author() {
    }

    public Author(String firstName, String lastName, List<BookInfo> booksWritten, LocalDate born, LocalDate died, String placeBorn,
                  String placeDied, Nationality nationality, String currentProfession) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.booksWritten = booksWritten;
        this.born = born;
        this.died = died;
        this.placeBorn = placeBorn;
        this.placeDied = placeDied;
        this.nationality = nationality;
        this.currentProfession = currentProfession;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<BookInfo> getBooksWritten() {
        return booksWritten;
    }

    public void setBooksWritten(List<BookInfo> booksWritten) {
        this.booksWritten = booksWritten;
    }

    public LocalDate getBorn() {
        return born;
    }

    public void setBorn(LocalDate born) {
        this.born = born;
    }

    public LocalDate getDied() {
        return died;
    }

    public void setDied(LocalDate died) {
        this.died = died;
    }

    public String getPlaceBorn() {
        return placeBorn;
    }

    public void setPlaceBorn(String placeBorn) {
        this.placeBorn = placeBorn;
    }

    public String getPlaceDied() {
        return placeDied;
    }

    public void setPlaceDied(String placeDied) {
        this.placeDied = placeDied;
    }

    public Nationality getNationality() {
        return nationality;
    }

    public void setNationality(Nationality nationality) {
        this.nationality = nationality;
    }

    public String getCurrentProfession() {
        return currentProfession;
    }

    public void setCurrentProfession(String currentProfession) {
        this.currentProfession = currentProfession;
    }
}
