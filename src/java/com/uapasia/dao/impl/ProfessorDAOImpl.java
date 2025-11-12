package com.uapasia.dao.impl;

import com.uapasia.dao.ProfessorDAO;
import com.uapasia.dao.util.DB;
import com.uapasia.model.Professor;
import com.uapasia.model.search.ProfessorSearchCriteria;
import com.uapasia.model.search.ProfessorSearchResult;

import java.sql.*;
import java.util.*;

/**
 * Implementation of ProfessorDAO for MySQL database.
 * Compatible with your schema:
 * - professors(prof_id, first_name, last_name, dept_id, created_at)
 * - departments(dept_id, dept_name)
 * - ratings(rating_id, prof_id, score, comment, created_at)
 * - subjects(subject_id, code, title, dept_id)
 * - professor_subjects(prof_id, subject_id)
 */
public class ProfessorDAOImpl implements ProfessorDAO {

    @Override
    public Professor findById(int profId) throws SQLException {
        final String sql =
            "SELECT prof_id, first_name, last_name, dept_id, created_at " +
            "FROM professors WHERE prof_id = ?";

        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

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
        final boolean filterByDept = deptId > 0;
        final String sql =
            "SELECT prof_id, first_name, last_name, dept_id, created_at " +
            "FROM professors " +
            (filterByDept ? "WHERE dept_id = ? " : "") +
            "ORDER BY last_name, first_name";

        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            if (filterByDept) ps.setInt(1, deptId);

            List<Professor> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
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
            }
            return out;
        }
    }

    @Override
    public List<ProfessorSearchResult> search(ProfessorSearchCriteria c, int offset, int limit) {
        StringBuilder sql = new StringBuilder(
            "SELECT p.prof_id, CONCAT(p.first_name, ' ', p.last_name) AS full_name, " +
            "       d.dept_name, AVG(r.score) AS avg_rating, COUNT(r.rating_id) AS rating_count, " +
            "       (SELECT rt.comment FROM ratings rt WHERE rt.prof_id = p.prof_id " +
            "         ORDER BY rt.created_at DESC LIMIT 1) AS recent_comment " +
            "FROM professors p " +
            "JOIN departments d ON d.dept_id = p.dept_id " +
            "LEFT JOIN ratings r ON r.prof_id = p.prof_id "
        );

        List<Object> params = new ArrayList<>();
        List<Integer> types = new ArrayList<>();
        List<String> where = new ArrayList<>();

        // --- Name filter ---
        if (hasText(c.getQuery())) {
            where.add("(p.first_name LIKE ? OR p.last_name LIKE ?)");
            params.add("%" + c.getQuery() + "%"); types.add(Types.VARCHAR);
            params.add("%" + c.getQuery() + "%"); types.add(Types.VARCHAR);
        }

        // --- Department filter ---
        if (c.getDeptId() != null) {
            where.add("p.dept_id = ?");
            params.add(c.getDeptId()); types.add(Types.INTEGER);
        }

        // --- Course filter (subjects + professor_subjects) ---
        if (hasText(c.getCourseKeyword())) {
            where.add(
                "EXISTS (SELECT 1 FROM professor_subjects ps " +
                "JOIN subjects s ON s.subject_id = ps.subject_id " +
                "WHERE ps.prof_id = p.prof_id " +
                "AND (s.code LIKE ? OR s.title LIKE ?))"
            );
            params.add("%" + c.getCourseKeyword() + "%"); types.add(Types.VARCHAR);
            params.add("%" + c.getCourseKeyword() + "%"); types.add(Types.VARCHAR);
        }

        // --- Combine WHERE ---
        if (!where.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", where)).append(" ");
        }

        sql.append(" GROUP BY p.prof_id ");

        // --- Minimum rating ---
        if (c.getMinRating() > 0.0) {
            sql.append(" HAVING COALESCE(AVG(r.score), 0) >= ? ");
            params.add(c.getMinRating()); types.add(Types.DOUBLE);
        }

        // --- Sort order ---
        switch (String.valueOf(c.getSort())) {
            case "highest":
                sql.append(" ORDER BY avg_rating DESC, rating_count DESC ");
                break;
            case "recent":
                sql.append(" ORDER BY COALESCE((SELECT MAX(rt.created_at) FROM ratings rt WHERE rt.prof_id = p.prof_id), '1970-01-01') DESC ");
                break;
            case "az":
                sql.append(" ORDER BY full_name ASC ");
                break;
            case "most":
            default:
                sql.append(" ORDER BY rating_count DESC, avg_rating DESC ");
        }

        sql.append(" LIMIT ? OFFSET ? ");
        params.add(limit);  types.add(Types.INTEGER);
        params.add(offset); types.add(Types.INTEGER);

        // --- Execute query ---
        List<ProfessorSearchResult> results = new ArrayList<>();
        try (Connection cx = DB.get();
             PreparedStatement ps = cx.prepareStatement(sql.toString())) {

            bind(ps, params, types);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProfessorSearchResult r = new ProfessorSearchResult();
                    r.setProfId(rs.getInt("prof_id"));
                    r.setFullName(rs.getString("full_name"));
                    r.setDeptName(rs.getString("dept_name"));
                    r.setAvgRating(rs.getDouble("avg_rating"));
                    r.setRatingCount(rs.getInt("rating_count"));
                    r.setRecentCommentSnippet(snip(rs.getString("recent_comment"), 140));
                    r.setTopTags(Collections.emptyList());
                    results.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    // --- Helpers ---

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static void bind(PreparedStatement ps, List<Object> params, List<Integer> types) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            int t = types.get(i);
            Object v = params.get(i);
            if (t == Types.INTEGER) ps.setInt(i + 1, (Integer) v);
            else if (t == Types.DOUBLE) ps.setDouble(i + 1, (Double) v);
            else ps.setString(i + 1, String.valueOf(v));
        }
    }

    private static String snip(String s, int max) {
        if (s == null) return null;
        String t = s.trim().replaceAll("\\s+", " ");
        return (t.length() <= max) ? t : t.substring(0, Math.max(0, max - 1)) + "…";
    }
}
