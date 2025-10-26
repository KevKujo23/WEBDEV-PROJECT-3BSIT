package com.uapasia.web;

import com.uapasia.model.Professor;
import com.uapasia.repo.ContextStore;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name="ProfessorsServlet", urlPatterns={"/do.professors"})
public class ProfessorsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String ctx = req.getContextPath();
        String q = req.getParameter("q");
        List<Professor> list = (q == null || q.trim().isEmpty())
                ? ContextStore.professors(getServletContext())
                : ContextStore.searchProfessors(getServletContext(), q);

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<!doctype html><html><head><meta charset='utf-8'><title>Professors</title>"
                + "<link rel='stylesheet' href='"+ctx+"/css/styles.css'></head><body>");

        out.println("<nav class='top-nav'>"
                + "<a href='"+ctx+"/'>Home</a>"
                + "<a href='"+ctx+"/do.professors'>Professors</a>"
                + "</nav>");

        out.println("<div class='panel-container'>");
        out.println("<h2>Professors</h2>");

        out.println("<form method='get' action='"+ctx+"/do.professors' style='margin-bottom:12px'>"
                + "<input type='text' name='q' placeholder='Search by name or dept' value='"+esc(n2e(q))+"'/> "
                + "<button type='submit'>Search</button>"
                + "</form>");

        out.println("<table><thead><tr><th>ID</th><th>Name</th><th>Dept</th></tr></thead><tbody>");
        for (Professor p : list) {
            out.println("<tr>"
                    + "<td>"+p.getId()+"</td>"
                    + "<td><a href='"+ctx+"/do.professor.view?id="+p.getId()+"'>"+esc(p.getName())+"</a></td>"
                    + "<td>"+esc(p.getDept())+"</td>"
                    + "</tr>");
        }
        out.println("</tbody></table>");
        out.println("</div></body></html>");
    }

    private static String esc(String s){
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
                .replace("\"","&quot;").replace("'","&#39;");
    }
    private static String n2e(String s){ return s==null? "" : s; }
}
