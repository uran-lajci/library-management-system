package org.kodelabs.book.models;

import org.kodelabs.users.models.UserInfo;

public class CommentAndUserInfo {
    private String comment;
    private UserInfo userInfo;

    public CommentAndUserInfo() {
    }

    public CommentAndUserInfo(String comment, UserInfo userInfo) {
        this.comment = comment;
        this.userInfo = userInfo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
