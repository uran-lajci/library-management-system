package org.kodelabs.review;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CreateReview {
    @NotNull
    @Min(1)
    @Max(5)
    private int averageRating;
    @NotBlank
    private String comment;
    @NotBlank
    private String userId;

    public CreateReview() {
    }

    public CreateReview(int averageRating, String comment, String userId) {
        this.averageRating = averageRating;
        this.comment = comment;
        this.userId = userId;
    }

    public int getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(int averageRating) {
        this.averageRating = averageRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
