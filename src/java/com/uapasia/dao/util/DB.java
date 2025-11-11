/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.dao.util;

/**
 *
 * @author Kevin
 */
import java.sql.*;

public final class DB {
    public static final String URL  =
        "jdbc:mysql://localhost:3306/uap_portal?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    public static final String USER = "root";
    public static final String PASS = "b0bbY!";

    static {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { throw new RuntimeException(e); }
    }

    /** Open a new connection for this request. Caller closes. */
    public static Connection open() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
    
    public static Connection get() throws SQLException {
        return open();
    }

    private DB() {}
}



