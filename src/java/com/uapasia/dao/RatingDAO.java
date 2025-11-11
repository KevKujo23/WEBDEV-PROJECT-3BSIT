/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.dao;

/**
 *
 * @author Kevin
 */
import com.uapasia.model.Rating;
import com.uapasia.model.Term;
import java.sql.SQLException;

public interface RatingDAO {
    boolean exists(int userId, int profId, int subjectId, String academicYear, Term term) throws SQLException;
    int insert(Rating r) throws SQLException;
    boolean canEdit(int ratingId, int userId) throws SQLException; // 5-minute window
    int updateCommentAndScore(int ratingId, int userId, int score, String comment) throws SQLException;
}
