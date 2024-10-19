package org.kodelabs.publisher;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class CreatePublisher {
    @NotBlank
    @Pattern(regexp = "[a-zA-Z\\s]+", message = "publishers name can contain only letters and spaces")
    private String name;

    public CreatePublisher() {
    }

    public CreatePublisher(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
