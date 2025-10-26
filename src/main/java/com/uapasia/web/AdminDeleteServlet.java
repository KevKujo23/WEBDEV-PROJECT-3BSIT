package com.uapasia.web;

import com.uapasia.repo.ContextStore;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "AdminDeleteServlet", urlPatterns = {"/do.admin.delete"})
public class AdminDeleteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String type = req.getParameter("type");
        String id = req.getParameter("id");

        if ("professor".equals(type)) {
            ContextStore.deleteProfessor(getServletContext(), id);
        } else if ("rating".equals(type)) {
            ContextStore.deleteRating(getServletContext(), id);
        }
        resp.sendRedirect(req.getContextPath() + "/admin?status=deleted");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Prefer POST; fallback to POST behavior
        doPost(req, resp);
    }
}
