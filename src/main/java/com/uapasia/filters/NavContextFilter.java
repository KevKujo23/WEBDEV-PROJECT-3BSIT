package com.uapasia.filters;

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

        req.setAttribute("ctx", req.getContextPath());
        req.setAttribute("loggedIn", u != null);
        req.setAttribute("who", (u == null) ? "Guest" : ("@" + u.getUsername()));

        // optional: active tab (based on request path)
        String p = req.getRequestURI();
        String active =
                p.contains("/do.professors") ? "professors" :
                        p.contains("/do.newprofessors") ? "newprof" :
                                p.contains("/do.profile") ? "profile" :
                                        p.endsWith("/") || p.endsWith("/index.jsp") ? "home" :
                                                p.contains("login") ? "login" :
                                                        p.contains("register") ? "register" : "";
        req.setAttribute("active", active);

        fc.doFilter(rq, rs);
    }
}
