package com.example.restservice;

public class UpdateRequest {
    private long id;
    private String text;

    public UpdateRequest(long id, String text) {
        this.id = id;
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
