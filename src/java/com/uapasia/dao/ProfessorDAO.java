/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.dao;

/**
 *
 * @author Kevin
 */
import com.uapasia.model.Professor;
import java.sql.SQLException;
import java.util.List;

public interface ProfessorDAO {
    Professor findById(int profId) throws SQLException;
    List<Professor> listByDepartment(int deptId) throws SQLException;
}

