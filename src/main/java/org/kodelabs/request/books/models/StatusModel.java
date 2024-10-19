package org.kodelabs.request.books.models;

import org.kodelabs.request.books.Status;

import javax.validation.constraints.NotNull;

public class StatusModel {
    @NotNull
    private Status status;

    public StatusModel() {}

    public StatusModel(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
