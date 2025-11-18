/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package asia.uap.controller.admin;

import asia.uap.dao.AuditLogDAO;
import asia.uap.dao.DepartmentDAO;
import asia.uap.dao.SubjectDAO;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

/**
 *
 * @author Alexander
 */
@WebServlet(name = "AdminSubjectServlet", urlPatterns = {"/admin/subjects", "/admin/subjects/new", "/admin/subjects/create", "/admin/subjects/edit", "/admin/subjects/update", "/admin/subjects/delete"})
public class AdminSubjectServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private SubjectDAO subjectDAO;
    private DepartmentDAO deptDAO;
    private AuditLogDAO auditDAO;

    @Override
    public void init() {
        subjectDAO = new SubjectDAO();
        deptDAO = new DepartmentDAO();
        auditDAO = new AuditLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        try {
            if ("/admin/subjects".equals(path)) {
                req.setAttribute("subjects", subjectDAO.listAllForAdmin());
                req.getRequestDispatcher("/WEB-INF/pages/admin/subjects.jsp").forward(req, resp);
                return;
            }
            if ("/admin/subjects/new".equals(path)) {
                req.setAttribute("isEdit", false);
                req.setAttribute("departments", deptDAO.listSimple());
                req.getRequestDispatcher("/WEB-INF/pages/admin/subject_form.jsp").forward(req, resp);
                return;
            }
            if ("/admin/subjects/edit".equals(path)) {
                int id = Integer.parseInt(req.getParameter("id"));
                req.setAttribute("isEdit", true);
                req.setAttribute("subject", subjectDAO.find(id));
                req.setAttribute("departments", deptDAO.listSimple());
                req.getRequestDispatcher("/WEB-INF/pages/admin/subject_form.jsp").forward(req, resp);
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
            if ("/admin/subjects/create".equals(path)) {
                int deptId = Integer.parseInt(req.getParameter("deptId"));
                String code = req.getParameter("subjectCode");
                String name = req.getParameter("subjectName");
                subjectDAO.createSubject(deptId, code, name);
                auditDAO.log(adminUserId, "CREATE_SUBJ", "Subject", null, "code=" + code + ",name=" + name + ",deptId=" + deptId);
                resp.sendRedirect(req.getContextPath() + "/admin/subjects");
                return;
            }
            if ("/admin/subjects/update".equals(path)) {
                int id = Integer.parseInt(req.getParameter("subjectId"));
                int deptId = Integer.parseInt(req.getParameter("deptId"));
                String code = req.getParameter("subjectCode");
                String name = req.getParameter("subjectName");
                subjectDAO.updateSubject(id, deptId, code, name);
                auditDAO.log(adminUserId, "UPDATE_SUBJ", "Subject", id, "code=" + code + ",name=" + name + ",deptId=" + deptId);
                resp.sendRedirect(req.getContextPath() + "/admin/subjects");
                return;
            }
            if ("/admin/subjects/delete".equals(path)) {
                int id = Integer.parseInt(req.getParameter("id"));
                subjectDAO.deleteSubject(id);
                auditDAO.log(adminUserId, "DELETE_SUBJ", "Subject", id, null);
                resp.sendRedirect(req.getContextPath() + "/admin/subjects");
                return;
            }
            resp.sendError(404);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
