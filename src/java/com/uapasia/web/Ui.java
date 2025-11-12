package com.uapasia.web;

import com.uapasia.model.Role;
import com.uapasia.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class Ui {
    private Ui() {}

    public static String header(HttpServletRequest req, String active) {
        String ctx = req.getContextPath();
        if (ctx == null) ctx = "";
        if (!ctx.endsWith("/")) ctx += "/";

        HttpSession s = req.getSession(false);
        User u = (s == null) ? null : (User) s.getAttribute("user");
        boolean loggedIn = (u != null);
        boolean isAdmin  = loggedIn && u.getRole() == Role.ADMIN;

        String who = "Guest";
        if (loggedIn) {
            String handle = (u.getStudentNumber() != null && !u.getStudentNumber().isEmpty())
                    ? u.getStudentNumber()
                    : (u.getEmail() == null ? "" : u.getEmail());
            who = "@" + esc(handle);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<nav class='top-nav'>")
          .append("<span class='greet'>Hi, ").append(who).append("!</span>")
          .append(link(ctx, "", "Home", "home".equals(active)))
          .append(link(ctx, "do.professors", "Professors", "professors".equals(active)));

        // Admin-only nav
        if (isAdmin) {
            sb.append(link(ctx, "do.newprofessors", "Add Professor", "newprof".equals(active)))
              .append(link(ctx, "admin", "Admin", "admin".equals(active)));
        }

        if (loggedIn) {
            sb.append(link(ctx, "do.profile", "Profile", "profile".equals(active)))
              // Logout via GET (our renewed LogoutServlet handles GET)
              .append("<form style='display:inline' action='").append(ctx).append("do.logout' method='get'>")
              .append("<button type='submit'>Logout</button>")
              .append("</form>");
        } else {
            sb.append(link(ctx, "login.jsp", "Login", "login".equals(active)))
              .append(link(ctx, "register.jsp", "Register", "register".equals(active)));
        }

        sb.append("</nav>");
        return sb.toString();
    }

    public static String footer(HttpServletRequest req) {
        return "<footer class='site-footer'><span>Rate My Professor — Demo</span></footer>";
    }

    // helpers
    private static String link(String ctx, String path, String label, boolean active) {
        String href = ctx + path;
        return "<a" + (active ? " class='active'" : "") + " href='" + href + "'>" + esc(label) + "</a>";
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;")
                .replace("<","&lt;")
                .replace(">","&gt;")
                .replace("\"","&quot;")
                .replace("'","&#39;");
    }
}
