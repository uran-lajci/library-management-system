package org.kodelabs.request.books;

import org.kodelabs.entities.BaseEntity;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreateRequest extends BaseEntity {
    @NotBlank
    private String bookId;
    @NotBlank
    private String userId;
    @NotNull
    @FutureOrPresent
    private LocalDate startDate;
    @NotNull
    @Future
    private LocalDate endDate;

    public CreateRequest() {}

    public CreateRequest(String bookId, String userId, LocalDate startDate, LocalDate endDate) {
        this.bookId = bookId;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
