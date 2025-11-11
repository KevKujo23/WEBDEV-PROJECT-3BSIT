/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.dao.impl;

/**
 *
 * @author Kevin
 */
import com.uapasia.dao.RatingDAO;
import com.uapasia.dao.util.DB;
import com.uapasia.dao.util.EnumConverters;
import com.uapasia.model.Rating;
import com.uapasia.model.Term;

import java.sql.*;
import java.time.Instant;

public class RatingDAOImpl implements RatingDAO {

    @Override
    public boolean exists(int userId, int profId, int subjectId, String academicYear, Term term) throws SQLException {
        String sql = "SELECT 1 FROM ratings WHERE user_id=? AND prof_id=? AND subject_id=? AND academic_year=? AND term=? LIMIT 1";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, profId);
            ps.setInt(3, subjectId);
            ps.setString(4, academicYear);
            ps.setString(5, EnumConverters.termToDb(term));
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    @Override
    public int insert(Rating r) throws SQLException {
        String sql = "INSERT INTO ratings (prof_id, user_id, subject_id, academic_year, term, score, comment) " +
                     "VALUES (?,?,?,?,?,?,?)";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getProfId());
            ps.setInt(2, r.getUserId());
            ps.setInt(3, r.getSubjectId());
            ps.setString(4, r.getAcademicYear());
            ps.setString(5, EnumConverters.termToDb(r.getTerm()));
            ps.setInt(6, r.getScore());
            ps.setString(7, r.getComment());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public boolean canEdit(int ratingId, int userId) throws SQLException {
        // user must own it AND within 5 minutes of creation
        String sql = "SELECT TIMESTAMPDIFF(MINUTE, created_at, NOW()) AS mins FROM ratings WHERE rating_id=? AND user_id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, ratingId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                int mins = rs.getInt("mins");
                return mins <= 5; // allow edit up to 5 minutes
            }
        }
    }

    @Override
    public int updateCommentAndScore(int ratingId, int userId, int score, String comment) throws SQLException {
        // Enforce window here too (defense in depth)
        String sql = "UPDATE ratings " +
                     "SET score=?, comment=?, updated_at=NOW() " +
                     "WHERE rating_id=? AND user_id=? AND TIMESTAMPDIFF(MINUTE, created_at, NOW()) <= 5";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, score);
            ps.setString(2, comment);
            ps.setInt(3, ratingId);
            ps.setInt(4, userId);
            return ps.executeUpdate(); // 1 if ok, 0 if not allowed/not found
        }
    }
}

