package com.uapasia.model;

import java.io.Serializable;
import java.time.Instant;

public class Rating implements Serializable {
    private int ratingId;
    private int profId;
    private int userId;
    private int subjectId;
    private String academicYear;  // '2025-2026'
    private Term term;            // 1, 2, Summer (enum with DB mapping)
    private int score;            // 1..5
    private String comment;       // enforce 300-word limit in servlet
    private Instant createdAt;
    private Instant updatedAt;

    public int getRatingId() { return ratingId; }
    public void setRatingId(int ratingId) { this.ratingId = ratingId; }

    public int getProfId() { return profId; }
    public void setProfId(int profId) { this.profId = profId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public Term getTerm() { return term; }
    public void setTerm(Term term) { this.term = term; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
