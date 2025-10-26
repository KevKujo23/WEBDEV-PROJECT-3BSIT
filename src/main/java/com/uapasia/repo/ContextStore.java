package com.uapasia.repo;

import com.uapasia.model.*;
import javax.servlet.ServletContext;
import java.util.*;
import java.util.stream.Collectors;

public class ContextStore {

    public static final String USERS = "users";
    public static final String PROFESSORS = "professors";
    public static final String RATINGS = "ratings";

    private static final String SEEDED = "seeded_admins";
    private static final String PCOUNTER = "prof_id_counter";
    private static final String RCOUNTER = "rating_id_counter";

    /* ===================== Core collections ===================== */
    @SuppressWarnings("unchecked")
    public static synchronized List<User> users(ServletContext ctx) {
        List<User> v = (List<User>) ctx.getAttribute(USERS);
        if (v == null) {
            v = new ArrayList<>();
            ctx.setAttribute(USERS, v);
        }
        seedAdminsOnce(ctx, v);
        return v;
    }

    @SuppressWarnings("unchecked")
    public static synchronized List<Professor> professors(ServletContext ctx) {
        List<Professor> v = (List<Professor>) ctx.getAttribute(PROFESSORS);
        if (v == null) {
            v = new ArrayList<>();
            ctx.setAttribute(PROFESSORS, v);
        }
        return v;
    }

    // Optional back-compat alias if older code calls profs(...)
    public static List<Professor> profs(ServletContext ctx) {
        return professors(ctx);
    }

    @SuppressWarnings("unchecked")
    public static synchronized List<Rating> ratings(ServletContext ctx) {
        List<Rating> v = (List<Rating>) ctx.getAttribute(RATINGS);
        if (v == null) {
            v = new ArrayList<>();
            ctx.setAttribute(RATINGS, v);
        }
        return v;
    }

    /* ===================== ID counters ===================== */
    private static synchronized int next(ServletContext ctx, String key) {
        Integer n = (Integer) ctx.getAttribute(key);
        if (n == null) {
            n = 0;
        }
        n++;
        ctx.setAttribute(key, n);
        return n;
    }

    /* ===================== Create helpers ===================== */
    public static synchronized Professor addProfessor(ServletContext ctx, String name, String dept, String byUser) {
        Professor p = new Professor(next(ctx, PCOUNTER), name, dept, byUser);
        professors(ctx).add(p);
        return p;
    }

    public static synchronized Rating addRating(ServletContext ctx, int profId, int score, String comment, String byUser) {
        Rating r = new Rating(next(ctx, RCOUNTER), profId, score, comment, byUser, System.currentTimeMillis());
        ratings(ctx).add(r);
        return r;
    }

    /* ===================== Read/find helpers ===================== */
    public static Professor findProfessorById(ServletContext ctx, int id) {
        for (Professor p : professors(ctx)) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public static User findUserByUsername(ServletContext ctx, String uname) {
        if (uname == null) {
            return null;
        }
        for (User u : users(ctx)) {
            if (u.getUsername() != null && u.getUsername().equalsIgnoreCase(uname)) {
                return u;
            }
        }
        return null;
    }

    public static List<Rating> ratingsForProfessor(ServletContext ctx, int profId) {
        return ratings(ctx).stream()
                .filter(r -> r.getProfId() == profId)
                .sorted(Comparator.comparingLong(Rating::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public static List<Rating> ratingsByUser(ServletContext ctx, String username) {
        if (username == null) {
            return Collections.emptyList();
        }
        String who = username.toLowerCase();
        return ratings(ctx).stream()
                .filter(r -> r.getByUser() != null && r.getByUser().equalsIgnoreCase(who))
                .sorted(Comparator.comparingLong(Rating::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /* ===================== Admin delete helpers ===================== */
    /**
     * Delete a professor and cascade-delete its ratings. Returns true if
     * something was removed.
     */
    public static synchronized boolean deleteProfessor(ServletContext ctx, String idStr) {
        if (idStr == null) {
            return false;
        }
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            return false;
        }

        boolean removed = professors(ctx).removeIf(p -> p.getId() == id);
        if (removed) {
            ratings(ctx).removeIf(r -> r.getProfId() == id);
        }
        return removed;
    }

    /**
     * Delete a rating by id. Returns true if removed.
     */
    public static synchronized boolean deleteRating(ServletContext ctx, String idStr) {
        if (idStr == null) {
            return false;
        }
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            return false;
        }
        return ratings(ctx).removeIf(r -> r.getId() == id);
    }

    /* ===================== Seeding ===================== */
    private static void seedAdminsOnce(ServletContext ctx, List<User> users) {
        if (ctx.getAttribute(SEEDED) != null) {
            return;
        }

        seedUserIfMissing(users,
                new User("alexandervelo", "12345", "admin", "Alexander", "Velo", "alexandervelo@uap.asia", "SSE"));
        seedUserIfMissing(users,
                new User("kevinlapuz", "12345", "admin", "Kevin", "Lapuz", "kevinlapuz@uap.asia", "SSE"));
        seedUserIfMissing(users,
                new User("aldrinpaltao", "12345", "admin", "Aldrin", "Paltao", "aldrinpaltao@uap.asia", "SSE"));

        ctx.setAttribute(SEEDED, Boolean.TRUE);
    }

    private static void seedUserIfMissing(List<User> users, User candidate) {
        boolean exists = users.stream()
                .anyMatch(u -> u.getUsername() != null
                && u.getUsername().equalsIgnoreCase(candidate.getUsername()));
        if (!exists) {
            users.add(candidate);
        }
    }

    /* ===================== Optional: simple search/summaries ===================== */
    /**
     * Case-insensitive contains search on Professor.name and dept.
     */
    public static List<Professor> searchProfessors(ServletContext ctx, String q) {
        List<Professor> all = new ArrayList<>(professors(ctx));
        if (q == null || q.isBlank()) {
            return all;
        }

        String s = q.trim().toLowerCase();
        return all.stream()
                .filter(p -> {
                    String name = p.getName() == null ? "" : p.getName().toLowerCase();
                    String dept = p.getDept() == null ? "" : p.getDept().toLowerCase();
                    return name.contains(s) || dept.contains(s);
                })
                .collect(Collectors.toList());
    }

    public static double averageScore(ServletContext ctx, int profId) {
        List<Rating> rs = ratingsForProfessor(ctx, profId);
        if (rs.isEmpty()) {
            return 0.0;
        }
        int sum = 0;
        for (Rating r : rs) {
            sum += r.getScore();
        }
        return sum / (double) rs.size();
    }
}
