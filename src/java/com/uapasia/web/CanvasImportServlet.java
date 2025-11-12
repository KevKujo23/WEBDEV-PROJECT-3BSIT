// Based on: Users endpoint (list users by account) + Link pagination behavior
// Docs: Users API + Pagination + Access tokens (citations below)

package com.uapasia.web;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;

public class CanvasImportServlet extends HttpServlet {

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String token = System.getenv("CANVAS_TOKEN");            // per docs: send Bearer token
        String accountId = "<ACCOUNT_ID>";
        String base = "https://<your-canvas-domain>/api/v1/accounts/" + accountId
                + "/users?enrollment_type=teacher&per_page=100";

        String url = base;
        while (url != null) {
            HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
            c.setRequestProperty("Authorization", "Bearer " + token);  // Access token header
            c.setRequestMethod("GET");

            // 1) Read this page of results (JSON array of users)
            String json = new String(c.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);

            // 2) TODO: parse JSON (e.g., with Gson) and UPSERT professor rows
            //    fields commonly available: id, name, sortable_name, login_id, email (if visible)

            // 3) Pagination: follow Link header rel="next" (as per Canvas pagination article)
            String link = c.getHeaderField("Link");
            url = nextLink(link);     // null when no more pages
            c.disconnect();
        }
        resp.getWriter().println("Import complete.");
    }

    private String nextLink(String linkHeader) {
        if (linkHeader == null) return null;
        for (String part : linkHeader.split(",")) {
            if (part.contains("rel=\"next\"")) {
                int a = part.indexOf('<'), b = part.indexOf('>');
                return (a >= 0 && b > a) ? part.substring(a + 1, b) : null;
            }
        }
        return null;
    }
}
