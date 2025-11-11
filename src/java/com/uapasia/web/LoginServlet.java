package com.uapasia.web;

import com.uapasia.dao.RememberTokenDAO;
import com.uapasia.dao.UserDAO;
import com.uapasia.factory.DAOFactory;
import com.uapasia.model.User;
import com.uapasia.model.Role;            // <-- ensure this import exists
import com.uapasia.util.CookieUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "LoginServlet", urlPatterns = {"/do.login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            // already logged in: route by role
            Object r = session.getAttribute("role");
            String ctx = req.getContextPath();
            if ("ADMIN".equals(r)) {
                resp.sendRedirect(ctx + "/admin");
            } else {
                resp.sendRedirect(ctx + "/");
            }
            return;
        }

        String redirect = req.getContextPath() + "/login.jsp";
        if (req.getQueryString() != null && !req.getQueryString().isEmpty()) {
            redirect += "?" + req.getQueryString();
        }
        resp.sendRedirect(redirect);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String login    = nvl(req.getParameter("login"), req.getParameter("username"));
        String password = req.getParameter("password");

        UserDAO userDAO = DAOFactory.users();
        RememberTokenDAO tokenDAO = DAOFactory.tokens();

        try {
            User found = userDAO.findByLoginAndPassword(login, password);
            if (found == null) {
                resp.sendRedirect(req.getContextPath() + "/login.jsp?error=1");
                return;
            }

            // Invalidate previous session (if any)
            HttpSession old = req.getSession(false);
            if (old != null) old.invalidate();

            // New session
            HttpSession session = req.getSession(true);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes
            session.setAttribute("user", found);

            // ---- add this: simple string for JSTL/EL checks (no scriptlets) ----
            Role role = found.getRole();
            session.setAttribute("role", role == null ? null : role.name()); // "ADMIN" / "STUDENT"
            // -------------------------------------------------------------------

            // Remember-me (optional)
            String remember = req.getParameter("remember");
            if ("on".equalsIgnoreCase(remember) || "1".equals(remember)) {
                String token = UUID.randomUUID().toString();
                tokenDAO.insert(found.getUserId(), token);
                Cookie ck = CookieUtils.make("REMEMBER", token, 60 * 60 * 24 * 30, req.getContextPath(), false);
                resp.addCookie(ck);
            }

            // Route by role
            String ctx = req.getContextPath();
            if (role == Role.ADMIN) {
                resp.sendRedirect(ctx + "/admin");
            } else {
                resp.sendRedirect(ctx + "/");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/login.jsp?status=error");
        }
    }

    private static String nvl(String a, String b) {
        return (a != null && !a.isEmpty()) ? a : b;
    }
}
