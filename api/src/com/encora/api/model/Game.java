package com.encora.api.model;


public class Game {

    private String code;
    private String title;
    private String genre;
    private int year;

    public Game() {
    }

    public Game(String code, String title, String genre, int year) {
        this.code = code;
        this.title = title;
        this.genre = genre;
        this.year = year;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
