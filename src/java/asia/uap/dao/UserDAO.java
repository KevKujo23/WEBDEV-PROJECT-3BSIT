/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asia.uap.dao;

/**
 *
 * @author Alexander
 */
import asia.uap.model.User;
import java.sql.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class UserDAO {

    /* ---------- Hash helpers ---------- */
    private static final SecureRandom RNG = new SecureRandom();

    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] out = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : out) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String randomSaltHex(int bytes) {
        byte[] b = new byte[bytes];
        RNG.nextBytes(b);
        StringBuilder sb = new StringBuilder();
        for (byte x : b) {
            sb.append(String.format("%02x", x));
        }
        return sb.toString();
    }

    /* ---------- Login ---------- */
    public User verifyLogin(String username, String password) throws SQLException {
        if (username == null || !username.toLowerCase().endsWith("@uap.asia")) {
            return null;
        }

        String sql = "SELECT user_id, student_number, username, email, password_hash, salt, role, dept_id "
                + "FROM Users WHERE username=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String salt = rs.getString("salt");
                String expected = rs.getString("password_hash");
                String actual = sha256(salt + password);
                if (!expected.equalsIgnoreCase(actual)) {
                    return null;
                }

                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setStudentNumber(rs.getString("student_number"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setRole(rs.getString("role"));
                u.setDeptId(rs.getInt("dept_id"));
                return u;
            }
        }
    }

    /* ---------- Registration helpers ---------- */
    public boolean isUsernameOrEmailTaken(String usernameOrEmail) throws SQLException {
        String sql = "SELECT 1 FROM Users WHERE username=? OR email=? LIMIT 1";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            String v = usernameOrEmail.toLowerCase();
            ps.setString(1, v);
            ps.setString(2, v);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isStudentNumberTaken(String studentNumber) throws SQLException {
        String sql = "SELECT 1 FROM Users WHERE student_number=? LIMIT 1";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public User createStudent(String studentNumber, String email, int deptId, String rawPassword) throws SQLException {
        String salt = randomSaltHex(16);
        String hash = sha256(salt + rawPassword);
        String sql = "INSERT INTO Users (student_number, username, email, password_hash, salt, dept_id, role) "
                + "VALUES (?,?,?,?,?,?, 'student')";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, studentNumber);
            ps.setString(2, email);   // username == email
            ps.setString(3, email);
            ps.setString(4, hash);
            ps.setString(5, salt);
            ps.setInt(6, deptId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("No generated key for Users.");
                }
                int id = rs.getInt(1);
                User u = new User();
                u.setUserId(id);
                u.setStudentNumber(studentNumber);
                u.setUsername(email);
                u.setEmail(email);
                u.setDeptId(deptId);
                u.setRole("student");
                return u;
            }
        }
    }
}
