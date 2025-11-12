package com.uapasia.web;

import com.uapasia.dao.RatingDAO;
import com.uapasia.factory.DAOFactory;
import com.uapasia.model.*;

import javax.servlet.http.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "EditRatingServlet", urlPatterns = {"/do.ratings.edit"})
public class EditRatingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("user");
        final String ctx = req.getContextPath();

        // Require login
        if (user == null) {
            resp.sendRedirect(ctx + "/login.jsp?status=unauthorized");
            return;
        }

        // Normal users only (admins cannot edit ratings)
        if (user.getRole() == Role.ADMIN) {
            resp.sendRedirect(ctx + "/index.jsp?status=forbidden");
            return;
        }

        int ratingId = parseInt(req.getParameter("ratingId"), 0);
        int profId   = parseInt(req.getParameter("profId"), 0); // used in redirect
        int score    = parseInt(req.getParameter("score"), 0);
        String comment = trim(req.getParameter("comment"));

        // Validation
        if (ratingId <= 0 || profId <= 0 || score < 1 || score > 5) {
            resp.sendRedirect(ctx + "/professor.jsp?id=" + profId + "&status=missing");
            return;
        }
        if (wordCount(comment) > 300) {
            resp.sendRedirect(ctx + "/professor.jsp?id=" + profId + "&status=toolong");
            return;
        }

        RatingDAO dao = DAOFactory.ratings();

        try {
            // Optional pre-check for ownership/time-lock
            if (!dao.canEdit(ratingId, user.getUserId())) {
                resp.sendRedirect(ctx + "/professor.jsp?id=" + profId + "&status=locked");
                return;
            }

            int updated = dao.updateCommentAndScore(ratingId, user.getUserId(), score, comment);
            if (updated != 1) {
                resp.sendRedirect(ctx + "/professor.jsp?id=" + profId + "&status=locked");
                return;
            }

            resp.sendRedirect(ctx + "/professor.jsp?id=" + profId + "&status=updated");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(ctx + "/professor.jsp?id=" + profId + "&status=error");
        }
    }

    /* ---------- helpers ---------- */
    private static int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    private static int wordCount(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        return s.trim().split("\\s+").length;
    }
}
