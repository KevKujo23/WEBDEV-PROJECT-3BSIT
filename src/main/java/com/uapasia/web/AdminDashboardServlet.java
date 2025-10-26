package com.uapasia.web;

import com.uapasia.model.Professor;
import com.uapasia.model.Rating;
import com.uapasia.repo.ContextStore;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin"})
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String ctx = req.getContextPath();
        List<Professor> profs = ContextStore.professors(getServletContext());
        List<Rating> ratings = ContextStore.ratings(getServletContext());

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<!doctype html><html><head><meta charset='utf-8'><title>Admin Dashboard</title>"
                + "<link rel='stylesheet' href='" + ctx + "/css/styles.css'></head><body>");

        // Minimal nav (servlet-safe)
        out.println("<nav class='top-nav'>"
                + "<a href='" + ctx + "/'>Home</a>"
                + "<a href='" + ctx + "/admin'>Admin</a>"
                + "<form style='display:inline' action='" + ctx + "/do.logout' method='post'>"
                + "<button type='submit'>Logout</button></form>"
                + "</nav>");

        out.println("<div class='panel-container'>");
        out.println("<h2>Admin Dashboard</h2>");

        /* -------- Professors ---------- */
        out.println("<h3>Professors</h3>");
        out.println("<table><thead>"
                + "<tr><th>ID</th><th>Name</th><th>Dept</th><th>Submitted By</th><th>Actions</th></tr>"
                + "</thead><tbody>");
        for (Professor p : profs) {
            String id = String.valueOf(p.getId());
            String name = esc(p.getName());
            String dept = esc(p.getDept());
            String by = esc(nullToEmpty(p.getSubmittedBy()));
            out.println("<tr>"
                    + "<td>" + id + "</td>"
                    + "<td>" + name + "</td>"
                    + "<td>" + dept + "</td>"
                    + "<td>" + by + "</td>"
                    + "<td>"
                    + "<form method='post' action='" + ctx + "/do.admin.delete' style='display:inline'>"
                    + "<input type='hidden' name='type' value='professor'/>"
                    + "<input type='hidden' name='id' value='" + id + "'/>"
                    + "<button type='submit'>Delete</button>"
                    + "</form>"
                    + "</td>"
                    + "</tr>");
        }
        out.println("</tbody></table>");

        /* -------- Ratings ---------- */
        out.println("<h3 style='margin-top:24px'>Ratings</h3>");
        out.println("<table><thead>"
                + "<tr><th>ID</th><th>Prof ID</th><th>Score</th><th>Comment</th><th>By User</th>"
                + "<th>Created</th><th>Actions</th></tr>"
                + "</thead><tbody>");
        for (Rating r : ratings) {
            String id = String.valueOf(r.getId());
            String cmt = esc(nullToEmpty(r.getComment()));
            String by = esc(nullToEmpty(r.getByUser()));
            String when = formatTs(r.getCreatedAt());
            out.println("<tr>"
                    + "<td>" + id + "</td>"
                    + "<td>" + r.getProfId() + "</td>"
                    + "<td>" + r.getScore() + "</td>"
                    + "<td>" + cmt + "</td>"
                    + "<td>" + by + "</td>"
                    + "<td>" + when + "</td>"
                    + "<td>"
                    + "<form method='post' action='" + ctx + "/do.admin.delete' style='display:inline'>"
                    + "<input type='hidden' name='type' value='rating'/>"
                    + "<input type='hidden' name='id' value='" + id + "'/>"
                    + "<button type='submit'>Delete</button>"
                    + "</form>"
                    + "</td>"
                    + "</tr>");
        }
        out.println("</tbody></table>");

        out.println("</div></body></html>");
    }

    private static String esc(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String formatTs(long ms) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(ms));
        } catch (Exception e) {
            return String.valueOf(ms);
        }
    }
}
