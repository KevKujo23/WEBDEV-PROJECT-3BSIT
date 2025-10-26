package com.uapasia.web;

import com.uapasia.model.Professor;
import com.uapasia.model.Rating;
import com.uapasia.repo.ContextStore;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(name = "ProfessorViewServlet", urlPatterns = {"/do.professor.view"})
public class ProfessorViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String ctx = req.getContextPath();
        String idStr = req.getParameter("id");
        int id = parseInt(idStr);
        Professor prof = ContextStore.findProfessorById(getServletContext(), id);
        if (prof == null) {
            resp.sendRedirect(ctx + "/do.professors");
            return;
        }

        List<Rating> list = ContextStore.ratingsForProfessor(getServletContext(), id);
        double avg = ContextStore.averageScore(getServletContext(), id);
        String avgTxt = list.isEmpty() ? "—" : new DecimalFormat("0.0").format(avg);

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<!doctype html><html><head><meta charset='utf-8'><title>"
                + esc(prof.getName()) + " • Professor</title>"
                + "<link rel='stylesheet' href='" + ctx + "/css/styles.css'></head><body>");

        out.println("<nav class='top-nav'>"
                + "<a href='" + ctx + "/'>Home</a>"
                + "<a href='" + ctx + "/do.professors'>Professors</a>"
                + "</nav>");

        out.println("<div class='panel-container'>");
        out.println("<h2>" + esc(prof.getName()) + " <span class='score-badge'>Avg: " + avgTxt + "</span></h2>");
        out.println("<p class='note'>Department: " + esc(prof.getDept())
                + " • Submitted by: " + esc(n2e(prof.getSubmittedBy())) + "</p>");

        // Add Rating form (POST -> /do.ratings). AuthFilter will require login.
        out.println("<h3 style='margin-top:20px'>Add Rating</h3>");
        out.println("<form method='post' action='" + ctx + "/do.ratings' autocomplete='off'>"
                + "<input type='hidden' name='profId' value='" + prof.getId() + "'/>"
                + "<div class='form-group'><label>Score (1–5)"
                + "<input type='number' name='score' min='1' max='5' required></label></div>"
                + "<div class='form-group'><label>Comment (optional)"
                + "<textarea name='comment' placeholder='Share your thoughts...'></textarea></label></div>"
                + "<button type='submit'>Submit Rating</button>"
                + "</form>");

        // Existing ratings
        out.println("<h3 style='margin-top:24px'>Ratings</h3>");
        if (list.isEmpty()) {
            out.println("<p class='note'>No ratings yet. Be the first to add one!</p>");
        } else {
            out.println("<table><thead><tr><th>ID</th><th>Score</th><th>Comment</th><th>By</th><th>When</th></tr></thead><tbody>");
            for (Rating r : list) {
                out.println("<tr>"
                        + "<td>" + r.getId() + "</td>"
                        + "<td>" + r.getScore() + "</td>"
                        + "<td>" + esc(n2e(r.getComment())) + "</td>"
                        + "<td>" + esc(n2e(r.getByUser())) + "</td>"
                        + "<td>" + fmt(r.getCreatedAt()) + "</td>"
                        + "</tr>");
            }
            out.println("</tbody></table>");
        }

        out.println("</div></body></html>");
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private static String esc(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private static String n2e(String s) {
        return s == null ? "" : s;
    }

    private static String fmt(long ms) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(ms));
        } catch (Exception e) {
            return String.valueOf(ms);
        }
    }
}
