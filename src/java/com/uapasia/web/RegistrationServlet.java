package com.uapasia.web;

import com.uapasia.dao.UserDAO;
import com.uapasia.factory.DAOFactory;
import com.uapasia.model.Role;
import com.uapasia.model.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "RegistrationServlet", urlPatterns = {"/do.register"})
public class RegistrationServlet extends HttpServlet {

    /* ------------ helpers ------------ */
    private static String trimOrNull(String s) {
        return (s == null) ? null : s.trim();
    }

    private static boolean emailOk(String e) {
        if (e == null) return false;
        String x = e.trim().toLowerCase();
        // must contain '@', have something before it, and end with @uap.asia
        return x.contains("@") && x.indexOf('@') > 0 && x.endsWith("@uap.asia")
               && x.length() > "@uap.asia".length();
    }

    private static boolean nonBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    /* ------------ routes ------------ */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            // already logged in -> go home
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        // show the register page (preserve any query string)
        String redirect = req.getContextPath() + "/register.jsp";
        if (req.getQueryString() != null && !req.getQueryString().isEmpty()) {
            redirect += "?" + req.getQueryString();
        }
        resp.sendRedirect(redirect);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String ctx = req.getContextPath();

        // read params
        String studentNumber = trimOrNull(req.getParameter("studentNumber"));
        String password      = trimOrNull(req.getParameter("password"));
        String confirm       = trimOrNull(req.getParameter("confirm"));
        String firstName     = trimOrNull(req.getParameter("firstName"));
        String lastName      = trimOrNull(req.getParameter("lastName"));
        String email         = trimOrNull(req.getParameter("email"));
        String deptIdStr     = trimOrNull(req.getParameter("deptId"));
        String yearLevelStr  = trimOrNull(req.getParameter("yearLevel"));

        int deptId    = parseInt(deptIdStr, 0);
        int yearLevel = parseInt(yearLevelStr, 1);

        // validate
        boolean ok = nonBlank(studentNumber)
                && nonBlank(firstName)
                && nonBlank(lastName)
                && emailOk(email)
                && nonBlank(password)
                && password.length() >= 5
                && password.equals(confirm)
                && deptId > 0;

        if (!ok) {
            resp.sendRedirect(ctx + "/register.jsp?status=invalid");
            return;
        }

        // DAO flow
        UserDAO userDAO = DAOFactory.users();
        try {
            // uniqueness checks
            if (userDAO.studentNumberExists(studentNumber) || userDAO.emailExists(email)) {
                resp.sendRedirect(ctx + "/register.jsp?status=exists");
                return;
            }

            // build user
            User u = new User();
            u.setStudentNumber(studentNumber);
            u.setEmail(email.toLowerCase());
            u.setPassword(password);              // plain text per your class rule
            u.setFullName(firstName + " " + lastName);
            u.setYearLevel(yearLevel);
            u.setDeptId(deptId);
            u.setRole(Role.USER);

            // create in DB
            int id = userDAO.create(u);
            if (id <= 0) {
                resp.sendRedirect(ctx + "/register.jsp?status=error");
                return;
            }

            // keep ID on the session user
            u.setUserId(id);

            // auto-login
            HttpSession session = req.getSession(true);
            session.setAttribute("user", u);

            // ✅ Option A: go to home with banner
            resp.sendRedirect(ctx + "/?status=registered");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(ctx + "/register.jsp?status=error");
        }
    }
}
