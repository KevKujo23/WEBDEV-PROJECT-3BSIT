/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.dao;

/**
 *
 * @author Kevin
 */
import com.uapasia.model.Subject;
import java.sql.SQLException;

import java.util.List;
public interface SubjectDAO {
    List<Subject> listAll() throws SQLException;
    Subject findById(int subjectId) throws SQLException;
}

