/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package asia.uap.controller;

import asia.uap.dao.DepartmentDAO;
import asia.uap.dao.UserDAO;
import asia.uap.model.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Alexander
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private DepartmentDAO deptDAO;
    private UserDAO userDAO;

    @Override
    public void init() {
        deptDAO = new DepartmentDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("departments", deptDAO.listSimple());
            req.setAttribute("form", new HashMap<String, Object>());
            req.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String studentNumber = trim(req.getParameter("studentNumber"));
        String username = trim(req.getParameter("username"));
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirm");
        String deptIdStr = req.getParameter("deptId");

        Map<String, Object> form = new HashMap<>();
        form.put("studentNumber", studentNumber);
        form.put("username", username);
        Integer deptId = parseInt(deptIdStr);
        if (deptId != null) {
            form.put("deptId", deptId);
        }

        try {
            // Basic validations
            if (isBlank(studentNumber) || isBlank(username) || isBlank(password) || isBlank(confirm) || deptId == null) {
                fail(req, resp, form, "All fields are required.");
                return;
            }
            if (!username.toLowerCase().endsWith("@uap.asia")) {
                fail(req, resp, form, "Email must end with @uap.asia.");
                return;
            }
            // Optional student number format (kept lenient; adjust if you want strict pattern)
            if (!studentNumber.matches("^[0-9]{4}-[0-9]{5}$")) {
                fail(req, resp, form, "Student number must match YYYY-XXXXX (e.g., 2021-12345).");
                return;
            }
            if (!password.equals(confirm)) {
                fail(req, resp, form, "Passwords do not match.");
                return;
            }
            if (password.length() < 8) {
                fail(req, resp, form, "Password must be at least 8 characters.");
                return;
            }
            // Uniqueness checks
            if (userDAO.isStudentNumberTaken(studentNumber)) {
                fail(req, resp, form, "Student number is already registered.");
                return;
            }
            if (userDAO.isUsernameOrEmailTaken(username)) {
                fail(req, resp, form, "Email is already registered.");
                return;
            }

            // Create user (role=student)
            User u = userDAO.createStudent(studentNumber, username.toLowerCase(), deptId, password);

            // Auto-login for convenience
            HttpSession s = req.getSession(true);
            s.setAttribute("userId", u.getUserId());
            s.setAttribute("username", u.getUsername());
            s.setAttribute("email", u.getEmail());
            s.setAttribute("role", u.getRole());
            s.setAttribute("deptId", u.getDeptId());

            resp.sendRedirect(req.getContextPath() + "/professors");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void fail(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> form, String message)
            throws ServletException, IOException {
        try {
            req.setAttribute("departments", deptDAO.listSimple());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.setAttribute("formError", message);
        req.setAttribute("form", form);
        req.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(req, resp);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    private static Integer parseInt(String s) {
        try {
            return s == null ? null : Integer.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }
}
