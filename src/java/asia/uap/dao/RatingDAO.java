/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asia.uap.dao;

/**
 *
 * @author Alexander
 */
import asia.uap.model.Rating;
import java.sql.*;
import java.util.*;

public class RatingDAO {

    public Rating findByUserAndPair(int userId, int profSubjectId) throws SQLException {
        String sql = "SELECT rating_id,user_id,prof_subject_id,clarity,fairness,engagement,knowledge,comment FROM Ratings WHERE user_id=? AND prof_subject_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, profSubjectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Rating r = new Rating();
                r.setRatingId(rs.getInt("rating_id"));
                r.setUserId(rs.getInt("user_id"));
                r.setProfSubjectId(rs.getInt("prof_subject_id"));
                r.setClarity(rs.getInt("clarity"));
                r.setFairness(rs.getInt("fairness"));
                r.setEngagement(rs.getInt("engagement"));
                r.setKnowledge(rs.getInt("knowledge"));
                r.setComment(rs.getString("comment"));
                return r;
            }
        }
    }

    public void insert(Rating r) throws SQLException {
        String sql = "INSERT INTO Ratings (user_id,prof_subject_id,clarity,fairness,engagement,knowledge,comment,status) VALUES (?,?,?,?,?,?,?,'approved')";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getUserId());
            ps.setInt(2, r.getProfSubjectId());
            ps.setInt(3, r.getClarity());
            ps.setInt(4, r.getFairness());
            ps.setInt(5, r.getEngagement());
            ps.setInt(6, r.getKnowledge());
            ps.setString(7, r.getComment());
            ps.executeUpdate();
        }
    }

    public void update(Rating r) throws SQLException {
        String sql = "UPDATE Ratings SET clarity=?,fairness=?,engagement=?,knowledge=?,comment=?,last_updated=CURRENT_TIMESTAMP WHERE rating_id=? AND user_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getClarity());
            ps.setInt(2, r.getFairness());
            ps.setInt(3, r.getEngagement());
            ps.setInt(4, r.getKnowledge());
            ps.setString(5, r.getComment());
            ps.setInt(6, r.getRatingId());
            ps.setInt(7, r.getUserId());
            ps.executeUpdate();
        }
    }

    public java.util.List<java.util.Map<String, Object>> listByUser(int userId) throws SQLException {
        String sql = "SELECT r.rating_id, r.prof_subject_id, r.clarity, r.fairness, r.engagement, r.knowledge, r.comment, "
                + "p.name AS professor_name, s.subject_code "
                + "FROM Ratings r "
                + "JOIN Professor_Subjects ps ON r.prof_subject_id=ps.prof_subject_id "
                + "JOIN Professors p ON ps.prof_id=p.prof_id "
                + "JOIN Subjects s ON ps.subject_id=s.subject_id "
                + "WHERE r.user_id=? "
                + "ORDER BY p.name ASC, s.subject_code ASC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                java.util.List<java.util.Map<String, Object>> out = new java.util.ArrayList<>();
                while (rs.next()) {
                    java.util.Map<String, Object> m = new java.util.HashMap<>();
                    m.put("ratingId", rs.getInt("rating_id"));
                    m.put("profSubjectId", rs.getInt("prof_subject_id"));
                    m.put("clarity", rs.getInt("clarity"));
                    m.put("fairness", rs.getInt("fairness"));
                    m.put("engagement", rs.getInt("engagement"));
                    m.put("knowledge", rs.getInt("knowledge"));
                    m.put("comment", rs.getString("comment"));
                    m.put("professorName", rs.getString("professor_name"));
                    m.put("subjectCode", rs.getString("subject_code"));
                    out.add(m);
                }
                return out;
            }
        }
    }

    public java.util.List<java.util.Map<String, Object>> listAllAdmin() throws SQLException {
        String sql = "SELECT r.rating_id, r.user_id, p.name AS professor_name, s.subject_code, "
                + "r.clarity, r.fairness, r.engagement, r.knowledge, r.comment "
                + "FROM Ratings r "
                + "JOIN Professor_Subjects ps ON r.prof_subject_id=ps.prof_subject_id "
                + "JOIN Professors p ON ps.prof_id=p.prof_id "
                + "JOIN Subjects s ON ps.subject_id=s.subject_id "
                + "ORDER BY r.date_submitted DESC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            java.util.List<java.util.Map<String, Object>> out = new java.util.ArrayList<>();
            while (rs.next()) {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("ratingId", rs.getInt("rating_id"));
                m.put("userId", rs.getInt("user_id"));
                m.put("professorName", rs.getString("professor_name"));
                m.put("subjectCode", rs.getString("subject_code"));
                m.put("clarity", rs.getInt("clarity"));
                m.put("fairness", rs.getInt("fairness"));
                m.put("engagement", rs.getInt("engagement"));
                m.put("knowledge", rs.getInt("knowledge"));
                m.put("comment", rs.getString("comment"));
                out.add(m);
            }
            return out;
        }
    }

    public void adminDelete(int ratingId) throws SQLException {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM Ratings WHERE rating_id=?")) {
            ps.setInt(1, ratingId);
            ps.executeUpdate();
        }
    }
    
        // List all ratings for a specific professor, with subject info.
