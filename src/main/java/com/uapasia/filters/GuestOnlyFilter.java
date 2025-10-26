package com.uapasia.filters;

import com.uapasia.model.User;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {"/login.jsp", "/register.jsp", "/do.login", "/do.register"})
public class GuestOnlyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain fc) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) rq;
        HttpServletResponse resp = (HttpServletResponse) rs;

        HttpSession s = req.getSession(false);
        User u = (s == null) ? null : (User) s.getAttribute("user");
        if (u != null) {
            // already logged in → bounce to home
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        fc.doFilter(rq, rs);
    }
}
