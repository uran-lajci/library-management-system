package org.kodelabs.publisher;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class UpdatePublisher {
    @NotBlank
    @Pattern(regexp = "[a-zA-Z\\s]+", message = "publishers name can contain only letters and spaces")
    private String name;

    public UpdatePublisher() {
    }

    public UpdatePublisher(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
