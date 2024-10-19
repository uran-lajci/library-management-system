package org.kodelabs.users;

import org.kodelabs.entities.BaseEntity;

public class User extends BaseEntity {
    public static final transient String FIELD_FIRST_NAME = "firstName";
    public static final transient String FIELD_LAST_NAME = "lastName";
    public static final transient String FIELD_USERNAME = "username";
    public static final transient String FIELD_PASSWORD = "password";
    public static final transient String FIELD_ROLE = "role";
    public static final transient String FIELD_DATE_CREATED = "dateCreated";
    public static final transient String FIELD_PHONE_NUMBER = "phoneNumber";
    public static final transient String FIELD_ADDRESS = "address";

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private Role role;
    private String phoneNumber;
    private String address;

    public User() {}

    public User(String firstName, String lastName, String username, String password, Role role,
                String phoneNumber, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}
