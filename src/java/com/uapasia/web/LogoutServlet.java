package com.uapasia.web;

import com.uapasia.dao.RememberTokenDAO;
import com.uapasia.factory.DAOFactory;
import com.uapasia.util.CookieUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/do.logout"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String ctx = req.getContextPath();                    // "" or "/ProjectWebDev"

        // Remove DB token if present
        String token = CookieUtils.get(req, "REMEMBER");
        if (token != null) {
            try {
                RememberTokenDAO tokenDAO = DAOFactory.tokens();
                tokenDAO.delete(token);
            } catch (Exception ignored) {}
        }

        // IMPORTANT: clear cookie using the SAME path used when creating it
        CookieUtils.clear(resp, "REMEMBER", (ctx.isEmpty() ? "/" : ctx));

        // Kill session
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();

        // Send back to login with a logged-out flag
        resp.sendRedirect(ctx + "/login.jsp?loggedout=1");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }
}
