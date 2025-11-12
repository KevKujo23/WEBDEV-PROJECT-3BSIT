package com.uapasia.util;

import javax.servlet.http.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CookieUtils {

    private static String enc(String v){
        if (v == null) return "";
        // URLEncoder uses '+' for spaces; that's OK for cookie values.
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
    private static String dec(String v){
        return (v == null) ? null : URLDecoder.decode(v, StandardCharsets.UTF_8);
    }

    /** Create a cookie bound to the app context path, HttpOnly by default. */
    public static Cookie make(String name, String value, int maxAgeSeconds, String contextPath, boolean secure){
        Cookie c = new Cookie(name, enc(value));
        c.setPath((contextPath == null || contextPath.isEmpty()) ? "/" : contextPath);
        c.setMaxAge(maxAgeSeconds);
        c.setHttpOnly(true);
        c.setSecure(secure);
        return c;
    }

    /** Convenience: create a cookie at root path (/) – only use if you truly need it across apps. */
    public static Cookie makeRoot(String name, String value, int maxAgeSeconds, boolean secure){
        Cookie c = new Cookie(name, enc(value));
        c.setPath("/");
        c.setMaxAge(maxAgeSeconds);
        c.setHttpOnly(true);
        c.setSecure(secure);
        return c;
    }

    /** Add SameSite to a cookie (Servlet 4 workaround). Call this after resp.addCookie(cookie). */
    public static void addSameSite(HttpServletResponse resp, Cookie cookie, String sameSite /* Lax|Strict|None */){
        // Build a Set-Cookie header manually to append SameSite (Tomcat 9/Servlet 4 has no API)
        StringBuilder sb = new StringBuilder();
        sb.append(cookie.getName()).append("=").append(cookie.getValue())
          .append("; Path=").append(cookie.getPath()==null?"/":cookie.getPath());
        if (cookie.getMaxAge() >= 0) sb.append("; Max-Age=").append(cookie.getMaxAge());
        if (cookie.getSecure()) sb.append("; Secure");
        if (cookie.isHttpOnly()) sb.append("; HttpOnly");
        if (sameSite != null && !sameSite.isEmpty()) sb.append("; SameSite=").append(sameSite);
        resp.addHeader("Set-Cookie", sb.toString());
    }

    public static String get(HttpServletRequest req, String name){
        Cookie[] cs = req.getCookies();
        if (cs == null) return null;
        for (Cookie c : cs) {
            if (name.equals(c.getName())) return dec(c.getValue());
        }
        return null;
    }

    public static void clear(HttpServletResponse resp, String name, String contextPath){
        Cookie c = new Cookie(name, "");
        c.setPath((contextPath == null || contextPath.isEmpty()) ? "/" : contextPath);
        c.setMaxAge(0);
        c.setHttpOnly(true);
        resp.addCookie(c);
        // If you used SameSite before, optionally also add a header-based clear:
        resp.addHeader("Set-Cookie", name + "=; Max-Age=0; Path=" +
                ((contextPath == null || contextPath.isEmpty()) ? "/" : contextPath) +
                "; HttpOnly; SameSite=Lax");
    }
}
