package org.kodelabs.borrow;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreateBorrow {
    @NotBlank
    private String internalUserId;
    @NotBlank
    private String externalUserId;
    @NotBlank
    private String bookId;
    @NotNull
    private LocalDate startDate;
    @NotNull
    @Future
    private LocalDate endDate;

    public CreateBorrow() {}

    public CreateBorrow(String internalUserId, String externalUserId, String bookId, LocalDate startDate, LocalDate endDate) {
        this.internalUserId = internalUserId;
        this.externalUserId = externalUserId;
        this.bookId = bookId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getInternalUserId() {
        return internalUserId;
    }

    public void setInternalUserId(String internalUserId) {
        this.internalUserId = internalUserId;
    }

    public String getExternalUserId() {
        return externalUserId;
    }

    public void setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
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
