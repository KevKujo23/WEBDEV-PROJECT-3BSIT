package asia.uap.controller.admin;

import asia.uap.dao.DepartmentDAO;
import asia.uap.dao.ProfessorDAO;
import asia.uap.dao.SubjectDAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet(name = "CSVImportServlet", urlPatterns = {"/admin/import"})
@MultipartConfig
public class CSVImportServlet extends HttpServlet {

    private ProfessorDAO profDAO;
    private SubjectDAO subjDAO;

    @Override
    public void init() {
        profDAO = new ProfessorDAO();
        subjDAO = new SubjectDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/pages/admin/import.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String type = req.getParameter("type");
        if (type != null) {
            type = type.toLowerCase();  // normalize
        }

        Part file = req.getPart("file");
        int imported = 0;

        if (file == null || file.getSize() == 0) {
            req.setAttribute("importMsg", "No file uploaded.");
            req.getRequestDispatcher("/WEB-INF/pages/admin/import.jsp").forward(req, resp);
            return;
        }

        // preload department code -> id map once
        Map<String, Integer> deptMap;
        try {
            deptMap = loadDeptCodeMap();
        } catch (SQLException e) {
            throw new ServletException("Failed to load departments.", e);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] t = line.split(",", -1);

                if ("professors".equals(type)) {
                    // CSV: name, dept_code
                    if (t.length < 2) {
                        continue;
                    }

                    String name = t[0].trim();
                    String deptCode = t[1].trim().toLowerCase();
                    Integer deptId = deptMap.get(deptCode);

                    if (deptId != null && !name.isEmpty()) {
                        // check first if this prof already exists for this dept
                        Integer existingId = profDAO.findIdByNameAndDept(name, deptId);
                        if (existingId == null) {
                            // only insert if not found
                            profDAO.createProfessor(name, deptId);
                            imported++;
                        }
                        // if existingId != null, skip (no duplicate insert)
                    }

                } else if ("subjects".equals(type)) {
                    // CSV: dept_code, subject_code, subject_name
                    if (t.length < 3) {
                        continue;
                    }

                    String deptCode = t[0].trim().toLowerCase();
                    String code = t[1].trim();
                    String name = t[2].trim();
                    Integer deptId = deptMap.get(deptCode);

                    if (deptId != null && !code.isEmpty() && !name.isEmpty()) {
                        // NOTE: this still allows duplicates; add similar check if you want dedupe
                        subjDAO.createSubject(deptId, code, name);
                        imported++;
                    }
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }

        req.setAttribute("importMsg", "Imported " + imported + " new record(s).");
        req.getRequestDispatcher("/WEB-INF/pages/admin/import.jsp").forward(req, resp);
    }

    private Map<String, Integer> loadDeptCodeMap() throws SQLException {
        DepartmentDAO d = new DepartmentDAO();
        Map<String, Integer> map = new HashMap<>();
        for (Map<String, Object> m : d.listSimple()) {
            String code = String.valueOf(m.get("deptCode")).toLowerCase();
            map.put(code, (Integer) m.get("deptId"));
        }
        return map;
    }
}
