package com.uapasia.web;

import com.uapasia.dao.ProfessorDAO;
import com.uapasia.factory.DAOFactory;
import com.uapasia.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin"})
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        final String ctx = req.getContextPath();

        // Require Admin
        HttpSession session = req.getSession(false);
        User current = (session == null) ? null : (User) session.getAttribute("user");
        if (current == null || current.getRole() != Role.ADMIN) {
            resp.sendRedirect(ctx + "/login.jsp?status=unauthorized");
            return;
        }

        // -------- Professors (via DAO) --------
        List<Professor> professors = new ArrayList<>();
        try {
            ProfessorDAO profDAO = DAOFactory.professors();
            professors = profDAO.listByDepartment(0); // 0 = all professors
        } catch (Exception e) {
            e.printStackTrace();
        }

        // -------- Ratings --------
        List<Rating> ratings = new ArrayList<>();
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

        // -------- Departments (for dropdowns/badges) --------
        List<Map<String, Object>> departments = new ArrayList<>();
        try (java.sql.Connection c = com.uapasia.dao.util.DB.get();
             java.sql.PreparedStatement ps = c.prepareStatement(
                     "SELECT dept_id, dept_name FROM departments ORDER BY dept_name");
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getInt("dept_id"));
                m.put("name", rs.getString("dept_name"));
                departments.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // -------- Subjects (labels for professor modal) --------
        List<Map<String, Object>> subjects = new ArrayList<>();
        try (java.sql.Connection c = com.uapasia.dao.util.DB.get();
             java.sql.PreparedStatement ps = c.prepareStatement(
                     "SELECT s.subject_id, CONCAT(s.code, ' - ', s.title, ' (', d.dept_name, ')') AS label " +
                     "FROM subjects s JOIN departments d ON s.dept_id = d.dept_id ORDER BY s.code");
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getInt("subject_id"));
                m.put("label", rs.getString("label"));
                subjects.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // -------- Subjects FULL (for the Subjects tab) --------
        List<Map<String, Object>> subjectsFull = new ArrayList<>();
        try (java.sql.Connection c = com.uapasia.dao.util.DB.get();
             java.sql.PreparedStatement ps = c.prepareStatement(
                     "SELECT s.subject_id, s.code, s.title, s.dept_id, d.dept_name " +
                     "FROM subjects s JOIN departments d ON s.dept_id = d.dept_id ORDER BY s.code");
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getInt("subject_id"));
                m.put("code", rs.getString("code"));
                m.put("title", rs.getString("title"));
                m.put("deptId", rs.getInt("dept_id"));
                m.put("deptName", rs.getString("dept_name"));
                subjectsFull.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // -------- prof_id -> CSV(subject_id) for Edit modal preselect --------
        Map<Integer, String> profSubjectsCsv = new HashMap<>();
        if (!professors.isEmpty()) {
            StringBuilder sql = new StringBuilder(
                    "SELECT prof_id, subject_id FROM professor_subjects WHERE prof_id IN (");
            for (int i = 0; i < professors.size(); i++) {
                if (i > 0) sql.append(',');
                sql.append('?');
            }
            sql.append(')');

            try (java.sql.Connection c = com.uapasia.dao.util.DB.get();
                 java.sql.PreparedStatement ps = c.prepareStatement(sql.toString())) {
                int idx = 1;
                for (Professor p : professors) ps.setInt(idx++, p.getProfId());
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    Map<Integer, List<Integer>> tmp = new HashMap<>();
                    while (rs.next()) {
                        tmp.computeIfAbsent(rs.getInt("prof_id"), k -> new ArrayList<>())
                           .add(rs.getInt("subject_id"));
                    }
                    for (Professor p : professors) {
                        List<Integer> s = tmp.getOrDefault(p.getProfId(), Collections.emptyList());
                        String csv = s.stream().map(String::valueOf)
                                      .reduce((a, b) -> a + "," + b).orElse("");
                        profSubjectsCsv.put(p.getProfId(), csv);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // -------- Bind & forward --------
        req.setAttribute("departments", departments);
        req.setAttribute("subjects", subjects);           // for professor modal
        req.setAttribute("subjectsFull", subjectsFull);   // for Subjects tab
        req.setAttribute("profSubjectsCsv", profSubjectsCsv);
        req.setAttribute("professors", professors);
        req.setAttribute("ratings", ratings);

        req.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(req, resp);
    }
}
