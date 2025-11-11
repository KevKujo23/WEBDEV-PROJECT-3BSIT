/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.dao.impl;

/**
 *
 * @author Kevin
 */
import com.uapasia.dao.ProfessorDAO;
import com.uapasia.dao.util.DB;
import com.uapasia.model.Professor;

import java.sql.*;
import java.time.Instant;
import java.util.*;

public class ProfessorDAOImpl implements ProfessorDAO {

    @Override
    public Professor findById(int profId) throws SQLException {
        String sql = "SELECT prof_id, first_name, last_name, dept_id, created_at FROM professors WHERE prof_id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, profId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Professor p = new Professor();
                p.setProfId(rs.getInt("prof_id"));
                p.setFirstName(rs.getString("first_name"));
                p.setLastName(rs.getString("last_name"));
                p.setDeptId(rs.getInt("dept_id"));
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) p.setCreatedAt(ts.toInstant());
                return p;
            }
        }
    }

    @Override
    public List<Professor> listByDepartment(int deptId) throws SQLException {
        String sql = "SELECT prof_id, first_name, last_name, dept_id, created_at FROM professors WHERE dept_id=? ORDER BY last_name, first_name";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Professor> out = new ArrayList<>();
                while (rs.next()) {
                    Professor p = new Professor();
                    p.setProfId(rs.getInt("prof_id"));
                    p.setFirstName(rs.getString("first_name"));
                    p.setLastName(rs.getString("last_name"));
                    p.setDeptId(rs.getInt("dept_id"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) p.setCreatedAt(ts.toInstant());
                    out.add(p);
                }
                return out;
            }
        }
    }
}

