package asia.uap.filter;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(filterName = "StudentOnlyFilter", urlPatterns = {"/rating", "/my-ratings"})
public class StudentOnlyFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // no-op
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        String action = req.getParameter("action");
        String path = req.getServletPath();

        // Allow admin for /rating?action=delete (adminDelete)
        if ("/rating".equals(path) && "delete".equals(action) && "admin".equals(role)) {
            chain.doFilter(request, response);
            return;
        }

        // All other access must be from a student
        if (!"student".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only students can access this page.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // no-op
    }
}
