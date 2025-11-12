/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.uapasia.web;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name="AdminSubjectSaveServlet", urlPatterns={"/do.admin.subject.save"})
public class AdminSubjectSaveServlet extends HttpServlet {

  @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String id    = req.getParameter("id");          // empty when new
    String code  = req.getParameter("code");
    String title = req.getParameter("title");
    String dept  = req.getParameter("departmentId");

    if (code != null)  code  = code.trim();
    if (title != null) title = title.trim();

    try (java.sql.Connection c = com.uapasia.dao.util.DB.get()) {
      if (id == null || id.isBlank()) {
        try (java.sql.PreparedStatement ps = c.prepareStatement(
            "INSERT INTO subjects(code, title, dept_id) VALUES (?,?,?)")) {
          ps.setString(1, code);
          ps.setString(2, title);
          ps.setInt(3, Integer.parseInt(dept));
          ps.executeUpdate();
        }
      } else {
        try (java.sql.PreparedStatement ps = c.prepareStatement(
            "UPDATE subjects SET code=?, title=?, dept_id=? WHERE subject_id=?")) {
          ps.setString(1, code);
          ps.setString(2, title);
          ps.setInt(3, Integer.parseInt(dept));
          ps.setInt(4, Integer.parseInt(id));
          ps.executeUpdate();
        }
      }
      resp.sendRedirect(req.getContextPath() + "/admin?status=saved");
    } catch (Exception e) {
      e.printStackTrace();
      resp.sendRedirect(req.getContextPath() + "/admin?status=error");
    }
  }
}
