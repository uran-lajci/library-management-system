package org.kodelabs.publisher;

public class PublisherInfo {
    private String id;
    private String name;

    public PublisherInfo() {
    }

    public PublisherInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
