package com.uapasia.web;

import com.uapasia.model.*;
import com.uapasia.repo.ContextStore;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(name = "ProfileServlet", urlPatterns = {"/do.profile"})
public class ProfileServlet extends HttpServlet {

    private static String safe(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = (User) req.getSession().getAttribute("user");
        if (u == null) {
            resp.sendRedirect(req.getContextPath() + "/do.login");
            return;
        }

        String ctx = req.getContextPath();
        List<Rating> mine = ContextStore.ratingsByUser(getServletContext(), u.getUsername());
        SimpleDateFormat fmt = new SimpleDateFormat("MMM d, yyyy h:mm a");

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>My Profile</title>");
            out.println("<link rel='stylesheet' href='" + ctx + "/css/styles.css'>");
            out.println("</head><body class='panel'>");

            out.println(Ui.header(req, "profile"));

            out.println("<div class='panel-container'>");
            out.println("<h2>" + safe(u.getFullName()) + "</h2>");
            out.println("<p class='note'>" + safe(u.getEmail()) + " • " + safe(u.getDepartment()) + "</p>");

            out.println("<h3>My Ratings</h3><div class='feed'>");
            if (mine.isEmpty()) {
                out.println("<p class='note'>No ratings yet.</p>");
            } else {
                for (Rating r : mine) {
                    Professor p = ContextStore.findProfessorById(getServletContext(), r.getProfId());
                    out.println("<div class='post'>");
                    out.println("<div class='meta'>" + (p == null ? "Unknown professor" : safe(p.getName())) + " • " + fmt.format(new Date(r.getCreatedAt()))
                            + " • <span class='score-badge'>" + r.getScore() + "/5</span></div>");
                    out.println("<div>" + safe(r.getComment()) + "</div>");
                    if (p != null) {
                        out.println("<div class='note'><a href='" + ctx + "/do.professor.view?id=" + p.getId() + "'>Open thread</a></div>");
                    }
                    out.println("</div>");
                }
            }
            out.println("</div></div>");
            out.println(Ui.header(req, "profile"));
            out.println("</body></html>");
        }
    }
}
