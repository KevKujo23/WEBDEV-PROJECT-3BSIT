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

public class ProfessorDAO {

    /**
     * Browse/filter professors for the public/student page.
     * Now includes analytics per professor-subject pair.
     */
    public java.util.List<java.util.Map<String, Object>> filter(Integer deptId, Integer subjectId, String q) throws SQLException {
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append("  ps.prof_subject_id, ");
        sb.append("  p.prof_id, ");
        sb.append("  p.name AS professor_name, ");
        sb.append("  d.dept_code, ");
        sb.append("  s.subject_code, ");
        sb.append("  s.subject_name, ");

        // --- analytics columns ---
        sb.append("  AVG((r.clarity + r.fairness + r.engagement + r.knowledge) / 4.0) AS avg_overall, ");
        sb.append("  COUNT(r.rating_id) AS rating_count, ");
        sb.append("  MIN((r.clarity + r.fairness + r.engagement + r.knowledge) / 4.0) AS min_overall, ");
        sb.append("  MAX((r.clarity + r.fairness + r.engagement + r.knowledge) / 4.0) AS max_overall, ");
        sb.append("  AVG(r.clarity)    AS avg_clarity, ");
        sb.append("  AVG(r.fairness)   AS avg_fairness, ");
        sb.append("  AVG(r.engagement) AS avg_engagement, ");
        sb.append("  AVG(r.knowledge)  AS avg_knowledge, ");
        sb.append("  MAX(r.date_submitted) AS last_rating_date ");

        sb.append("FROM Professor_Subjects ps ");
        sb.append("JOIN Professors p ON ps.prof_id = p.prof_id ");
        sb.append("JOIN Departments d ON p.dept_id = d.dept_id ");
        sb.append("JOIN Subjects s ON ps.subject_id = s.subject_id ");
        sb.append("LEFT JOIN Ratings r ON r.prof_subject_id = ps.prof_subject_id ");
        sb.append("WHERE 1=1 ");

        java.util.List<Object> prms = new java.util.ArrayList<>();

        if (deptId != null) {
            sb.append(" AND p.dept_id=?");
            prms.add(deptId);
        }
        if (subjectId != null) {
            sb.append(" AND s.subject_id=?");
            prms.add(subjectId);
        }
        if (q != null && !q.isBlank()) {
            sb.append(" AND (p.name LIKE ? OR s.subject_code LIKE ?)");
            prms.add("%" + q.trim() + "%");
            prms.add("%" + q.trim() + "%");
        }

        sb.append(" GROUP BY ");
        sb.append("  ps.prof_subject_id, ");
        sb.append("  p.prof_id, ");
        sb.append("  p.name, ");
        sb.append("  d.dept_code, ");
        sb.append("  s.subject_code, ");
        sb.append("  s.subject_name ");

        sb.append(" ORDER BY p.name ASC, s.subject_code ASC");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sb.toString())) {

            for (int i = 0; i < prms.size(); i++) {
                ps.setObject(i + 1, prms.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
                while (rs.next()) {
                    java.util.Map<String, Object> m = new java.util.HashMap<>();

                    // base info (same keys as before)
                    m.put("profSubjectId", rs.getInt("prof_subject_id"));
                    m.put("profId", rs.getInt("prof_id"));
                    m.put("professorName", rs.getString("professor_name"));
                    m.put("deptCode", rs.getString("dept_code"));
                    m.put("subjectCode", rs.getString("subject_code"));
                    m.put("subjectName", rs.getString("subject_name"));

                    // analytics (may be null if no ratings)
                    m.put("avgOverall", rs.getBigDecimal("avg_overall"));
                    m.put("ratingCount", rs.getInt("rating_count"));
                    m.put("minOverall", rs.getBigDecimal("min_overall"));
                    m.put("maxOverall", rs.getBigDecimal("max_overall"));
                    m.put("avgClarity", rs.getBigDecimal("avg_clarity"));
                    m.put("avgFairness", rs.getBigDecimal("avg_fairness"));
                    m.put("avgEngagement", rs.getBigDecimal("avg_engagement"));
                    m.put("avgKnowledge", rs.getBigDecimal("avg_knowledge"));
                    m.put("lastRatingDate", rs.getTimestamp("last_rating_date"));

                    list.add(m);
                }
                return list;
            }
        }
    }

    /* ---------- existing admin / CRUD methods below are unchanged ---------- */

    public java.util.List<java.util.Map<String, Object>> listAllForAdmin() throws SQLException {
        String sql = "SELECT p.prof_id, p.name, d.dept_code, p.dept_id "
                + "FROM Professors p JOIN Departments d ON p.dept_id=d.dept_id "
                + "ORDER BY p.name ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            java.util.List<java.util.Map<String, Object>> out = new java.util.ArrayList<>();
            while (rs.next()) {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("profId", rs.getInt("prof_id"));
                m.put("name", rs.getString("name"));
                m.put("deptCode", rs.getString("dept_code"));
                m.put("deptId", rs.getInt("dept_id"));
                out.add(m);
            }
            return out;
        }
    }

    public void createProfessor(String name, int deptId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO Professors (name, dept_id) VALUES (?,?)")) {
            ps.setString(1, name);
            ps.setInt(2, deptId);
            ps.executeUpdate();
        }
    }

    public void deleteProfessor(int profId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM Professors WHERE prof_id=?")) {
            ps.setInt(1, profId);
            ps.executeUpdate();
        }
    }

    public java.util.Map<String, Object> findProfessor(int profId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT prof_id, name, dept_id FROM Professors WHERE prof_id=?")) {
            ps.setInt(1, profId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("profId", rs.getInt("prof_id"));
                m.put("name", rs.getString("name"));
                m.put("deptId", rs.getInt("dept_id"));
                return m;
            }
        }
    }

    /**
     * Create and return prof_id. If the (dept_id,name) already exists, return its existing ID.
     */
    public int createProfessorReturningId(String name, int deptId) throws SQLException {
        String insert = "INSERT INTO Professors (name, dept_id) VALUES (?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setInt(2, deptId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new SQLException("Failed to get generated key for Professors.");
        } catch (SQLIntegrityConstraintViolationException dup) {
            // Already exists (because of uq_prof_dept_name). Fetch and return existing prof_id.
            Integer id = findIdByNameAndDept(name, deptId);
            if (id != null) {
                return id;
            }
            throw dup;
        }
    }

    public Integer findIdByNameAndDept(String name, int deptId) throws SQLException {
        String sql = "SELECT prof_id FROM Professors WHERE dept_id=? AND name=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            ps.setString(2, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }

    public void updateProfessor(int profId, String name, int deptId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE Professors SET name=?, dept_id=? WHERE prof_id=?")) {
            ps.setString(1, name);
            ps.setInt(2, deptId);
            ps.setInt(3, profId);
            ps.executeUpdate();
        }
    }
    
            /**
     * Overall stats for a single professor across ALL their subjects.
     */
    public Map<String, Object> getProfessorOverview(int profId) throws SQLException {
        String sql =
            "SELECT " +
            "  p.prof_id, " +
            "  p.name AS professor_name, " +
            "  d.dept_code, " +
            "  d.dept_name, " +
            "  COUNT(r.rating_id) AS rating_count, " +
            "  AVG((r.clarity + r.fairness + r.engagement + r.knowledge) / 4.0) AS avg_overall, " +
            "  MIN((r.clarity + r.fairness + r.engagement + r.knowledge) / 4.0) AS min_overall, " +
            "  MAX((r.clarity + r.fairness + r.engagement + r.knowledge) / 4.0) AS max_overall, " +
            "  AVG(r.clarity)    AS avg_clarity, " +
            "  AVG(r.fairness)   AS avg_fairness, " +
            "  AVG(r.engagement) AS avg_engagement, " +
            "  AVG(r.knowledge)  AS avg_knowledge, " +
            "  MAX(r.date_submitted) AS last_rating_date " +
            "FROM Professors p " +
            "JOIN Departments d ON p.dept_id = d.dept_id " +
            "LEFT JOIN Professor_Subjects ps ON ps.prof_id = p.prof_id " +
            "LEFT JOIN Ratings r ON r.prof_subject_id = ps.prof_subject_id " +
            "WHERE p.prof_id = ? " +
            "GROUP BY p.prof_id, p.name, d.dept_code, d.dept_name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, profId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Map<String, Object> m = new HashMap<>();
                m.put("profId", rs.getInt("prof_id"));
                m.put("professorName", rs.getString("professor_name"));
                m.put("deptCode", rs.getString("dept_code"));
                m.put("deptName", rs.getString("dept_name"));
                m.put("ratingCount", rs.getInt("rating_count"));   // 0 if no ratings
                m.put("avgOverall", rs.getBigDecimal("avg_overall"));
                m.put("minOverall", rs.getBigDecimal("min_overall"));
                m.put("maxOverall", rs.getBigDecimal("max_overall"));
                m.put("avgClarity", rs.getBigDecimal("avg_clarity"));
                m.put("avgFairness", rs.getBigDecimal("avg_fairness"));
                m.put("avgEngagement", rs.getBigDecimal("avg_engagement"));
                m.put("avgKnowledge", rs.getBigDecimal("avg_knowledge"));
                m.put("lastRatingDate", rs.getTimestamp("last_rating_date"));
                return m;
            }
        }
    }

        /**
     * Per-subject stats for a given professor.
     */
    public List<Map<String, Object>> listSubjectsWithStatsByProfessor(int profId) throws SQLException {
        String sql =
            "SELECT " +
            "  ps.prof_subject_id, " +
            "  s.subject_id, " +
            "  s.subject_code, " +
            "  s.subject_name, " +
            "  COUNT(r.rating_id) AS rating_count, " +
            "  AVG((r.clarity + r.fairness + r.engagement + r.knowledge) / 4.0) AS avg_overall, " +
            "  MIN((r.clarity + r.fairness + r.engagement + r.knowledge) / 4.0) AS min_overall, " +
            "  MAX((r.clarity + r.fairness + r.engagement + r.knowledge) / 4.0) AS max_overall, " +
            "  AVG(r.clarity)    AS avg_clarity, " +
            "  AVG(r.fairness)   AS avg_fairness, " +
            "  AVG(r.engagement) AS avg_engagement, " +
            "  AVG(r.knowledge)  AS avg_knowledge " +
            "FROM Professor_Subjects ps " +
            "JOIN Subjects s ON ps.subject_id = s.subject_id " +
            "LEFT JOIN Ratings r ON r.prof_subject_id = ps.prof_subject_id " +
            "WHERE ps.prof_id = ? " +
            "GROUP BY ps.prof_subject_id, s.subject_id, s.subject_code, s.subject_name " +
            "ORDER BY s.subject_code";

        List<Map<String, Object>> out = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, profId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("profSubjectId", rs.getInt("prof_subject_id"));
                    m.put("subjectId", rs.getInt("subject_id"));
                    m.put("subjectCode", rs.getString("subject_code"));
                    m.put("subjectName", rs.getString("subject_name"));
                    m.put("ratingCount", rs.getInt("rating_count"));    // 0 if none
                    m.put("avgOverall", rs.getBigDecimal("avg_overall"));
                    m.put("minOverall", rs.getBigDecimal("min_overall"));
                    m.put("maxOverall", rs.getBigDecimal("max_overall"));
                    m.put("avgClarity", rs.getBigDecimal("avg_clarity"));
                    m.put("avgFairness", rs.getBigDecimal("avg_fairness"));
                    m.put("avgEngagement", rs.getBigDecimal("avg_engagement"));
                    m.put("avgKnowledge", rs.getBigDecimal("avg_knowledge"));
                    out.add(m);
                }
            }
        }

        return out;
    }
    
    public Map<String, Object> getProfessorWithDept(int profId) throws SQLException {
    String sql = "SELECT p.prof_id, p.name, d.dept_code, d.dept_name, p.dept_id "
               + "FROM Professors p "
               + "JOIN Departments d ON p.dept_id = d.dept_id "
               + "WHERE p.prof_id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, profId);

        try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                return null;
            }

            Map<String, Object> m = new HashMap<>();

            String fullName = rs.getString("name");
            String first = "";
            String last = "";

            if (fullName != null) {
                String[] parts = fullName.trim().split("\\s+", 2);
                first = parts.length > 0 ? parts[0] : "";
                last  = parts.length > 1 ? parts[1] : "";
            }

            m.put("profId", rs.getInt("prof_id"));
            m.put("firstName", first);
            m.put("lastName", last);
            m.put("professorName", fullName);  // used directly by JSP
            m.put("deptId", rs.getInt("dept_id"));
            m.put("deptCode", rs.getString("dept_code"));
            m.put("deptName", rs.getString("dept_name"));

            return m;
        }
    }
}
    
}


