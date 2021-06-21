package com.example.restservice;

public class UpdateRequest {
    private String text;

    public UpdateRequest(long id, String text) {
        this.text = text;
    }



    public String getText() {
        return text;
    }
}
