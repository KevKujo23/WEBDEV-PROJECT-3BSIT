package com.uapasia.web;

import com.uapasia.dao.ProfessorDAO;
import com.uapasia.dao.RatingDAO;
import com.uapasia.factory.DAOFactory;
import com.uapasia.model.*;
import com.uapasia.util.CookieUtils;

import javax.servlet.http.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "NewRatingServlet", urlPatterns = {"/do.ratings"})
public class NewRatingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String ctx = request.getContextPath(); // e.g., "/ProjectWebDev"

        // Require login and non-admin
        HttpSession session = request.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(ctx + "/login.jsp?status=unauthorized");
            return;
        }
        if (user.getRole() == Role.ADMIN) {
            response.sendRedirect(ctx + "/index.jsp?status=forbidden");
            return;
        }

        int profId     = parseInt(pick(request.getParameter("profId"), request.getParameter("professorId")), 0);
        int subjectId  = parseInt(request.getParameter("subjectId"), 0);
        String ay      = trim(request.getParameter("academicYear"));
        Term term      = parseTerm(request.getParameter("term"));
        int score      = parseInt(request.getParameter("score"), 0);
        String comment = trim(request.getParameter("comment"));

        ProfessorDAO profDAO = DAOFactory.professors();

        try {
            Professor prof = profDAO.findById(profId);
            if (prof == null) {
                response.sendRedirect(ctx + "/do.professors");
                return;
            }

            // Validation
            if (score < 1 || score > 5) {
                response.sendRedirect(ctx + "/do.professor.view?id=" + profId + "&status=badscore");
                return;
            }
            if (subjectId <= 0 || isBlank(ay) || term == null) {
                response.sendRedirect(ctx + "/do.professor.view?id=" + profId + "&status=missing");
                return;
            }
            if (wordCount(comment) > 300) {
                response.sendRedirect(ctx + "/do.professor.view?id=" + profId + "&status=toolong");
                return;
            }

            RatingDAO ratingDAO = DAOFactory.ratings();

            if (ratingDAO.exists(user.getUserId(), profId, subjectId, ay, term)) {
                response.sendRedirect(ctx + "/do.professor.view?id=" + profId + "&status=already");
                return;
            }

            // Build and insert rating
            Rating r = new Rating();
            r.setUserId(user.getUserId());
            r.setProfId(profId);
            r.setSubjectId(subjectId);
            r.setAcademicYear(ay);
            r.setTerm(term);
            r.setScore(score);
            r.setComment(comment);

            int id = ratingDAO.insert(r);
            if (id <= 0) {
                response.sendRedirect(ctx + "/do.professor.view?id=" + profId + "&status=error");
                return;
            }

            // Optional nicety cookie: store last professor viewed (30 days)
            try {
                Cookie ck = CookieUtils.make(
                        "last_prof",
                        prof.getFullName(),
                        30 * 24 * 60 * 60, // 30 days
                        ctx,
                        false // set to true for HTTPS
                );
                response.addCookie(ck);
            } catch (Throwable ignored) { }

            response.sendRedirect(ctx + "/do.professor.view?id=" + profId + "&status=ok");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(ctx + "/do.professor.view?id=" + profId + "&status=error");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String ctx = req.getContextPath();
        resp.sendRedirect(ctx + "/do.professors");
    }

    /* ---------- helper methods ---------- */
    private static String pick(String... vals) {
        if (vals == null) return null;
        for (String v : vals) if (v != null && !v.trim().isEmpty()) return v.trim();
        return null;
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    private static int wordCount(String s) {
        if (isBlank(s)) return 0;
        return s.trim().split("\\s+").length;
    }

    private static Term parseTerm(String t) {
        if (t == null) return null;
        String v = t.trim();
        if ("1".equals(v)) return Term.ONE;
        if ("2".equals(v)) return Term.TWO;
        if ("Summer".equalsIgnoreCase(v)) return Term.SUMMER;
        return null;
    }
}
