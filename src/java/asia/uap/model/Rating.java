/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asia.uap.model;

/**
 *
 * @author Alexander
 */
public class Rating {

    private int ratingId, userId, profSubjectId, clarity, fairness, engagement, knowledge;
    private String comment;

    public int getRatingId() {
        return ratingId;
    }

    public void setRatingId(int v) {
        ratingId = v;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int v) {
        userId = v;
    }

    public int getProfSubjectId() {
        return profSubjectId;
    }

    public void setProfSubjectId(int v) {
        profSubjectId = v;
    }

    public int getClarity() {
        return clarity;
    }

    public void setClarity(int v) {
        clarity = v;
    }

    public int getFairness() {
        return fairness;
    }

    public void setFairness(int v) {
        fairness = v;
    }

    public int getEngagement() {
        return engagement;
    }

    public void setEngagement(int v) {
        engagement = v;
    }

    public int getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(int v) {
        knowledge = v;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String v) {
        comment = v;
    }
}
