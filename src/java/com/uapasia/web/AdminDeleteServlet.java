package com.uapasia.web;

import com.uapasia.model.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet(name = "AdminDeleteServlet", urlPatterns = {"/do.admin.delete"})
public class AdminDeleteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User admin = (User) req.getSession().getAttribute("user");
        final String ctx = req.getContextPath(); // e.g. "/demo"

        // 1) Must be logged in and admin
        if (admin == null || admin.getRole() != Role.ADMIN) {
            resp.sendRedirect(ctx + "/login.jsp?status=unauthorized");
            return;
        }

        final String type = req.getParameter("type");   // "professor" or "rating"
        final int id = parseInt(req.getParameter("id"), 0);

        if (id <= 0 || type == null) {
            resp.sendRedirect(ctx + "/admin?status=invalid");
            return;
        }

        try {
            boolean deleted = false;

            if ("professor".equalsIgnoreCase(type)) {
                deleted = deleteProfessor(id);
                if (deleted) recordAudit(admin, "DELETE_PROFESSOR", "professor:" + id);
            } else if ("rating".equalsIgnoreCase(type)) {
                deleted = deleteRating(id);
                if (deleted) recordAudit(admin, "DELETE_RATING", "rating:" + id);
            }

            resp.sendRedirect(ctx + (deleted ? "/admin?status=deleted" : "/admin?status=notfound"));
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(ctx + "/admin?status=error");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Prefer POST; fallback to POST behavior
        doPost(req, resp);
    }

    /* ---------- helper methods ---------- */

    // Delete professor and any ratings for that professor in a single transaction
    private static boolean deleteProfessor(int profId) throws Exception {
        try (Connection c = com.uapasia.dao.util.DB.get()) {
            c.setAutoCommit(false);
            int affectedProf;

            try (PreparedStatement ps1 = c.prepareStatement("DELETE FROM ratings WHERE prof_id=?")) {
                ps1.setInt(1, profId);
                ps1.executeUpdate(); // cascade cleanup (safe even if none)
            }

            try (PreparedStatement ps2 = c.prepareStatement("DELETE FROM professors WHERE prof_id=?")) {
                ps2.setInt(1, profId);
                affectedProf = ps2.executeUpdate();
            }

            c.commit();
            return affectedProf > 0;
        } catch (Exception ex) {
            // Let caller handle; if you want explicit rollback:
            // (DB.get() returns a fresh connection per call, so no rollback here)
            throw ex;
        }
    }

    private static boolean deleteRating(int ratingId) throws Exception {
        try (Connection c = com.uapasia.dao.util.DB.get();
             PreparedStatement ps = c.prepareStatement("DELETE FROM ratings WHERE rating_id=?")) {
            ps.setInt(1, ratingId);
            return ps.executeUpdate() > 0;
        }
    }

    private static void recordAudit(User admin, String action, String target) {
        try (Connection c = com.uapasia.dao.util.DB.get();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO audit_logs(actor_id, action, target) VALUES (?,?,?)")) {
            ps.setInt(1, admin.getUserId());
            ps.setString(2, action);
            ps.setString(3, target);
            ps.executeUpdate();
        } catch (Exception ignored) { }
    }

    private static int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}
