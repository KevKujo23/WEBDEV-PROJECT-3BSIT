package com.uapasia.web;

import com.uapasia.model.User;
import com.uapasia.repo.ContextStore;
import com.uapasia.util.CookieUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "NewRatingServlet", urlPatterns = {"/do.ratings"})
public class NewRatingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession s = request.getSession(false);
        User user = (s == null) ? null : (User) s.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?status=unauthorized");
            return;
        }

        String profIdStr = request.getParameter("profId");
        String scoreStr = request.getParameter("score");
        String comment = request.getParameter("comment");

        int profId = parseInt(profIdStr);
        int score = parseInt(scoreStr);

        if (profId <= 0 || score < 1 || score > 5) {
            response.sendRedirect(request.getContextPath() + "/do.professors");
            return;
        }

        var prof = ContextStore.findProfessorById(getServletContext(), profId);
        if (prof == null) {
            response.sendRedirect(request.getContextPath() + "/do.professors");
            return;
        }

        String cleanComment = (comment == null) ? "" : comment.trim();
        ContextStore.addRating(getServletContext(), profId, score, cleanComment, user.getUsername());

        // optional nicety cookie
        try {
            response.addCookie(CookieUtils.make("last_prof", prof.getName(), 30 * 24 * 60 * 60));
        } catch (Throwable ignored) {
            /* if CookieUtils not present, ignore */ }

        response.sendRedirect(request.getContextPath() + "/do.professor.view?id=" + profId);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/do.professors");
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }
}
