package com.example.greenhearts;

public class CommentStructure {
    private String user_name;
    private String content;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    private String timestamp;

    public CommentStructure(){}

    public CommentStructure(String user_name, String content, String timestamp) {
        this.user_name = user_name;
        this.content = content;
        this.timestamp=timestamp;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
