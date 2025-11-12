package com.uapasia.web;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name="AdminProfessorSaveServlet", urlPatterns={"/do.admin.professor.save"})
public class AdminProfessorSaveServlet extends HttpServlet {

  @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String id   = req.getParameter("id");
    String name = req.getParameter("name");
    String dept = req.getParameter("departmentId");
    String[] subjectIds = req.getParameterValues("subjectIds");

    int deptId = Integer.parseInt(dept);
    String[] parts = (name != null ? name.trim() : "").split("\\s+", 2);
    String first = parts.length > 0 ? parts[0] : "";
    String last  = parts.length > 1 ? parts[1] : "";

    try (java.sql.Connection c = com.uapasia.dao.util.DB.get()) {
      c.setAutoCommit(false);
      int profId;

      if (id == null || id.isBlank()) {
        try (java.sql.PreparedStatement ps = c.prepareStatement(
            "INSERT INTO professors(first_name,last_name,dept_id) VALUES (?,?,?)",
            java.sql.Statement.RETURN_GENERATED_KEYS)) {
          ps.setString(1, first);
          ps.setString(2, last);
          ps.setInt(3, deptId);
          ps.executeUpdate();
          try (java.sql.ResultSet keys = ps.getGeneratedKeys()) { keys.next(); profId = keys.getInt(1); }
        }
      } else {
        profId = Integer.parseInt(id);
        try (java.sql.PreparedStatement ps = c.prepareStatement(
            "UPDATE professors SET first_name=?, last_name=?, dept_id=? WHERE prof_id=?")) {
          ps.setString(1, first);
          ps.setString(2, last);
          ps.setInt(3, deptId);
          ps.setInt(4, profId);
          ps.executeUpdate();
        }
        try (java.sql.PreparedStatement ps = c.prepareStatement(
            "DELETE FROM professor_subjects WHERE prof_id=?")) {
          ps.setInt(1, profId);
          ps.executeUpdate();
        }
      }

      if (subjectIds != null && subjectIds.length > 0) {
        try (java.sql.PreparedStatement ps = c.prepareStatement(
            "INSERT INTO professor_subjects(prof_id, subject_id) VALUES (?, ?)")) {
          for (String s : subjectIds) {
            if (s == null || s.isBlank()) continue;
            ps.setInt(1, profId);
            ps.setInt(2, Integer.parseInt(s));
            ps.addBatch();
          }
          ps.executeBatch();
        }
      }

      c.commit();
      resp.sendRedirect(req.getContextPath() + "/admin?status=saved");
    } catch (Exception e) {
      e.printStackTrace();
      resp.sendRedirect(req.getContextPath() + "/admin?status=error");
    }
  }
}
