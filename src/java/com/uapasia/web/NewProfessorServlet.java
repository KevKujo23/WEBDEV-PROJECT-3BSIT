package com.uapasia.web;

import com.uapasia.dao.DepartmentDAO;
import com.uapasia.factory.DAOFactory;
import com.uapasia.model.Department;
import com.uapasia.model.Role;
import com.uapasia.model.User;

import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "NewProfessorServlet", urlPatterns = {"/do.newprofessors"})
public class NewProfessorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String ctx = req.getContextPath();

        // Require admin
        HttpSession session = req.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");
        if (user == null || user.getRole() != Role.ADMIN) {
            resp.sendRedirect(ctx + "/login.jsp?status=unauthorized");
            return;
        }

        // Load departments from DB
        List<Department> depts;
        try {
            DepartmentDAO deptDAO = DAOFactory.departments();
            depts = deptDAO.listAll();
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(ctx + "/admin?status=dept_error");
            return;
        }

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<!doctype html><html><head><meta charset='utf-8'><title>Add Professor</title>"
                + "<link rel='stylesheet' href='" + ctx + "/css/styles.css'></head><body>");

        out.println("<nav class='top-nav'>"
                + "<a href='" + ctx + "/'>Home</a>"
                + "<a href='" + ctx + "/admin'>Admin</a>"
                + "<form style='display:inline' action='" + ctx + "/do.logout' method='get'>"
                + "<button type='submit'>Logout</button></form>"
                + "</nav>");

        out.println("<div class='panel-container'>");
        out.println("<h2>Add Professor</h2>");

        // Simple form: single name field + dept dropdown
        out.println("<form method='post' action='" + ctx + "/do.newprofessors' autocomplete='off'>");
        out.println("<div class='form-group'><label>Professor Name"
                + "<input type='text' name='name' placeholder='e.g. Juan Dela Cruz' required></label></div>");

        out.println("<div class='form-group'><label>Department"
                + "<select name='deptId' required><option value=''>Select…</option>");
        for (Department d : depts) {
            out.println("<option value='" + d.getDeptId() + "'>" + esc(d.getName()) + "</option>");
        }
        out.println("</select></label></div>");

        out.println("<button type='submit'>Save</button>");
        out.println("</form></div></body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String ctx = req.getContextPath();

        // Require admin
        HttpSession session = req.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");
        if (user == null || user.getRole() != Role.ADMIN) {
            resp.sendRedirect(ctx + "/login.jsp?status=unauthorized");
            return;
        }

        String name = trim(req.getParameter("name"));
        int deptId = parseInt(req.getParameter("deptId"), 0);

        if (isBlank(name) || deptId <= 0) {
            resp.sendRedirect(ctx + "/do.newprofessors?status=invalid");
            return;
        }

        // Split full name into first/last
        String[] parts = splitName(name);
        String first = parts[0];
        String last = parts[1];

        // Insert into DB
        try (java.sql.Connection c = com.uapasia.dao.util.DB.get();
             java.sql.PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO professors(first_name, last_name, dept_id) VALUES (?,?,?)")) {
            ps.setString(1, first);
            ps.setString(2, last);
            ps.setInt(3, deptId);
            int n = ps.executeUpdate();
            if (n <= 0) {
                resp.sendRedirect(ctx + "/do.newprofessors?status=error");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(ctx + "/do.newprofessors?status=error");
            return;
        }

        resp.sendRedirect(ctx + "/admin?status=added");
    }

    /* ---------- helpers ---------- */
    private static String trim(String s) { return s == null ? null : s.trim(); }
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static int parseInt(String s, int def) { try { return Integer.parseInt(s); } catch (Exception e) { return def; } }
    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    /** Split "Full Name" into first/last by last space */
    private static String[] splitName(String full) {
        String f = full.trim();
        int i = f.lastIndexOf(' ');
        if (i <= 0) return new String[]{f, ""};
        String first = f.substring(0, i).trim();
        String last = f.substring(i + 1).trim();
        return new String[]{first, last};
    }
}