// This will be the single source of truth for the professor detail page.
public List<Map<String, Object>> listRatingsForProfessor(int profId) throws SQLException {
    String sql =
        "SELECT " +
        "  r.rating_id, " +
        "  r.user_id, " +
        "  r.prof_subject_id, " +
        "  r.clarity, r.fairness, r.engagement, r.knowledge, " +
        "  r.comment, r.date_submitted, " +
        "  s.subject_code, s.subject_name " +
        "FROM Ratings r " +
        "JOIN Professor_Subjects ps ON r.prof_subject_id = ps.prof_subject_id " +
        "JOIN Subjects s ON ps.subject_id = s.subject_id " +
        "WHERE ps.prof_id = ? " +
        "ORDER BY s.subject_code, r.date_submitted DESC";

    List<Map<String, Object>> out = new ArrayList<>();

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, profId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("ratingId", rs.getInt("rating_id"));
                m.put("userId", rs.getInt("user_id"));
                m.put("profSubjectId", rs.getInt("prof_subject_id"));
                m.put("clarity", rs.getInt("clarity"));
                m.put("fairness", rs.getInt("fairness"));
                m.put("engagement", rs.getInt("engagement"));
                m.put("knowledge", rs.getInt("knowledge"));
                m.put("comment", rs.getString("comment"));
                m.put("dateSubmitted", rs.getTimestamp("date_submitted"));
                m.put("subjectCode", rs.getString("subject_code"));
                m.put("subjectName", rs.getString("subject_name"));
                out.add(m);
            }
        }
    }
    return out;
}
   
        /**
     * Aggregated stats per subject for a given professor.
     */
    public java.util.List<java.util.Map<String, Object>> listSubjectStatsByProfessor(int profId)
            throws SQLException {

        String sql = "SELECT s.subject_id, s.subject_code, s.subject_name, "
                + "COUNT(r.rating_id) AS total_ratings, "
                + "AVG(r.clarity) AS avg_clarity, "
                + "AVG(r.fairness) AS avg_fairness, "
                + "AVG(r.engagement) AS avg_engagement, "
                + "AVG(r.knowledge) AS avg_knowledge, "
                + "AVG((r.clarity + r.fairness + r.engagement + r.knowledge) / 4.0) AS avg_overall "
                + "FROM Professor_Subjects ps "
                + "JOIN Subjects s ON ps.subject_id = s.subject_id "
                + "LEFT JOIN Ratings r ON r.prof_subject_id = ps.prof_subject_id "
                + "WHERE ps.prof_id = ? "
                + "GROUP BY s.subject_id, s.subject_code, s.subject_name "
                + "ORDER BY s.subject_code";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, profId);
            try (ResultSet rs = ps.executeQuery()) {
                java.util.List<java.util.Map<String, Object>> out = new java.util.ArrayList<>();
                while (rs.next()) {
                    java.util.Map<String, Object> m = new java.util.HashMap<>();
                    m.put("subjectId", rs.getInt("subject_id"));
                    m.put("subjectCode", rs.getString("subject_code"));
                    m.put("subjectName", rs.getString("subject_name"));
                    m.put("totalRatings", rs.getInt("total_ratings"));
                    m.put("avgClarity", rs.getDouble("avg_clarity"));
                    m.put("avgFairness", rs.getDouble("avg_fairness"));
                    m.put("avgEngagement", rs.getDouble("avg_engagement"));
                    m.put("avgKnowledge", rs.getDouble("avg_knowledge"));
                    m.put("avgOverall", rs.getDouble("avg_overall"));
                    out.add(m);
                }
                return out;
            }
        }
    }
    
        /**
     * All ratings for a specific professor, with subject context.
     */
    public java.util.List<java.util.Map<String, Object>> listByProfessor(int profId)
            throws SQLException {

        String sql = "SELECT r.rating_id, r.user_id, r.clarity, r.fairness, r.engagement, "
                + "r.knowledge, r.comment, r.date_submitted, "
                + "s.subject_code, s.subject_name "
                + "FROM Ratings r "
                + "JOIN Professor_Subjects ps ON r.prof_subject_id = ps.prof_subject_id "
                + "JOIN Subjects s ON ps.subject_id = s.subject_id "
                + "WHERE ps.prof_id = ? "
                + "ORDER BY s.subject_code, r.date_submitted DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, profId);
            try (ResultSet rs = ps.executeQuery()) {
                java.util.List<java.util.Map<String, Object>> out = new java.util.ArrayList<>();
                while (rs.next()) {
                    java.util.Map<String, Object> m = new java.util.HashMap<>();
                    m.put("ratingId", rs.getInt("rating_id"));
                    m.put("userId", rs.getInt("user_id"));
                    m.put("clarity", rs.getInt("clarity"));
                    m.put("fairness", rs.getInt("fairness"));
                    m.put("engagement", rs.getInt("engagement"));
                    m.put("knowledge", rs.getInt("knowledge"));
                    m.put("comment", rs.getString("comment"));
                    m.put("dateSubmitted", rs.getTimestamp("date_submitted"));
                    m.put("subjectCode", rs.getString("subject_code"));
                    m.put("subjectName", rs.getString("subject_name"));
                    out.add(m);
                }
                return out;
            }
        }
    }
    
    // List all ratings given by a specific student (for "My Ratings" page)
