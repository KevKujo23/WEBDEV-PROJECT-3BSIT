package com.uapasia.web;

import com.uapasia.dao.*;
import com.uapasia.factory.DAOFactory;
import com.uapasia.model.*;

import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "ProfessorViewServlet", urlPatterns = {"/do.professor.view"})
public class ProfessorViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String ctx = req.getContextPath();
        int profId = parseInt(req.getParameter("id"), 0);

        if (profId <= 0) {
            resp.sendRedirect(ctx + "/do.professors");
            return;
        }

        ProfessorDAO profDAO = DAOFactory.professors();
        SubjectDAO subjDAO   = DAOFactory.subjects();

        Professor prof = null;
        try {
            prof = profDAO.findById(profId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (prof == null) {
            resp.sendRedirect(ctx + "/do.professors?status=notfound");
            return;
        }

        // --- Load ratings for this professor ---
        List<Rating> ratings = new ArrayList<>();
        double average = 0.0;

        try (Connection c = com.uapasia.dao.util.DB.get();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT rating_id, prof_id, user_id, subject_id, academic_year, term, score, comment, created_at " +
                             "FROM ratings WHERE prof_id=? ORDER BY created_at DESC")) {
            ps.setInt(1, profId);
            try (ResultSet rs = ps.executeQuery()) {
                int totalScore = 0, count = 0;
                while (rs.next()) {
                    Rating r = new Rating();
                    r.setRatingId(rs.getInt("rating_id"));
                    r.setProfId(rs.getInt("prof_id"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setSubjectId(rs.getInt("subject_id"));
                    r.setAcademicYear(rs.getString("academic_year"));
                    r.setTerm(Term.fromDb(rs.getString("term")));
                    r.setScore(rs.getInt("score"));
                    r.setComment(rs.getString("comment"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) r.setCreatedAt(ts.toInstant());
                    ratings.add(r);
                    totalScore += r.getScore();
                    count++;
                }
                if (count > 0) average = totalScore / (double) count;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String avgTxt = ratings.isEmpty() ? "—" : new DecimalFormat("0.0").format(average);

        // --- HTML output ---
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<!doctype html><html><head><meta charset='utf-8'><title>"
                + esc(prof.getFullName()) + " • Professor</title>"
                + "<link rel='stylesheet' href='" + ctx + "/css/styles.css'></head><body>");

        out.println("<nav class='top-nav'>"
                + "<a href='" + ctx + "/'>Home</a>"
                + "<a href='" + ctx + "/do.professors'>Professors</a>"
                + "</nav>");

        out.println("<div class='panel-container'>");
        out.println("<h2>" + esc(prof.getFullName()) +
                " <span class='score-badge'>Avg: " + avgTxt + "</span></h2>");
        out.println("<p class='note'>Department ID: " + prof.getDeptId() + "</p>");

        /* ---------- Add Rating Form ---------- */
        out.println("<h3 style='margin-top:20px'>Add Rating</h3>");
        out.println("<form method='post' action='" + ctx + "/do.ratings' autocomplete='off'>");
        out.println("<input type='hidden' name='profId' value='" + prof.getProfId() + "'/>");

        // Subject dropdown (from DB)
        try {
            List<Subject> subjects = subjDAO.listAll();
            out.println("<div class='form-group'><label>Subject<select name='subjectId' required>");
            out.println("<option value=''>Select…</option>");
            for (Subject s : subjects) {
                out.println("<option value='" + s.getSubjectId() + "'>" +
                        esc(s.getCode()) + " – " + esc(s.getTitle()) + "</option>");
            }
            out.println("</select></label></div>");
        } catch (Exception e) {
            out.println("<p class='note error'>Failed to load subjects.</p>");
        }

        out.println("<div class='form-group'><label>Academic Year"
                + "<input type='text' name='academicYear' placeholder='e.g. 2025-2026' required></label></div>");
        out.println("<div class='form-group'><label>Term"
                + "<select name='term' required>"
                + "<option value='1'>1st</option><option value='2'>2nd</option><option value='Summer'>Summer</option>"
                + "</select></label></div>");
        out.println("<div class='form-group'><label>Score (1–5)"
                + "<input type='number' name='score' min='1' max='5' required></label></div>");
        out.println("<div class='form-group'><label>Comment (optional)"
                + "<textarea name='comment' placeholder='Share your thoughts...'></textarea></label></div>");
        out.println("<button type='submit'>Submit Rating</button>");
        out.println("</form>");

        /* ---------- Ratings List ---------- */
        out.println("<h3 style='margin-top:24px'>Ratings</h3>");
        if (ratings.isEmpty()) {
            out.println("<p class='note'>No ratings yet. Be the first to add one!</p>");
        } else {
            out.println("<table><thead><tr><th>ID</th><th>Subject ID</th><th>Score</th><th>Comment</th>"
                    + "<th>Year</th><th>Term</th><th>Created</th></tr></thead><tbody>");
            for (Rating r : ratings) {
                out.println("<tr>"
                        + "<td>" + r.getRatingId() + "</td>"
                        + "<td>" + r.getSubjectId() + "</td>"
                        + "<td>" + r.getScore() + "</td>"
                        + "<td>" + esc(nullToEmpty(r.getComment())) + "</td>"
                        + "<td>" + esc(nullToEmpty(r.getAcademicYear())) + "</td>"
                        + "<td>" + (r.getTerm() == null ? "" : r.getTerm().getDbValue()) + "</td>"
                        + "<td>" + fmt(r.getCreatedAt()) + "</td>"
                        + "</tr>");
            }
            out.println("</tbody></table>");
        }

        out.println("</div></body></html>");
    }

    /* ---------- helpers ---------- */
    private static int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String fmt(java.time.Instant t) {
        if (t == null) return "";
        try {
            java.util.Date d = java.util.Date.from(t);
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(d);
        } catch (Exception e) {
            return t.toString();
        }
    }
}
