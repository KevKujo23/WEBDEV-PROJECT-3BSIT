package com.uapasia.web;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ProfessorsServlet", urlPatterns = {"/do.professors"})
public class ProfessorsServlet extends HttpServlet {

    static final class Row {
        final int id;
        final String name;
        final String deptName;
        Row(int id, String name, String deptName){ this.id=id; this.name=name; this.deptName=deptName; }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String ctx = req.getContextPath();
        String q = trim(req.getParameter("q"));

        List<Row> list = new ArrayList<>();
        String sqlAll =
            "SELECT p.prof_id, CONCAT(p.first_name,' ',p.last_name) AS full_name, COALESCE(d.name,'') AS dept_name " +
            "FROM professors p LEFT JOIN departments d ON d.dept_id=p.dept_id " +
            "ORDER BY p.last_name, p.first_name";

        String sqlSearch =
            "SELECT p.prof_id, CONCAT(p.first_name,' ',p.last_name) AS full_name, COALESCE(d.name,'') AS dept_name " +
            "FROM professors p LEFT JOIN departments d ON d.dept_id=p.dept_id " +
            "WHERE LOWER(CONCAT(p.first_name,' ',p.last_name)) LIKE ? OR LOWER(d.name) LIKE ? " +
            "ORDER BY p.last_name, p.first_name";

        try (Connection c = com.uapasia.dao.util.DB.get();
             PreparedStatement ps = c.prepareStatement(isBlank(q) ? sqlAll : sqlSearch)) {

            if (!isBlank(q)) {
                String like = "%" + q.toLowerCase() + "%";
                ps.setString(1, like);
                ps.setString(2, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("prof_id");
                    String name = rs.getString("full_name");
                    String dept = rs.getString("dept_name");
                    list.add(new Row(id, name, dept));
                }
            }
        } catch (Exception e) {
            // If DB fails, we’ll just render an empty list with an error note
        }

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<!doctype html><html><head><meta charset='utf-8'><title>Professors</title>"
                + "<link rel='stylesheet' href='"+ctx+"/css/styles.css'></head><body>");

        out.println("<nav class='top-nav'>"
                + "<a href='"+ctx+"/'>Home</a>"
                + "<a href='"+ctx+"/do.professors'>Professors</a>"
                + "</nav>");

        out.println("<div class='panel-container'>");
        out.println("<h2>Professors</h2>");

        out.println("<form method='get' action='"+ctx+"/do.professors' style='margin-bottom:12px'>"
                + "<input type='text' name='q' placeholder='Search by name or dept' value='"+esc(n2e(q))+"'/> "
                + "<button type='submit'>Search</button>"
                + "</form>");

        // Optional error banner if DB returned nothing and a query string exists
        if (!isBlank(q) && list.isEmpty()) {
            out.println("<p class='note'>No professors matched <strong>"+esc(q)+"</strong>.</p>");
        }

        out.println("<table><thead><tr><th>ID</th><th>Name</th><th>Dept</th></tr></thead><tbody>");
        for (Row p : list) {
            out.println("<tr>"
                    + "<td>"+p.id+"</td>"
                    + "<td><a href='"+ctx+"/do.professor.view?id="+p.id+"'>"+esc(p.name)+"</a></td>"
                    + "<td>"+esc(n2e(p.deptName))+"</td>"
                    + "</tr>");
        }
        out.println("</tbody></table>");
        out.println("</div></body></html>");
    }

    /* helpers */
    private static String trim(String s){ return s==null? null : s.trim(); }
    private static boolean isBlank(String s){ return s==null || s.trim().isEmpty(); }
    private static String esc(String s){
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
                .replace("\"","&quot;").replace("'","&#39;");
    }
    private static String n2e(String s){ return s==null? "" : s; }
}
