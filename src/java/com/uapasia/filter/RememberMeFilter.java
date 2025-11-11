package com.uapasia.filter;

import com.uapasia.dao.RememberTokenDAO;
import com.uapasia.dao.impl.RememberTokenDAOImpl;
import com.uapasia.model.User;
import com.uapasia.util.CookieUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class RememberMeFilter implements Filter {

    @Override
    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain)
        throws IOException, ServletException {

    HttpServletRequest  req  = (HttpServletRequest) rq;
    HttpServletResponse resp = (HttpServletResponse) rs;

    HttpSession session = req.getSession(false);

    if (session == null || session.getAttribute("user") == null) {
        String token = CookieUtils.get(req, "REMEMBER");
        if (token != null && !token.isEmpty()) {
            try {
                RememberTokenDAO tokenDAO = new RememberTokenDAOImpl(); 
                User u = tokenDAO.findUserByToken(token);
                if (u != null) {
                    req.getSession(true).setAttribute("user", u);
                } else {
                    // stale token -> clear cookie (3 args)
                    CookieUtils.clear(resp, "REMEMBER", req.getContextPath());
                }
            } catch (Exception ignored) { /* continue unauthenticated */ }
        }
    }
    chain.doFilter(rq, rs);
    }
}
