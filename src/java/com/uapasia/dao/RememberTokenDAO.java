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

public interface RememberTokenDAO {
    void insert(int userId, String token) throws SQLException;
    User findUserByToken(String token) throws SQLException;
    void delete(String token) throws SQLException;
}

