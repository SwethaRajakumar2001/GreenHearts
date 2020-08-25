package com.example.greenhearts;

public class PostStructure {
    //private String id, user_id, username, message, image, timestamp;
    private int nlikes, ncomments;

    public PostStructure(){}

    public PostStructure(int nlikes, int ncomments) {
        this.nlikes = nlikes;
        this.ncomments = ncomments;
    }

}
