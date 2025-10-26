package com.uapasia.filters;

import com.uapasia.model.User;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {"/do.newprofessors", "/do.ratings", "/do.profile", "/do.logout"})
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain fc) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) rq;
        HttpServletResponse resp = (HttpServletResponse) rs;

        HttpSession s = req.getSession(false);
        User u = (s == null) ? null : (User) s.getAttribute("user");
        if (u == null) {
            // redirect to login with a status flag -> login.jsp will show a prompt (no scriptlets/JSTL)
            resp.sendRedirect(req.getContextPath() + "/login.jsp?status=unauthorized");
            return;
        }
        fc.doFilter(rq, rs);
    }
}
