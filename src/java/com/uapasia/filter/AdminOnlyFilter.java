package com.uapasia.filter;

import com.uapasia.model.Role;
import com.uapasia.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

@WebFilter({"/admin", "/do.admin.*"})
public class AdminOnlyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain fc)
            throws IOException, ServletException {

        HttpServletRequest req  = (HttpServletRequest) rq;
        HttpServletResponse resp = (HttpServletResponse) rs;

        HttpSession s = req.getSession(false);
        User u = (s == null) ? null : (User) s.getAttribute("user");

        boolean isAdmin = (u != null && u.getRole() == Role.ADMIN);
        if (!isAdmin) {
            // Redirect to login if not logged in or not admin
            resp.sendRedirect(req.getContextPath() + "/login.jsp?status=unauthorized");
            return;
        }

        fc.doFilter(rq, rs);
    }
}
