/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.dao.impl;

/**
 *
 * @author Kevin
 */
import com.uapasia.dao.SubjectDAO;
import com.uapasia.dao.util.DB;
import com.uapasia.model.Subject;

import java.sql.*;
import java.util.*;

public class SubjectDAOImpl implements SubjectDAO {

    @Override
    public List<Subject> listAll() throws SQLException {
        String sql = "SELECT subject_id, code, title FROM subjects ORDER BY code";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Subject> out = new ArrayList<>();
            while (rs.next()) {
                Subject s = new Subject();
                s.setSubjectId(rs.getInt("subject_id"));
                s.setCode(rs.getString("code"));
                s.setTitle(rs.getString("title"));
                out.add(s);
            }
            return out;
        }
    }

    @Override
    public Subject findById(int subjectId) throws SQLException {
        String sql = "SELECT subject_id, code, title FROM subjects WHERE subject_id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Subject s = new Subject();
                s.setSubjectId(rs.getInt("subject_id"));
                s.setCode(rs.getString("code"));
                s.setTitle(rs.getString("title"));
                return s;
            }
        }
    }
}

