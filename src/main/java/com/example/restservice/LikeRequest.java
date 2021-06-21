package com.example.restservice;

public class LikeRequest {
    private long id;

    public LikeRequest(long id, String text) {
        this.id = id;
    }


    public long getId() {
        return id;
    }
}
