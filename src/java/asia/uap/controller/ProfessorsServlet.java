package asia.uap.controller;

import asia.uap.dao.DepartmentDAO;
import asia.uap.dao.ProfessorDAO;
import asia.uap.dao.SubjectDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

@WebServlet(name = "ProfessorsServlet", urlPatterns = {"/professors"})
public class ProfessorsServlet extends HttpServlet {

    private ProfessorDAO profDAO;
    private DepartmentDAO deptDAO;
    private SubjectDAO subjDAO;

    @Override
    public void init() {
        profDAO = new ProfessorDAO();
        deptDAO = new DepartmentDAO();
        subjDAO = new SubjectDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Integer deptId = parseInt(req.getParameter("deptId"));
        Integer subjectId = parseInt(req.getParameter("subjectId"));
        String q = req.getParameter("q");

        try {
            req.setAttribute("departments", deptDAO.listSimple());
            req.setAttribute("subjects", subjDAO.listAll());
            req.setAttribute("professors", profDAO.filter(deptId, subjectId, q));
            req.getRequestDispatcher("/WEB-INF/pages/professors.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private Integer parseInt(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Integer.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }
}
