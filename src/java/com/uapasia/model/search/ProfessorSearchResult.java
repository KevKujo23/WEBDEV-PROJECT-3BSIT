/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.model.search;

/**
 *
 * @author Kevin
 */
import java.util.*;

public class ProfessorSearchResult {
    private int profId;
    private String fullName;
    private String deptName;
    private double avgRating;
    private int ratingCount;
    private List<String> topTags = new ArrayList<>();
    private String recentCommentSnippet;

    public int getProfId() { return profId; }
    public void setProfId(int profId) { this.profId = profId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }

    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }

    public List<String> getTopTags() { return topTags; }
    public void setTopTags(List<String> topTags) { this.topTags = (topTags == null ? new ArrayList<>() : topTags); }

    public String getRecentCommentSnippet() { return recentCommentSnippet; }
    public void setRecentCommentSnippet(String recentCommentSnippet) { this.recentCommentSnippet = recentCommentSnippet; }
}

