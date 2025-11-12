/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.dao.impl;

/**
 *
 * @author Kevin
 */
import com.uapasia.dao.UserDAO;
import com.uapasia.dao.util.DB;
import com.uapasia.dao.util.EnumConverters;
import com.uapasia.model.Role;
import com.uapasia.model.User;

import java.sql.*;
import java.time.Instant;

public class UserDAOImpl implements UserDAO {

    @Override
    public User findByLoginAndPassword(String login, String password) throws SQLException {
        String sql = "SELECT user_id, student_number, email, password, full_name, year_level, dept_id, role, created_at " +
                     "FROM users WHERE (email=? OR student_number=?) AND password=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, login);
            ps.setString(3, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email=? LIMIT 1";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    @Override
    public boolean studentNumberExists(String studentNumber) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE student_number=? LIMIT 1";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, studentNumber);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    @Override
    public int create(User u) throws SQLException {
        String sql = "INSERT INTO users(student_number,email,password,full_name,year_level,dept_id,role) " +
                     "VALUES (?,?,?,?,?,?,?)";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getStudentNumber());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword()); // plain-text per class requirement
            ps.setString(4, u.getFullName());
            ps.setInt(5, u.getYearLevel());
            ps.setInt(6, u.getDeptId());
            ps.setString(7, EnumConverters.roleToDb(u.getRole()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    u.setUserId(id);
                    return id;
                }
            }
        }
        return 0;
    }

    @Override
    public User findById(int userId) throws SQLException {
        String sql = "SELECT user_id, student_number, email, password, full_name, year_level, dept_id, role, created_at " +
                     "FROM users WHERE user_id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    private User map(ResultSet rs) throws SQLException {
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

