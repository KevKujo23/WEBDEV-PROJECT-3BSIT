package asia.uap.controller;

import asia.uap.dao.RatingDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

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

        // Admins see a disclaimer (already have not_allowed.jsp)
        if ("admin".equalsIgnoreCase(role.toString())) {
            req.setAttribute("msg", "Administrators do not have a profile page.");
            req.getRequestDispatcher("/WEB-INF/pages/not_allowed.jsp")
               .forward(req, resp);
            return;
        }

        Integer userId = (Integer) req.getSession().getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            // reuse existing DAO: listByUser(userId) -> List<Map<String,Object>>
            List<Map<String, Object>> ratings = ratingDAO.listByUser(userId);

            int count = ratings.size();
            req.setAttribute("ratingCount", count);

            if (count > 0) {
                double sumCl = 0, sumFa = 0, sumEn = 0, sumKn = 0;

                for (Map<String, Object> r : ratings) {
                    sumCl += (Integer) r.get("clarity");
                    sumFa += (Integer) r.get("fairness");
                    sumEn += (Integer) r.get("engagement");
                    sumKn += (Integer) r.get("knowledge");
                }

                double avgCl = sumCl / count;
                double avgFa = sumFa / count;
                double avgEn = sumEn / count;
                double avgKn = sumKn / count;
                double avgOverall = (avgCl + avgFa + avgEn + avgKn) / 4.0;

                req.setAttribute("avgClarity", avgCl);
                req.setAttribute("avgFairness", avgFa);
                req.setAttribute("avgEngagement", avgEn);
                req.setAttribute("avgKnowledge", avgKn);
                req.setAttribute("avgOverall", avgOverall);

                // recent 3 ratings
                int end = Math.min(3, count);
                req.setAttribute("recentRatings", ratings.subList(0, end));
            }

            req.getRequestDispatcher("/WEB-INF/pages/profile.jsp")
               .forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
