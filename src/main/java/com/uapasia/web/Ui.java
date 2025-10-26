package com.uapasia.web;

import com.uapasia.model.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class Ui {

    private Ui() {
    }

    public static String header(HttpServletRequest req, String active) {
        String ctx = req.getContextPath();
        HttpSession s = req.getSession(false);
        User u = (s == null) ? null : (User) s.getAttribute("user");
        boolean loggedIn = (u != null);
        String who = loggedIn ? ("@" + esc(u.getUsername())) : "Guest";

        StringBuilder sb = new StringBuilder();
        sb.append("<nav class='top-nav'>")
                .append("<span class='greet'>Hi, ").append(who).append("!</span>")
                .append(link(ctx, "/", "Home", "home".equals(active)))
                .append(link(ctx, "/do.professors", "Professors", "professors".equals(active)))
                .append(link(ctx, "/do.newprofessors", "Add Professor", "newprof".equals(active)));

        if (loggedIn) {
            sb.append(link(ctx, "/do.profile", "Profile", "profile".equals(active)))
                    .append("<form style='display:inline' action='").append(ctx).append("/do.logout' method='post'>")
                    .append("<button type='submit'>Logout</button>")
                    .append("</form>");
        } else {
            sb.append(link(ctx, "/login.jsp", "Login", "login".equals(active)))
                    .append(link(ctx, "/register.jsp", "Register", "register".equals(active)));
        }

        sb.append("</nav>");
        return sb.toString();
    }

    public static String footer(HttpServletRequest req) {
        return "<footer class='site-footer'><span>Rate My Professor — Demo</span></footer>";
    }

    // helpers
    private static String link(String ctx, String path, String label, boolean active) {
        return "<a" + (active ? " class='active'" : "") + " href='" + ctx + path + "'>" + esc(label) + "</a>";
    }

    private static String link(String ctx, String path, String label, String activeKey) {
        // overload if you ever want to compute elsewhere
        return link(ctx, path, label, false);
    }

    private static String esc(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
