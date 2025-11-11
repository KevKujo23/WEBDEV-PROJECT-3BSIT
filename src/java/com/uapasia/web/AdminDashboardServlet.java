package com.uapasia.web;

import com.uapasia.dao.ProfessorDAO;
import com.uapasia.factory.DAOFactory;
import com.uapasia.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin"})
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        final String ctx = req.getContextPath(); // e.g. "/ProjectWebDev"

        // Require Admin
        HttpSession session = req.getSession(false);
        User current = (session == null) ? null : (User) session.getAttribute("user");
        if (current == null || current.getRole() != Role.ADMIN) {
            resp.sendRedirect(ctx + "/login.jsp?status=unauthorized");
            return;
        }

        ProfessorDAO profDAO = DAOFactory.professors();
        List<Professor> professors = new ArrayList<>();
        List<Rating> ratings = new ArrayList<>();

        // Load professors (0 = all in your DAO)
        try {
            professors = profDAO.listByDepartment(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load ratings
        try (java.sql.Connection c = com.uapasia.dao.util.DB.get();
             java.sql.PreparedStatement ps = c.prepareStatement(
                     "SELECT rating_id, prof_id, user_id, subject_id, academic_year, term, score, comment, created_at " +
                             "FROM ratings ORDER BY rating_id DESC");
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Rating r = new Rating();
                r.setRatingId(rs.getInt("rating_id"));
                r.setProfId(rs.getInt("prof_id"));
                r.setUserId(rs.getInt("user_id"));
                r.setSubjectId(rs.getInt("subject_id"));
                r.setAcademicYear(rs.getString("academic_year"));
                r.setTerm(Term.fromDb(rs.getString("term")));
                r.setScore(rs.getInt("score"));
                r.setComment(rs.getString("comment"));
                java.sql.Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) r.setCreatedAt(ts.toInstant());
                ratings.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- Departments for selects (dept_id + name) ---
        List<java.util.Map<String, Object>> departments = new ArrayList<>();
        try (java.sql.Connection c = com.uapasia.dao.util.DB.get();
             java.sql.PreparedStatement ps = c.prepareStatement(
                     "SELECT dept_id, name FROM departments ORDER BY name");
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("id", rs.getInt("dept_id"));
                m.put("name", rs.getString("name"));
                departments.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        req.setAttribute("departments", departments);
        req.setAttribute("professors", professors);
        req.setAttribute("ratings", ratings);

        // Forward to JSP view
        req.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(req, resp);
    }
}
