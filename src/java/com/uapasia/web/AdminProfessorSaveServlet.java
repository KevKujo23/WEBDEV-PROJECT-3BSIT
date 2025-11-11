package com.uapasia.web;

import com.uapasia.model.Role;
import com.uapasia.model.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet(name="AdminProfessorSaveServlet", urlPatterns={"/do.admin.professor.save"})
public class AdminProfessorSaveServlet extends HttpServlet {

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String ctx = req.getContextPath();

        // Admin only
        HttpSession s = req.getSession(false);
        User u = (s==null)?null:(User)s.getAttribute("user");
        if (u==null || u.getRole()!=Role.ADMIN) {
            resp.sendRedirect(ctx + "/login.jsp?status=unauthorized");
            return;
        }

        String idStr = trim(req.getParameter("id"));           // empty => insert
        String name  = trim(req.getParameter("name"));         // "First Last"
        String dept  = trim(req.getParameter("departmentId")); // numeric id

        if (name.isEmpty() || dept.isEmpty()) {
            resp.sendRedirect(ctx + "/admin?status=invalid");
            return;
        }

        String first = name, last = "";
        int i = name.lastIndexOf(' ');
        if (i > 0) { first = name.substring(0, i).trim(); last = name.substring(i+1).trim(); }

        try (Connection c = com.uapasia.dao.util.DB.get()) {
            if (idStr == null || idStr.isBlank()) {
                // INSERT
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO professors(first_name,last_name,dept_id) VALUES (?,?,?)")) {
                    ps.setString(1, first);
                    ps.setString(2, last);
                    ps.setInt(3, Integer.parseInt(dept));
                    ps.executeUpdate();
                }
                resp.sendRedirect(ctx + "/admin?status=added");
            } else {
                // UPDATE
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE professors SET first_name=?, last_name=?, dept_id=? WHERE prof_id=?")) {
                    ps.setString(1, first);
                    ps.setString(2, last);
                    ps.setInt(3, Integer.parseInt(dept));
                    ps.setInt(4, Integer.parseInt(idStr));
                    int n = ps.executeUpdate();
                    resp.sendRedirect(ctx + (n>0 ? "/admin?status=updated" : "/admin?status=notfound"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(ctx + "/admin?status=error");
        }
    }

    private static String trim(String s){ return s==null? "" : s.trim(); }
}
