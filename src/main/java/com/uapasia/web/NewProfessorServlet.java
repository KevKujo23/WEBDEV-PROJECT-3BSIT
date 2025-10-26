package com.uapasia.web;

import com.uapasia.model.User;
import com.uapasia.repo.ContextStore;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "NewProfessorServlet", urlPatterns = {"/do.newprofessors"})
public class NewProfessorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String ctx = req.getContextPath();
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<!doctype html><html><head><meta charset='utf-8'><title>Add Professor</title>"
                + "<link rel='stylesheet' href='" + ctx + "/css/styles.css'></head><body>");

        out.println("<nav class='top-nav'>"
                + "<a href='" + ctx + "/'>Home</a>"
                + "<a href='" + ctx + "/admin'>Admin</a>"
                + "<form style='display:inline' action='" + ctx + "/do.logout' method='post'>"
                + "<button type='submit'>Logout</button></form>"
                + "</nav>");

        out.println("<div class='panel-container'>");
        out.println("<h2>Add Professor</h2>");
        out.println("<form method='post' action='" + ctx + "/do.newprofessors' autocomplete='off'>");
        out.println("<div class='form-group'><label>Professor Name"
                + "<input type='text' name='name' required></label></div>");
        out.println("<div class='form-group'><label>Department"
                + "<select name='dept' required>"
                + "<option value=''>Select…</option>"
                + "<option>SSE</option><option>SMN</option><option>SLG</option>"
                + "<option>SCM</option><option>SEC</option>"
                + "</select></label></div>");
        out.println("<button type='submit'>Save</button>");
        out.println("</form></div></body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = trim(req.getParameter("name"));
        String dept = trim(req.getParameter("dept"));

        if (name == null || name.isEmpty() || dept == null || dept.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/do.newprofessors?status=invalid");
            return;
        }

        HttpSession s = req.getSession(false);
        String by = "admin";
        if (s != null && s.getAttribute("user") != null) {
            by = ((User) s.getAttribute("user")).getUsername();
        }

        ContextStore.addProfessor(getServletContext(), name, dept, by);
        resp.sendRedirect(req.getContextPath() + "/admin?status=added");
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
}
