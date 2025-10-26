package com.uapasia.filters;

import com.uapasia.model.User;
import com.uapasia.repo.ContextStore;
import com.uapasia.util.CookieUtils;
import javax.servlet.annotation.WebFilter;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebFilter(urlPatterns={"/*"})
public class RememberMeFilter implements Filter {
    @Override public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain fc) throws IOException, ServletException {
        HttpServletRequest req=(HttpServletRequest)rq;
        HttpSession s=req.getSession(false);
        if(s==null || s.getAttribute("user")==null){
            String uname = CookieUtils.get(req, "remember_username");
            if(uname!=null){
                List<User> users = ContextStore.users(req.getServletContext());
                User found = users.stream().filter(u->u.getUsername().equals(uname)).findFirst().orElse(null);
                if(found!=null){ HttpSession ns=req.getSession(true); ns.setAttribute("user", found); ns.setMaxInactiveInterval(15*60); }
            }
        }
        fc.doFilter(rq, rs);
    }
}
