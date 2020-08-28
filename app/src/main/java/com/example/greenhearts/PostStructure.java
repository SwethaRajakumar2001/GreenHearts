package com.example.greenhearts;

public class PostStructure {
    public String image, message, timestamp, user_id, username;
    public int nlikes, ncomment;

    public PostStructure(){}

    public PostStructure(String image, String message, String timestamp, String user_id, String username, int nlikes, int ncomment) {
        this.image = image;
        this.message = message;
        this.timestamp = timestamp;
        this.user_id = user_id;
        this.username = username;
        this.nlikes = nlikes;
        this.ncomment = ncomment;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNlikes() {
        return nlikes;
    }

    public void setNlikes(int nlikes) {
        this.nlikes = nlikes;
    }

    public int getNcomment() {
        return ncomment;
    }

    public void setNcomment(int ncomment) {
        this.ncomment = ncomment;
    }
}
