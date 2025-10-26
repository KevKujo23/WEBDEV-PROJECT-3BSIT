package com.uapasia.model;

public class Rating {
    private int id;
    private int profId;
    private int score;
    private String comment;
    private String byUser;
    private long createdAt;

    public Rating() {

    }

    public Rating(int id, int profId, int score, String comment, String byUser, long createdAt) {
        this.id = id;
        this.profId = profId;
        this.score = score;
        this.comment = comment;
        this.byUser = byUser;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getProfId() {
        return profId;
    }

    public int getScore() {
        return score;
    }

    public String getComment() {
        return comment;
    }

    public String getByUser() {
        return byUser;
    }

    public long getCreatedAt() {
        return createdAt;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setProfId(int profId) {
        this.profId = profId;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public void setByUser(String byUser) {
        this.byUser = byUser;
    }
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

}
