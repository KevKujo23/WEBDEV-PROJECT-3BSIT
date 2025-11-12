/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.uapasia.web;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name="AdminSubjectDeleteServlet", urlPatterns={"/do.admin.subject.delete"})
public class AdminSubjectDeleteServlet extends HttpServlet {

  @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String id = req.getParameter("id");
    try (java.sql.Connection c = com.uapasia.dao.util.DB.get();
         java.sql.PreparedStatement ps = c.prepareStatement("DELETE FROM subjects WHERE subject_id=?")) {
      ps.setInt(1, Integer.parseInt(id));
      ps.executeUpdate(); // will fail if referenced due to FK RESTRICT
      resp.sendRedirect(req.getContextPath() + "/admin?status=deleted");
    } catch (Exception e) {
      e.printStackTrace();
      resp.sendRedirect(req.getContextPath() + "/admin?status=cannot_delete_subject_in_use");
    }
  }
}
