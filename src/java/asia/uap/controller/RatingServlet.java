package asia.uap.controller;

import asia.uap.dao.RatingDAO;
import asia.uap.model.Rating;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "RatingServlet", urlPatterns = {"/rating"})
public class RatingServlet extends HttpServlet {

    private RatingDAO ratingDAO;

    @Override
    public void init() {
        ratingDAO = new RatingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Only students can access the rating form
        HttpSession session = req.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        if (!"student".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only students can rate professors.");
            return;
        }

        if (!"form".equals(req.getParameter("action"))) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?err=login");
            return;
        }

        int profSubjectId = Integer.parseInt(req.getParameter("profSubjectId"));
        try {
            Rating existing = ratingDAO.findByUserAndPair(userId, profSubjectId);
            req.setAttribute("existing", existing);
            req.setAttribute("profSubjectId", profSubjectId);
            req.getRequestDispatcher("/WEB-INF/pages/rating_form.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        HttpSession session = req.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        try {
            // ==== ADMIN-ONLY: delete rating ====
            if ("delete".equals(action)) {
                if (!"admin".equals(role)) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can delete ratings.");
                    return;
                }
                int ratingId = Integer.parseInt(req.getParameter("ratingId"));
                ratingDAO.adminDelete(ratingId);
                resp.sendRedirect(req.getContextPath() + "/admin/ratings?msg=deleted");
                return;
            }

            // ==== STUDENT-ONLY: create/update rating ====
            if (!"student".equals(role)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only students can rate professors.");
                return;
            }

            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                resp.sendRedirect(req.getContextPath() + "/index.jsp?err=login");
                return;
            }

            if ("save".equals(action)) {
                int profSubjectId = Integer.parseInt(req.getParameter("profSubjectId"));
                int clarity = Integer.parseInt(req.getParameter("clarity"));
                int fairness = Integer.parseInt(req.getParameter("fairness"));
                int engagement = Integer.parseInt(req.getParameter("engagement"));
                int knowledge = Integer.parseInt(req.getParameter("knowledge"));
                String comment = req.getParameter("comment");

                if (clarity < 1 || clarity > 5 || fairness < 1 || fairness > 5
                        || engagement < 1 || engagement > 5 || knowledge < 1 || knowledge > 5
                        || comment == null || comment.isBlank()) {

                    req.setAttribute("formError", "All criteria must be 1–5 and comment is required.");
                    req.setAttribute("existing", ratingDAO.findByUserAndPair(userId, profSubjectId));
                    req.setAttribute("profSubjectId", profSubjectId);
                    req.getRequestDispatcher("/WEB-INF/pages/rating_form.jsp").forward(req, resp);
                    return;
                }

                Rating existing = ratingDAO.findByUserAndPair(userId, profSubjectId);
                if (existing == null) {
                    Rating r = new Rating();
                    r.setUserId(userId);
                    r.setProfSubjectId(profSubjectId);
                    r.setClarity(clarity);
                    r.setFairness(fairness);
                    r.setEngagement(engagement);
                    r.setKnowledge(knowledge);
                    r.setComment(comment);
                    ratingDAO.insert(r);
                } else {
                    existing.setClarity(clarity);
                    existing.setFairness(fairness);
                    existing.setEngagement(engagement);
                    existing.setKnowledge(knowledge);
                    existing.setComment(comment);
                    ratingDAO.update(existing);
                }
                resp.sendRedirect(req.getContextPath() + "/my-ratings?msg=saved");
                return;
            }

            // Unknown action
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
