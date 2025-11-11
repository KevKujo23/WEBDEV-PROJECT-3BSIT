/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.dao;

/**
 *
 * @author Kevin
 */
import com.uapasia.model.User;
import java.sql.SQLException;

public interface UserDAO {
    User findByLoginAndPassword(String login, String password) throws SQLException;
    boolean emailExists(String email) throws SQLException;
    boolean studentNumberExists(String studentNumber) throws SQLException;
    int create(User u) throws SQLException;         // returns new user_id
    User findById(int userId) throws SQLException;
}

