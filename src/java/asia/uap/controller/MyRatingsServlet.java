package asia.uap.controller;

import asia.uap.dao.RatingDAO;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "MyRatingsServlet", urlPatterns = {"/my-ratings"})
public class MyRatingsServlet extends HttpServlet {

    private RatingDAO ratingDAO;

    @Override
    public void init() {
        ratingDAO = new RatingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Object role = req.getSession().getAttribute("role");

        if (role == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Block admins (they shouldn't have ratings)
        if ("admin".equalsIgnoreCase(role.toString())) {
            req.setAttribute("msg", "Administrators cannot have or submit ratings.");
            req.getRequestDispatcher("/WEB-INF/pages/not_allowed.jsp")
               .forward(req, resp);
            return;
        }

        // userId stored in session when logging in
        Integer studentId = (Integer) req.getSession().getAttribute("userId");
        if (studentId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            // USE EXISTING DAO METHOD THAT FILTERS BY r.user_id
            req.setAttribute("ratings", ratingDAO.listByUser(studentId));

            req.getRequestDispatcher("/WEB-INF/pages/my_ratings.jsp")
               .forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
