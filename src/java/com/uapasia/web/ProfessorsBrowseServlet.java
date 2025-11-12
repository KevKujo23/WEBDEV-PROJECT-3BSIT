/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.uapasia.web;

import com.uapasia.dao.DepartmentDAO;
import com.uapasia.factory.DAOFactory;
import com.uapasia.model.Department;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Kevin
 */


@WebServlet(name = "ProfessorsBrowseServlet", urlPatterns = {"/professors"})
public class ProfessorsBrowseServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        DepartmentDAO deptDAO = DAOFactory.departments();
        List<Department> departments = null;
        try {
            departments = deptDAO.listAll(); // You already have Departments in your schema
        } catch (SQLException ex) {
            System.getLogger(ProfessorsBrowseServlet.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        req.setAttribute("departments", departments);

        req.getRequestDispatcher("/WEB-INF/views/professors.jsp").forward(req, resp);
    }
}

