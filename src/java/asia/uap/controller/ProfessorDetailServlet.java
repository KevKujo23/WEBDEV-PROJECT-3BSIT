package asia.uap.controller;

import asia.uap.dao.ProfessorDAO;
import asia.uap.dao.RatingDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ProfessorDetailServlet", urlPatterns = {"/professor"})
public class ProfessorDetailServlet extends HttpServlet {

    private ProfessorDAO professorDAO;
    private RatingDAO ratingDAO;

    @Override
    public void init() {
        professorDAO = new ProfessorDAO();
        ratingDAO = new RatingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("profId");
        if (idParam == null || idParam.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/professors");
            return;
        }

        int profId;
        try {
            profId = Integer.parseInt(idParam);
        } catch (NumberFormatException ex) {
            resp.sendRedirect(req.getContextPath() + "/professors");
            return;
        }

        try {
            // Basic professor info
            Map<String, Object> profInfo = professorDAO.getProfessorWithDept(profId);
            if (profInfo == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Professor not found.");
                return;
            }

            // FIX: Build professorName from DAO fields
            String f = (String) profInfo.get("firstName");
            String l = (String) profInfo.get("lastName");
            profInfo.put("professorName", (f + " " + l).trim());

            // All ratings for this professor
            List<Map<String, Object>> ratings = ratingDAO.listRatingsForProfessor(profId);

            // Build overview + per-subject stats from ratings
            Map<String, Object> overview = buildOverview(profInfo, ratings);
            List<Map<String, Object>> subjectStats = buildSubjectStats(ratings);

            req.setAttribute("overview", overview);
            req.setAttribute("subjectStats", subjectStats);
            req.setAttribute("ratings", ratings);

            req.getRequestDispatcher("/WEB-INF/pages/professor_view.jsp")
               .forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    // ---- helpers to compute stats in Java ----

    private Map<String, Object> buildOverview(Map<String, Object> profInfo,
                                              List<Map<String, Object>> ratings) {
        Map<String, Object> ov = new HashMap<>(profInfo);

        int count = ratings.size();
        ov.put("ratingCount", count);

        if (count == 0) {
            ov.put("avgOverall", null);
            ov.put("minOverall", null);
            ov.put("maxOverall", null);
            ov.put("avgClarity", null);
            ov.put("avgFairness", null);
            ov.put("avgEngagement", null);
            ov.put("avgKnowledge", null);
            ov.put("lastRatingDate", null);
            return ov;
        }

        double sumCl = 0, sumFa = 0, sumEn = 0, sumKn = 0;
        double minOverall = Double.MAX_VALUE;
        double maxOverall = Double.MIN_VALUE;
        Timestamp lastDate = null;

        for (Map<String, Object> r : ratings) {
            int cl = (Integer) r.get("clarity");
            int fa = (Integer) r.get("fairness");
            int en = (Integer) r.get("engagement");
            int kn = (Integer) r.get("knowledge");
            Timestamp ts = (Timestamp) r.get("dateSubmitted");

            double overall = (cl + fa + en + kn) / 4.0;

            sumCl += cl;
            sumFa += fa;
            sumEn += en;
            sumKn += kn;
            if (overall < minOverall) minOverall = overall;
            if (overall > maxOverall) maxOverall = overall;
            if (ts != null && (lastDate == null || ts.after(lastDate))) {
                lastDate = ts;
            }
        }

        ov.put("avgClarity", sumCl / count);
        ov.put("avgFairness", sumFa / count);
        ov.put("avgEngagement", sumEn / count);
        ov.put("avgKnowledge", sumKn / count);
        ov.put("avgOverall", (sumCl + sumFa + sumEn + sumKn) / (4.0 * count));
        ov.put("minOverall", minOverall);
        ov.put("maxOverall", maxOverall);
        ov.put("lastRatingDate", lastDate);

        return ov;
    }

    private List<Map<String, Object>> buildSubjectStats(List<Map<String, Object>> ratings) {
        Map<Integer, SubjectAgg> aggMap = new LinkedHashMap<>();

        for (Map<String, Object> r : ratings) {
            Integer psId = (Integer) r.get("profSubjectId");
            String code = (String) r.get("subjectCode");
            String name = (String) r.get("subjectName");

            SubjectAgg agg = aggMap.get(psId);
            if (agg == null) {
                agg = new SubjectAgg(psId, code, name);
                aggMap.put(psId, agg);
            }

            int cl = (Integer) r.get("clarity");
            int fa = (Integer) r.get("fairness");
            int en = (Integer) r.get("engagement");
            int kn = (Integer) r.get("knowledge");

            agg.add(cl, fa, en, kn);
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (SubjectAgg a : aggMap.values()) {
            list.add(a.toMap());
        }
        return list;
    }

    private static class SubjectAgg {
        final int profSubjectId;
        final String subjectCode;
        final String subjectName;
        int count = 0;
        double sumCl = 0, sumFa = 0, sumEn = 0, sumKn = 0;
        double minOverall = Double.MAX_VALUE;
        double maxOverall = Double.MIN_VALUE;

        SubjectAgg(int psId, String code, String name) {
            this.profSubjectId = psId;
            this.subjectCode = code;
            this.subjectName = name;
        }

        void add(int cl, int fa, int en, int kn) {
            count++;
            sumCl += cl;
            sumFa += fa;
            sumEn += en;
            sumKn += kn;
            double overall = (cl + fa + en + kn) / 4.0;
            if (overall < minOverall) minOverall = overall;
            if (overall > maxOverall) maxOverall = overall;
        }

        Map<String, Object> toMap() {
            Map<String, Object> m = new HashMap<>();
            m.put("profSubjectId", profSubjectId);
            m.put("subjectCode", subjectCode);
            m.put("subjectName", subjectName);
            m.put("ratingCount", count);

            if (count == 0) {
                m.put("avgOverall", null);
                m.put("minOverall", null);
                m.put("maxOverall", null);
                m.put("avgClarity", null);
                m.put("avgFairness", null);
                m.put("avgEngagement", null);
                m.put("avgKnowledge", null);
            } else {
                m.put("avgClarity", sumCl / count);
                m.put("avgFairness", sumFa / count);
                m.put("avgEngagement", sumEn / count);
                m.put("avgKnowledge", sumKn / count);
                m.put("avgOverall", (sumCl + sumFa + sumEn + sumKn) / (4.0 * count));
                m.put("minOverall", minOverall);
                m.put("maxOverall", maxOverall);
            }

            return m;
        }
    }
}
