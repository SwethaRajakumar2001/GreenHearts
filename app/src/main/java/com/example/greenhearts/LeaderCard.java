package com.example.greenhearts;

public class LeaderCard implements Comparable<LeaderCard>{
    private String user_id;
    private String user_name;
    private int score;
    private int rank;
    private int no_plant;
    private String profile_pic;

    public LeaderCard(int score) {
        this.score = score;
        profile_pic=null;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getNo_plant() {
        return no_plant;
    }

    public void setNo_plant(int no_plant) {
        this.no_plant = no_plant;
    }

    @Override
    public int compareTo(LeaderCard card) {
        int compare=card.getScore();
        return (this.score < compare ? 1 :
                (this.score == compare ? 0 : -1));
    }

}
