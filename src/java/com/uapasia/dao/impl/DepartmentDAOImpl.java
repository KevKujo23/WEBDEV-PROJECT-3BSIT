/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.dao.impl;

/**
 *
 * @author Kevin
 */
import com.uapasia.dao.DepartmentDAO;
import com.uapasia.dao.util.DB;
import com.uapasia.model.Department;

import java.sql.*;
import java.util.*;

public class DepartmentDAOImpl implements DepartmentDAO {
    @Override
    public List<Department> listAll() throws SQLException {
        String sql = "SELECT dept_id, name FROM departments ORDER BY name";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Department> out = new ArrayList<>();
            while (rs.next()) {
                Department d = new Department();
                d.setDeptId(rs.getInt("dept_id"));
                d.setName(rs.getString("name"));
                out.add(d);
            }
            return out;
        }
    }

    @Override
    public Department findById(int deptId) throws SQLException {
        String sql = "SELECT dept_id, name FROM departments WHERE dept_id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Department d = new Department();
                d.setDeptId(rs.getInt("dept_id"));
                d.setName(rs.getString("name"));
                return d;
            }
        }
    }
}

