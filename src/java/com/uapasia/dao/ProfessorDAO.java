/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.dao;

import com.uapasia.model.Professor;
import com.uapasia.model.search.ProfessorSearchCriteria;
import com.uapasia.model.search.ProfessorSearchResult;

import java.sql.SQLException;
import java.util.List;

public interface ProfessorDAO {

    Professor findById(int profId) throws SQLException;

    List<Professor> listByDepartment(int deptId) throws SQLException;

    /**
     * Search professors with filters and sorting.
     * @param c      criteria (name, course keyword, dept, min rating, sort)
     * @param offset zero-based row offset
     * @param limit  page size
     * @return typed list of search results
     */
    List<ProfessorSearchResult> search(ProfessorSearchCriteria c, int offset, int limit);
}

