package org.kodelabs.users.models;

import java.util.List;

public class UsersByRole {
    private String role;
    private List<UserInfo> user;

    public UsersByRole() {}

    public UsersByRole(String role, List<UserInfo> user) {
        this.role = role;
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<UserInfo> getUser() {
        return user;
    }

    public void setUser(List<UserInfo> user) {
        this.user = user;
    }
}
