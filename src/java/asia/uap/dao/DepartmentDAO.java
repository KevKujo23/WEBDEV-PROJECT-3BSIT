/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asia.uap.dao;

/**
 *
 * @author Alexander
 */
import java.sql.*;
import java.util.*;

public class DepartmentDAO {

    public java.util.List<java.util.Map<String, Object>> listSimple() throws SQLException {
        String sql = "SELECT dept_id, dept_code FROM Departments ORDER BY dept_code";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            java.util.List<java.util.Map<String, Object>> out = new java.util.ArrayList<>();
            while (rs.next()) {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("deptId", rs.getInt("dept_id"));
                m.put("deptCode", rs.getString("dept_code"));
                out.add(m);
            }
            return out;
        }
    }
}
