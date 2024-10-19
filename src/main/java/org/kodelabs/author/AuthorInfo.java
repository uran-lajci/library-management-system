package org.kodelabs.author;

import java.util.List;
import java.util.stream.Collectors;

public class AuthorInfo {
    private String id;
    private String firstName;
    private String lastName;

    public AuthorInfo() {
    }

    public AuthorInfo(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static List<AuthorInfo> mapToListOfAuthorInfo(List<Author> authors) {
        return authors.stream().map(author -> new AuthorInfo(author._id, author.getFirstName(), author.getLastName()))
                .collect(Collectors.toList());
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

    public String getId() {
        return id;
    }

    public void setId(String bookId) {
        this.id = bookId;
    }
}
