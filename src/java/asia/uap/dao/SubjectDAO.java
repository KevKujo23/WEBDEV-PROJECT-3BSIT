/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asia.uap.dao;

/**
 *
 * @author Alexander
 */
import java.sql.*;
import java.util.*;

public class SubjectDAO {

    public java.util.List<java.util.Map<String, Object>> listAll() throws SQLException {
        String sql = "SELECT subject_id, subject_code, subject_name FROM Subjects ORDER BY subject_code";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            java.util.List<java.util.Map<String, Object>> out = new java.util.ArrayList<>();
            while (rs.next()) {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("subjectId", rs.getInt("subject_id"));
                m.put("subjectCode", rs.getString("subject_code"));
                m.put("subjectName", rs.getString("subject_name"));
                out.add(m);
            }
            return out;
        }
    }

    public java.util.List<java.util.Map<String, Object>> listAllForAdmin() throws SQLException {
        String sql = "SELECT s.subject_id, s.subject_code, s.subject_name, d.dept_code, s.dept_id "
                + "FROM Subjects s JOIN Departments d ON s.dept_id=d.dept_id "
                + "ORDER BY s.subject_code";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            java.util.List<java.util.Map<String, Object>> out = new java.util.ArrayList<>();
            while (rs.next()) {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("subjectId", rs.getInt("subject_id"));
                m.put("subjectCode", rs.getString("subject_code"));
                m.put("subjectName", rs.getString("subject_name"));
                m.put("deptCode", rs.getString("dept_code"));
                m.put("deptId", rs.getInt("dept_id"));
                out.add(m);
            }
            return out;
        }
    }

    public void createSubject(int deptId, String code, String name) throws SQLException {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(
                "INSERT INTO Subjects (dept_id, subject_code, subject_name) VALUES (?,?,?)")) {
            ps.setInt(1, deptId);
            ps.setString(2, code);
            ps.setString(3, name);
            ps.executeUpdate();
        }
    }

    public void updateSubject(int subjectId, int deptId, String code, String name) throws SQLException {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(
                "UPDATE Subjects SET dept_id=?, subject_code=?, subject_name=? WHERE subject_id=?")) {
            ps.setInt(1, deptId);
            ps.setString(2, code);
            ps.setString(3, name);
            ps.setInt(4, subjectId);
            ps.executeUpdate();
        }
    }

    public void deleteSubject(int subjectId) throws SQLException {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(
                "DELETE FROM Subjects WHERE subject_id=?")) {
            ps.setInt(1, subjectId);
            ps.executeUpdate();
        }
    }

    public java.util.Map<String, Object> find(int subjectId) throws SQLException {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(
                "SELECT subject_id, dept_id, subject_code, subject_name FROM Subjects WHERE subject_id=?")) {
            ps.setInt(1, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("subjectId", rs.getInt("subject_id"));
                m.put("deptId", rs.getInt("dept_id"));
                m.put("subjectCode", rs.getString("subject_code"));
                m.put("subjectName", rs.getString("subject_name"));
                return m;
            }
        }
    }
}
