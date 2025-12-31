/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.server_gui.db;

/**
 *
 * @author DELL
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    private static final String URL = "jdbc:derby://localhost:1527/TicTacToeDB;create=true";
    private static final String USER = "app";
    private static final String PASS = "app";

    private DBUtil() {
        // prevent instantiation
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
