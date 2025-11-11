/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.dao;

/**
 *
 * @author Kevin
 */
import com.uapasia.model.Department;
import java.sql.SQLException;
import java.util.List;

public interface DepartmentDAO {
    List<Department> listAll() throws SQLException;
    Department findById(int deptId) throws SQLException;
}

