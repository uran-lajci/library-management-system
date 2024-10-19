package org.kodelabs.author;

import javax.validation.constraints.*;
import java.time.LocalDate;

public class CreateAuthor {
    @NotBlank
    @Pattern(regexp = "[0-9a-zA-Z]+", message = "can contain only letters")
    private String firstName;
    @NotBlank
    @Pattern(regexp = "[a-zA-Z]+", message = "can contain only letters")
    private String lastName;
    @NotNull
    @PastOrPresent
    private LocalDate born;
    @Past
    private LocalDate died;
    @NotBlank
    private String placeBorn;
    private String placeDied;
    @NotNull(message = "Nationality should be one of the enum values")
    private Nationality nationality;
    @NotBlank
    private String currentProfession;

    public CreateAuthor() {
    }

    public CreateAuthor(String firstName, String lastName, LocalDate born, LocalDate died, String placeBorn, String placeDied,
                        Nationality nationality, String currentProfession) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.born = born;
        this.died = died;
        this.placeBorn = placeBorn;
        this.placeDied = placeDied;
        this.nationality = nationality;
        this.currentProfession = currentProfession;
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

    public LocalDate getBorn() {
        return born;
    }

    public void setBorn(LocalDate born) {
        this.born = born;
    }

    public LocalDate getDied() {
        return died;
    }

    public void setDied(LocalDate died) {
        this.died = died;
    }

    public String getPlaceBorn() {
        return placeBorn;
    }

    public void setPlaceBorn(String placeBorn) {
        this.placeBorn = placeBorn;
    }

    public String getPlaceDied() {
        return placeDied;
    }

    public void setPlaceDied(String placeDied) {
        this.placeDied = placeDied;
    }

    public Nationality getNationality() {
        return nationality;
    }

    public void setNationality(Nationality nationality) {
        this.nationality = nationality;
    }

    public String getCurrentProfession() {
        return currentProfession;
    }

    public void setCurrentProfession(String currentProfession) {
        this.currentProfession = currentProfession;
    }
}
