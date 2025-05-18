package com.example.myapplication.model;

public class Tile {
    private String name;
    private String description;
    private String picture;
    private Long sizex;
    private Long sizey;

    public Tile() {
    }

    public Tile(String name, String description, String picture, Long sizex, Long sizey) {
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.sizex = sizex;
        this.sizey = sizey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Long getSizex() {
        return sizex;
    }

    public void setSizex(Long sizex) {
        this.sizex = sizex;
    }

    public Long getSizey() {
        return sizey;
    }

    public void setSizey(Long sizey) {
        this.sizey = sizey;
    }
}
