package org.kodelabs.users;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class UpdateUser {
    @NotBlank
    @Pattern(regexp = "[a-zA-Z]+", message = "can contain only letters")
    private String firstName;
    @NotBlank
    @Pattern(regexp = "[a-zA-Z]+", message = "can contain only letters")
    private String lastName;
    @NotBlank
    @Pattern(regexp = "[0-9a-zA-Z]+", message = "can contain only letters and numbers")
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String address;

    public UpdateUser(String firstName, String lastName, String username, String password, String phoneNumber, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
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

}
