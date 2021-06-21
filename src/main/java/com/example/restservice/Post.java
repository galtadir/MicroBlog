package com.example.restservice;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class Post implements Comparable<Post> {
    private long id;
    private String text;
    private int likes;
    private LocalDate date;
    private double rank;

    public Post(long id, String text) {
        this.id = id;
        this.text = text;
        this.likes = 0;
        this.date = java.time.LocalDate.now();
        this.rank=0;
    }

    public void updateRank(){
        long daysBetween = DAYS.between(date, java.time.LocalDate.now());
        rank = likes*Math.pow(0.9,daysBetween);
    }

    public void addLike(){
        this.likes++;
        updateRank();
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public double getRank() {
        return rank;
    }

    public int getLikes() {
        return likes;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public int compareTo(Post o) {
        if(this.rank>o.rank){
            return -1;
        }
        return 1;
    }
}
