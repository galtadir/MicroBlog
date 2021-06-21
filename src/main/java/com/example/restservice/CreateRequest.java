package com.example.restservice;

public class CreateRequest {
    private String text;

    public CreateRequest(long id, String text) {
        this.text = text;
    }


    public String getText() {
        return text;
    }
}
