/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package asia.uap.controller.admin;

import asia.uap.dao.AuditLogDAO;
import asia.uap.dao.DepartmentDAO;
import asia.uap.dao.ProfessorDAO;
import asia.uap.dao.ProfessorSubjectDAO;
import asia.uap.dao.SubjectDAO;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author Alexander
 */
@WebServlet(name = "AdminProfessorServlet", urlPatterns = {"/admin/professors", "/admin/professors/new", "/admin/professors/create", "/admin/professors/edit", "/admin/professors/update", "/admin/professors/delete"})
public class AdminProfessorServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private ProfessorDAO profDAO;
    private DepartmentDAO deptDAO;
    private SubjectDAO subjDAO;
    private ProfessorSubjectDAO psDAO;
    private AuditLogDAO auditDAO;

    @Override
    public void init() {
        profDAO = new ProfessorDAO();
        deptDAO = new DepartmentDAO();
        subjDAO = new SubjectDAO();
        psDAO = new ProfessorSubjectDAO();
        auditDAO = new AuditLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        try {
            if ("/admin/professors".equals(path)) {
                req.setAttribute("professors", profDAO.listAllForAdmin());
                req.getRequestDispatcher("/WEB-INF/pages/admin/professors.jsp").forward(req, resp);
                return;
            }
            if ("/admin/professors/new".equals(path)) {
                req.setAttribute("isEdit", false);
                req.setAttribute("departments", deptDAO.listSimple());
                req.setAttribute("allSubjects", subjDAO.listAllForAdmin());
                req.setAttribute("selectedSubjectIds", Collections.emptyList());
                req.getRequestDispatcher("/WEB-INF/pages/admin/professor_form.jsp").forward(req, resp);
                return;
            }
            if ("/admin/professors/edit".equals(path)) {
                int id = Integer.parseInt(req.getParameter("id"));
                req.setAttribute("isEdit", true);
                req.setAttribute("prof", profDAO.findProfessor(id));
                req.setAttribute("departments", deptDAO.listSimple());
                req.setAttribute("allSubjects", subjDAO.listAllForAdmin());
                req.setAttribute("selectedSubjectIds", psDAO.getSubjectIdsByProfessor(id));
                req.getRequestDispatcher("/WEB-INF/pages/admin/professor_form.jsp").forward(req, resp);
                return;
            }
            resp.sendError(404);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        int adminUserId = (Integer) req.getSession().getAttribute("userId");

        try {
            if ("/admin/professors/create".equals(path)) {
                String name = trim(req.getParameter("name"));
                Integer deptId = parseInt(req.getParameter("deptId"));
                List<Integer> subjectIds = parseMultiInt(req.getParameterValues("subjectIds"));

                if (isBlank(name) || deptId == null) {
                    // Re-render form with error
                    req.setAttribute("formError", "Name and Department are required.");
                    req.setAttribute("isEdit", false);
                    req.setAttribute("departments", deptDAO.listSimple());
                    req.setAttribute("allSubjects", subjDAO.listAllForAdmin());
                    req.setAttribute("selectedSubjectIds", subjectIds);
                    req.getRequestDispatcher("/WEB-INF/pages/admin/professor_form.jsp").forward(req, resp);
                    return;
                }

                // Idempotent create (unique on dept_id+name); returns existing id if duplicate
                int profId = profDAO.createProfessorReturningId(name, deptId);
                psDAO.replaceProfessorSubjects(profId, subjectIds);
                auditDAO.log(adminUserId, "UPSERT_PROF", "Professor", profId, "name=" + name + ",deptId=" + deptId + ",subjects=" + subjectIds);

                // PRG
                resp.sendRedirect(req.getContextPath() + "/admin/professors?msg=saved");
                return;
            }

            if ("/admin/professors/update".equals(path)) {
                Integer profId = parseInt(req.getParameter("profId"));
                String name = trim(req.getParameter("name"));
                Integer deptId = parseInt(req.getParameter("deptId"));
                List<Integer> subjectIds = parseMultiInt(req.getParameterValues("subjectIds"));

                if (profId == null || isBlank(name) || deptId == null) {
                    req.setAttribute("formError", "All fields are required.");
                    req.setAttribute("isEdit", true);
                    req.setAttribute("prof", profDAO.findProfessor(profId == null ? 0 : profId));
                    req.setAttribute("departments", deptDAO.listSimple());
                    req.setAttribute("allSubjects", subjDAO.listAllForAdmin());
                    req.setAttribute("selectedSubjectIds", subjectIds);
                    req.getRequestDispatcher("/WEB-INF/pages/admin/professor_form.jsp").forward(req, resp);
                    return;
                }

                profDAO.updateProfessor(profId, name, deptId);
                psDAO.replaceProfessorSubjects(profId, subjectIds);
                auditDAO.log(adminUserId, "UPDATE_PROF", "Professor", profId, "name=" + name + ",deptId=" + deptId + ",subjects=" + subjectIds);

                resp.sendRedirect(req.getContextPath() + "/admin/professors?msg=updated");
                return;
            }

            if ("/admin/professors/delete".equals(path)) {
                int id = Integer.parseInt(req.getParameter("id"));
                // Cascades remove mappings and ratings via FK constraints
                profDAO.deleteProfessor(id);
                auditDAO.log(adminUserId, "DELETE_PROF", "Professor", id, null);
                resp.sendRedirect(req.getContextPath() + "/admin/professors?msg=deleted");
                return;
            }

            resp.sendError(404);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    private static Integer parseInt(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Integer.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }

    private static List<Integer> parseMultiInt(String[] arr) {
        if (arr == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(arr).map(AdminProfessorServlet::parseInt)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }
}
