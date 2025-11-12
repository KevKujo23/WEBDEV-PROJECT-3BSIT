package com.uapasia.web;

import com.uapasia.model.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet(name = "ProfileServlet", urlPatterns = {"/do.profile"})
public class ProfileServlet extends HttpServlet {

    private static String safe(String s){
        if(s==null) return "";
        return s.replace("&","&amp;")
                .replace("<","&lt;")
                .replace(">","&gt;")
                .replace("\"","&quot;")
                .replace("'","&#39;");
    }

    private static String href(String ctx, String path) {
        // path must start with '/', ctx is empty or like '/ProjectWebDev'
        return (ctx == null ? "" : ctx) + path;
    }

    private static String stars(int score){ // 1..5 -> ★★★☆☆
        int s = Math.max(0, Math.min(5, score));
        StringBuilder b = new StringBuilder(5);
        for(int i=0;i<s;i++) b.append('★');
        for(int i=s;i<5;i++) b.append('☆');
        return b.toString();
    }

    static final class MyRating {
        int ratingId;
        int profId;
        String profName;
        int score;
        String comment;
        Instant createdAt;
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User u = (session == null) ? null : (User) session.getAttribute("user");
        if (u == null) {
            resp.sendRedirect(href(req.getContextPath(), "/login.jsp?status=unauthorized"));
            return;
        }

        final String ctx = req.getContextPath(); // "" or "/ProjectWebDev"

        // 1) Department name for the user
        String deptName = "";
        try (Connection c = com.uapasia.dao.util.DB.get();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT name FROM departments WHERE dept_id=?")) {
            ps.setInt(1, u.getDeptId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) deptName = rs.getString("name");
            }
        } catch (Exception ignored) { }

        // 2) Load my ratings (join with professors for display)
        List<MyRating> mine = new ArrayList<>();
        try (Connection c = com.uapasia.dao.util.DB.get();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT r.rating_id, r.prof_id, r.score, r.comment, r.created_at, " +
                     "       CONCAT(p.first_name,' ',p.last_name) AS prof_name " +
                     "FROM ratings r JOIN professors p ON p.prof_id = r.prof_id " +
                     "WHERE r.user_id=? ORDER BY r.created_at DESC")) {
            ps.setInt(1, u.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MyRating r = new MyRating();
                    r.ratingId = rs.getInt("rating_id");
                    r.profId   = rs.getInt("prof_id");
                    r.profName = rs.getString("prof_name");
                    r.score    = rs.getInt("score");
                    r.comment  = rs.getString("comment");
                    Timestamp ts = rs.getTimestamp("created_at");
                    r.createdAt = (ts==null? null : ts.toInstant());
                    mine.add(r);
                }
            }
        } catch (Exception ignored) { }

        SimpleDateFormat fmt = new SimpleDateFormat("MMM d, yyyy h:mm a");

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>My Profile</title>");
            out.println("<link rel='stylesheet' href='" + href(ctx, "/css/styles.css") + "'>");
            out.println("<style>");
            out.println(".profile-header{display:flex;align-items:center;gap:1rem;margin-bottom:1rem}");
            out.println(".avatar{width:56px;height:56px;border-radius:50%;background:#ddd;display:inline-block}");
            out.println(".muted{color:#666;font-size:.95rem}");
            out.println(".feed{display:grid;gap:12px;margin-top:12px}");
            out.println(".card{border:1px solid #e6e6e6;border-radius:10px;padding:12px;background:#fff}");
            out.println(".meta{font-size:.9rem;color:#555;display:flex;flex-wrap:wrap;gap:.5rem;align-items:center}");
            out.println(".score{font-weight:600}");
            out.println(".actions a{font-size:.9rem}");
            out.println("</style>");
            out.println("</head><body class='panel'>");

            // Top nav (if you have a shared header util, keep that instead)
            out.println(Ui.header(req, "profile"));

            out.println("<div class='panel-container'>");

            // Profile header
            out.println("<div class='profile-header'>");
            out.println("<span class='avatar' aria-hidden='true'></span>");
            out.println("<div>");
            out.println("<h2 style='margin:0'>" + safe(u.getFullName()) + "</h2>");
            out.println("<div class='muted'>" + safe(u.getEmail())
                    + (deptName == null || deptName.isEmpty() ? "" : " • " + safe(deptName)) + "</div>");
            out.println("</div></div>");

            // Ratings section
            out.println("<h3 style='margin-top:20px'>My Ratings</h3>");
            if (mine.isEmpty()) {
                out.println("<p class='muted'>You haven’t posted any ratings yet.</p>");
                out.println("<div class='actions'><a href='" + href(ctx, "/do.professors") + "'>Browse professors</a></div>");
            } else {
                out.println("<div class='feed'>");
                for (MyRating r : mine) {
                    String when = (r.createdAt == null) ? "" : fmt.format(Date.from(r.createdAt));
                    out.println("<article class='card'>");
                    out.println("  <div class='meta'>");
                    out.println("    <span><strong>" + safe((r.profName==null? "Unknown professor" : r.profName)) + "</strong></span>");
                    out.println("    <span>•</span>");
                    out.println("    <span class='score' title='" + r.score + "/5'>" + stars(r.score) + "</span>");
                    if (!when.isEmpty()) {
                        out.println("    <span>•</span><span>" + when + "</span>");
                    }
                    out.println("  </div>");
                    String body = safe((r.comment == null || r.comment.isEmpty()) ? "(No comment)" : r.comment);
                    out.println("  <p style='margin:.5rem 0 0'>" + body + "</p>");
                    out.println("  <div class='actions' style='margin-top:.5rem'>");
                    out.println("    <a href='" + href(ctx, "/do.professor.view?id=" + r.profId) + "'>Open thread</a>");
                    out.println("  </div>");
                    out.println("</article>");
                }
                out.println("</div>");
            }

            out.println("</div>"); // /panel-container

            // Footer (don’t print header again)
            // If you have Ui.footer, use it; otherwise omit.
            out.println(Ui.footer(req));

            out.println("</body></html>");
        }
    }
}
