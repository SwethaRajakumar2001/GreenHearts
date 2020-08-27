package com.example.greenhearts;

public class Plants {
    private String date;
    private String name;
    private String photoUrl;

    public Plants() {
    }

    public Plants(String text, String name, String photoUrl) {
        this.date = text;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getText() {
        return date;
    }

    public void setText(String text) {
        this.date = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
