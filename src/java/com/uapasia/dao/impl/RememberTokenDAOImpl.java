/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.dao.impl;

/**
 *
 * @author Kevin
 */
import com.uapasia.dao.RememberTokenDAO;
import com.uapasia.dao.util.DB;
import com.uapasia.dao.util.EnumConverters;
import com.uapasia.model.User;

import java.sql.*;

public class RememberTokenDAOImpl implements RememberTokenDAO {

    @Override
    public void insert(int userId, String token) throws SQLException {
        String sql = "INSERT INTO remember_tokens(user_id, token) VALUES(?,?)";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, token);
            ps.executeUpdate();
        }
    }

    @Override
    public User findUserByToken(String token) throws SQLException {
        String sql = "SELECT u.user_id, u.student_number, u.email, u.password, u.full_name, u.year_level, u.dept_id, u.role, u.created_at " +
                     "FROM remember_tokens t JOIN users u ON u.user_id = t.user_id WHERE t.token=? LIMIT 1";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setStudentNumber(rs.getString("student_number"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setFullName(rs.getString("full_name"));
                u.setYearLevel(rs.getInt("year_level"));
                u.setDeptId(rs.getInt("dept_id"));
                u.setRole(EnumConverters.roleFromDb(rs.getString("role")));
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) u.setCreatedAt(ts.toInstant());
                return u;
            }
        }
    }

    @Override
    public void delete(String token) throws SQLException {
        String sql = "DELETE FROM remember_tokens WHERE token=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.executeUpdate();
        }
    }
}

