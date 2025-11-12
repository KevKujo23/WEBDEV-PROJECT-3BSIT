/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.uapasia.web.api;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.uapasia.dao.ProfessorDAO;
import com.uapasia.factory.DAOFactory;
import com.uapasia.model.search.ProfessorSearchCriteria;
import com.uapasia.model.search.ProfessorSearchResult;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 *
 * @author Kevin
 */


@WebServlet(name = "ProfessorSearchApiServlet", urlPatterns = {"/api/professors/search"})
public class ProfessorSearchApiServlet extends HttpServlet {

    private static int parseIntOr(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
    private static double parseDoubleOr(String s, double def) {
        try { return Double.parseDouble(s); } catch (Exception e) { return def; }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");

        // Query params
        String q        = Optional.ofNullable(req.getParameter("q")).orElse("").trim();
        String course   = Optional.ofNullable(req.getParameter("course")).orElse("").trim();
        String deptId   = Optional.ofNullable(req.getParameter("dept")).orElse("").trim();
        String tagsCsv  = Optional.ofNullable(req.getParameter("tags")).orElse("").trim();
        String sort     = Optional.ofNullable(req.getParameter("sort")).orElse("most"); // most|highest|recent|az
        int page        = parseIntOr(req.getParameter("page"), 0);
        int size        = Math.min(Math.max(parseIntOr(req.getParameter("size"), 12), 6), 48);
        double minRating= parseDoubleOr(req.getParameter("minRating"), 0.0);

        Set<String> tags = new LinkedHashSet<>();
        if (!tagsCsv.isEmpty()) {
            for (String t : tagsCsv.split(",")) {
                if (!t.trim().isEmpty()) tags.add(t.trim());
            }
        }

        Integer dept = null;
        if (!deptId.isEmpty()) {
            try { dept = Integer.valueOf(deptId); } catch (Exception ignored) {}
        }

        ProfessorSearchCriteria c = new ProfessorSearchCriteria();
        c.setQuery(q);
        c.setCourseKeyword(course);
        c.setDeptId(dept);
        c.setMinRating(minRating);
        c.setTags(tags);
        c.setSort(sort);

        int offset = page * size;

        ProfessorDAO dao = DAOFactory.professors();
        List<ProfessorSearchResult> items = dao.search(c, offset, size);

        boolean hasNext = items.size() == size; // heuristic: if filled page, assume more
        try (PrintWriter out = resp.getWriter()) {
            out.append("{\"items\":[");
            for (int i = 0; i < items.size(); i++) {
                ProfessorSearchResult r = items.get(i);
                if (i > 0) out.append(",");
                out.append("{")
                   .append("\"id\":").append(String.valueOf(r.getProfId())).append(",")
                   .append("\"name\":").append(json(r.getFullName())).append(",")
                   .append("\"deptName\":").append(json(r.getDeptName())).append(",")
                   .append("\"avgRating\":").append(String.format(Locale.US, "%.2f", r.getAvgRating())).append(",")
                   .append("\"ratingCount\":").append(String.valueOf(r.getRatingCount())).append(",")
                   .append("\"topTags\":").append(jsonArray(r.getTopTags())).append(",")
                   .append("\"recentComment\":").append(json(r.getRecentCommentSnippet()))
                   .append("}");
            }
            out.append("],\"hasNext\":").append(String.valueOf(hasNext)).append("}");
        }
    }

    private static String json(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n") + "\"";
    }

    private static String jsonArray(List<String> list) {
        if (list == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(json(list.get(i)));
        }
        return sb.append("]").toString();
    }
}

