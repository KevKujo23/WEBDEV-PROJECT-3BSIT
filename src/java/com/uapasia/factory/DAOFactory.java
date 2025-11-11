/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.factory;

/**
 *
 * @author Kevin
 */
import com.uapasia.dao.*;
import com.uapasia.dao.impl.*;

public final class DAOFactory {

    private DAOFactory() {}

    public static UserDAO users() {
        return new UserDAOImpl();
    }

    public static ProfessorDAO professors() {
        return new ProfessorDAOImpl();
    }

    public static RatingDAO ratings() {
        return new RatingDAOImpl();
    }

    public static SubjectDAO subjects() {
        return new SubjectDAOImpl();
    }

    public static DepartmentDAO departments() {
        return new DepartmentDAOImpl();
    }

    public static RememberTokenDAO tokens() {
        return new RememberTokenDAOImpl();
    }
}

