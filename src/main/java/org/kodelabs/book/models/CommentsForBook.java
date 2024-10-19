package org.kodelabs.book.models;

import java.util.List;

public class CommentsForBook {

    private List<CommentAndUserInfo> comments;

    public CommentsForBook() {
    }

    public CommentsForBook(List<CommentAndUserInfo> comments) {
        this.comments = comments;
    }

    public List<CommentAndUserInfo> getComments() {
        return comments;
    }

    public void setComments(List<CommentAndUserInfo> comments) {
        this.comments = comments;
    }
}
