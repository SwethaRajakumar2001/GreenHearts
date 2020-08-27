package com.example.greenhearts;

public class ChatMessage {

    private String user_id;
    private String username;
    private String text;
    private String photo_url;
    private String nlikes;
    private String time_stamp;

    public ChatMessage(String user_id, String username, String text, String photo_url, String nlikes, String time_stamp) {
        this.user_id = user_id;
        this.username = username;
        this.text = text;
        this.photo_url = photo_url;
        this.nlikes=nlikes;
        this.time_stamp = time_stamp;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNlikes() {
        return nlikes;
    }

    public void setNlikes(String nlikes) {
        this.nlikes = nlikes;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

}
