/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package asia.uap.controller;

import asia.uap.dao.UserDAO;
import asia.uap.model.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.*;

/**
 *
 * @author Alexander
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/logout"})
public class AuthServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if ("/logout".equals(req.getServletPath())) {
            HttpSession s = req.getSession(false);
            if (s != null) {
                s.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/index.jsp?msg=loggedout");
            return;
        }
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        try {
            User u = userDAO.verifyLogin(username, password);
            if (u == null) {
                req.setAttribute("error", "Invalid credentials (must be @uap.asia).");
                req.getRequestDispatcher("/index.jsp").forward(req, resp);
                return;
            }
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
}
