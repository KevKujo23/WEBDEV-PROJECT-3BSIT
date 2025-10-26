package com.uapasia.util;

import javax.servlet.http.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CookieUtils {

    private static String enc(String v) {
        return v == null ? "" : URLEncoder.encode(v, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private static String dec(String v) {
        return v == null ? null : URLDecoder.decode(v, StandardCharsets.UTF_8);
    }

    public static Cookie make(String name, String value, int maxAge) {
        Cookie c = new Cookie(name, enc(value));
        c.setPath("/");
        c.setMaxAge(maxAge);
        return c;
    }

    public static String get(HttpServletRequest req, String name) {
        Cookie[] cs = req.getCookies();
        if (cs == null) {
            return null;
        }
        for (Cookie c : cs) {
            if (c.getName().equals(name)) {
                return dec(c.getValue());
            }
        }
        return null;
    }

    public static void clear(HttpServletResponse resp, String name) {
        Cookie c = new Cookie(name, "");
        c.setPath("/");
        c.setMaxAge(0);
        resp.addCookie(c);
    }
}
