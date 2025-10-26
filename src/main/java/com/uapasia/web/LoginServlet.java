package com.uapasia.web;

import com.uapasia.model.User;
import com.uapasia.repo.ContextStore;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name="LoginServlet", urlPatterns={"/do.login"})
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/");
        } else {
            resp.sendRedirect(req.getContextPath() + "/login.jsp"
                    + (req.getQueryString()==null ? "" : "?" + req.getQueryString()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String u = req.getParameter("username");
        String p = req.getParameter("password");

        User found = ContextStore.findUserByUsername(getServletContext(), u);
        boolean ok = (found != null) && found.getPassword().equals(p);

        if (!ok) {
            resp.sendRedirect(req.getContextPath()+"/login.jsp?error=1");
            return;
        }

        // rotate session to avoid fixation
        HttpSession old = req.getSession(false);
        if (old != null) old.invalidate();

        HttpSession s = req.getSession(true);
        s.setMaxInactiveInterval(30 * 60); // 30 mins
        s.setAttribute("user", found);

        // remember me checkbox (optional)
        if ("on".equalsIgnoreCase(req.getParameter("remember")) || "1".equals(req.getParameter("remember"))) {
            Cookie c = new Cookie("remember_user", found.getUsername());
            c.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
            c.setMaxAge(7 * 24 * 60 * 60); // 7 days
            c.setHttpOnly(true);
            resp.addCookie(c);
        }

        resp.sendRedirect(req.getContextPath() + "/");
    }
}
