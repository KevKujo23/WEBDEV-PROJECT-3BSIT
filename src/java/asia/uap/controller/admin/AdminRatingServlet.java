/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package asia.uap.controller.admin;

import asia.uap.dao.RatingDAO;
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
@WebServlet(name = "AdminRatingServlet", urlPatterns = {"/admin/ratings", "/admin/ratings/delete"})
public class AdminRatingServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private RatingDAO ratingDAO;

    @Override
    public void init() {
        ratingDAO = new RatingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("ratings", ratingDAO.listAllAdmin());
            req.getRequestDispatcher("/WEB-INF/pages/admin/ratings.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/admin/ratings/delete".equals(path)) {
            try {
                int id = Integer.parseInt(req.getParameter("ratingId"));
                ratingDAO.adminDelete(id);
                resp.sendRedirect(req.getContextPath() + "/admin/ratings?msg=deleted");
            } catch (SQLException e) {
                throw new ServletException(e);
            }
            return;
        }
        resp.sendError(404);
    }
}
