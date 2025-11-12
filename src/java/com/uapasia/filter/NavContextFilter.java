package com.uapasia.filter;

import com.uapasia.model.Role;
import com.uapasia.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

@WebFilter("/*")
public class NavContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain fc)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) rq;
        HttpSession s = req.getSession(false);
        User u = (s == null) ? null : (User) s.getAttribute("user");

        // --- Context info for JSPs / includes ---
        req.setAttribute("ctx", req.getContextPath());
        req.setAttribute("loggedIn", u != null);
        req.setAttribute("user", u);
        req.setAttribute("isAdmin", u != null && u.getRole() == Role.ADMIN);

        // display name: student number or email
        String who = "Guest";
        if (u != null) {
            String handle = (u.getStudentNumber() != null && !u.getStudentNumber().isEmpty())
                    ? u.getStudentNumber()
                    : (u.getEmail() == null ? "" : u.getEmail());
            who = "@" + handle;
        }
        req.setAttribute("who", who);

        // optional: determine active tab by path
        String uri = req.getRequestURI();
        String active =
                uri.contains("/do.professors") ? "professors" :
                uri.contains("/do.newprofessors") ? "newprof" :
                uri.contains("/do.profile") ? "profile" :
                uri.contains("/admin") ? "admin" :
                uri.endsWith("/") || uri.endsWith("/index.jsp") ? "home" :
                uri.contains("login") ? "login" :
                uri.contains("register") ? "register" : "";
        req.setAttribute("active", active);

        fc.doFilter(rq, rs);
    }
}