public java.util.List<java.util.Map<String, Object>> listRatingsByStudent(int studentId) throws SQLException {
    String sql =
        "SELECT " +
        "  r.rating_id, " +
        "  r.clarity, " +
        "  r.fairness, " +
        "  r.engagement, " +
        "  r.knowledge, " +
        "  r.date_submitted, " +
        "  ps.prof_subject_id, " +
        "  p.prof_id, " +
        "  p.name AS professor_name, " +
        "  s.subject_code, " +
        "  s.subject_name " +
        "FROM Ratings r " +
        "JOIN Professor_Subjects ps ON r.prof_subject_id = ps.prof_subject_id " +
        "JOIN Professors p ON ps.prof_id = p.prof_id " +
        "JOIN Subjects s ON ps.subject_id = s.subject_id " +
        // 🔴 TODO: CHANGE r.student_id TO YOUR ACTUAL COLUMN NAME
        // e.g. r.user_id, r.account_id, r.student_no, etc.
        "WHERE r.student_id = ? " +
        "ORDER BY r.date_submitted DESC";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, studentId);

        try (ResultSet rs = ps.executeQuery()) {
            java.util.List<java.util.Map<String, Object>> out = new java.util.ArrayList<>();
            while (rs.next()) {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("ratingId", rs.getInt("rating_id"));
                m.put("dateSubmitted", rs.getTimestamp("date_submitted"));
                m.put("profSubjectId", rs.getInt("prof_subject_id"));
                m.put("profId", rs.getInt("prof_id"));
                m.put("professorName", rs.getString("professor_name"));
                m.put("subjectCode", rs.getString("subject_code"));
                m.put("subjectName", rs.getString("subject_name"));
                m.put("clarity", rs.getInt("clarity"));
                m.put("fairness", rs.getInt("fairness"));
                m.put("engagement", rs.getInt("engagement"));
                m.put("knowledge", rs.getInt("knowledge"));
                out.add(m);
            }
            return out;
        }
    }
}

}
