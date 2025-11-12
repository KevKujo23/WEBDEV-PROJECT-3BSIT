package com.uapasia.web;

import com.uapasia.dao.ProfessorDAO;
import com.uapasia.dao.RatingDAO;
import com.uapasia.factory.DAOFactory;
import com.uapasia.model.*;
import com.uapasia.util.CookieUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet(name = "NewRatingServlet", urlPatterns = {"/do.ratings"})
public class NewRatingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        final String ctx = request.getContextPath(); // e.g., "/ProjectWebDev"

        // Require login and non-admin (students only)
        HttpSession session = request.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");
        if (user == null) {
            // Preserve where they came from
            String next = "/do.professor.view?id=" + safe(request.getParameter("profId"));
            response.sendRedirect(ctx + "/login.jsp?status=unauthorized&next=" + URLEncoder.encode(next, "UTF-8"));
            return;
        }
        if (user.getRole() == Role.ADMIN) {
            response.sendRedirect(ctx + "/index.jsp?status=forbidden");
            return;
        }

        // Gather + validate inputs
        int profId     = parseInt(pick(request.getParameter("profId"), request.getParameter("professorId")), 0);
        int subjectId  = parseInt(request.getParameter("subjectId"), 0);
        String ay      = trim(request.getParameter("academicYear"));
        Term term      = parseTerm(request.getParameter("term"));
        int score      = parseInt(request.getParameter("score"), 0);
        String comment = trim(request.getParameter("comment"));

        // Ensure valid professor
        ProfessorDAO profDAO = DAOFactory.professors();
        try {
            Professor prof = profDAO.findById(profId);
            if (prof == null) {
                response.sendRedirect(ctx + "/do.professors");
                return;
            }

            // Field validations
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

            // Prevent duplicate rating for same (user, prof, subject, AY, term)
            if (ratingDAO.exists(user.getUserId(), profId, subjectId, ay, term)) {
                response.sendRedirect(ctx + "/do.professor.view?id=" + profId + "&status=already");
                return;
            }

            // Build rating
            Rating r = new Rating();
            r.setUserId(user.getUserId());
            r.setProfId(profId);
            r.setSubjectId(subjectId);
            r.setAcademicYear(ay);
            r.setTerm(term);
            r.setScore(score);
            r.setComment(comment);

            // Insert
            int id = ratingDAO.insert(r);
            if (id <= 0) {
                response.sendRedirect(ctx + "/do.professor.view?id=" + profId + "&status=error");
                return;
            }

            // Optional convenience cookie: last viewed professor (30 days)
            try {
                Cookie ck = CookieUtils.make(
                        "last_prof",
                        prof.getFullName(),             // ensure Professor#getFullName() exists
                        30 * 24 * 60 * 60,              // 30 days
                        ctx,
                        false                           // set true if site is HTTPS only
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
        // Ratings should be POSTed; redirect GETs to listing
        resp.sendRedirect(ctx + "/do.professors");
    }

    /* ---------- helper methods ---------- */

    private static String pick(String... vals) {
        if (vals == null) return null;
        for (String v : vals) if (v != null && !v.trim().isEmpty()) return v.trim();
        return null;
       }

    private static String trim(String s) { return s == null ? null : s.trim(); }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

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

    private static String safe(String v) { return v == null ? "" : v.trim(); }
}
