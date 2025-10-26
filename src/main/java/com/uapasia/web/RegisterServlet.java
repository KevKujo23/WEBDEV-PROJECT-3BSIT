package com.uapasia.web;

import com.uapasia.model.User;
import com.uapasia.repo.ContextStore;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/do.register"})
public class RegisterServlet extends HttpServlet {

    private static final Set<String> DEPTS = Set.of("SSE", "SMN", "SLG", "SCM", "SEC");

    private static String trimOrNull(String s) {
        return (s == null) ? null : s.trim();
    }

    private static boolean emailOk(String e) {
        if (e == null) {
            return false;
        }
        String x = e.trim().toLowerCase();
        return x.endsWith("@uap.asia") && x.contains("@") && x.indexOf('@') > 0 && x.length() > "@uap.asia".length();
    }

    private static boolean nonBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/register.jsp"
                + (req.getQueryString() == null ? "" : "?" + req.getQueryString()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = trimOrNull(req.getParameter("username"));
        String password = trimOrNull(req.getParameter("password"));
        String confirm = trimOrNull(req.getParameter("confirm"));
        String firstName = trimOrNull(req.getParameter("firstName"));
        String lastName = trimOrNull(req.getParameter("lastName"));
        String email = trimOrNull(req.getParameter("email"));
        String department = trimOrNull(req.getParameter("department"));

        boolean ok = nonBlank(username)
                && nonBlank(firstName)
                && nonBlank(lastName)
                && emailOk(email)
                && nonBlank(password)
                && password.length() >= 5
                && password.equals(confirm)
                && department != null
                && DEPTS.contains(department);

        if (ok) {
            List<User> users = ContextStore.users(getServletContext());
            boolean exists = users.stream()
                    .anyMatch(x -> x.getUsername() != null && x.getUsername().equalsIgnoreCase(username));
            if (!exists) {
                users.add(new User(username, password, "student",
                        firstName, lastName, email.toLowerCase(), department));
            } else {
                ok = false;
            }
        }

        if (ok) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?status=registered");
        } else {
            resp.sendRedirect(req.getContextPath() + "/register.jsp?status=invalid");
        }
    }
}
