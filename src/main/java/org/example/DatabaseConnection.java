package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/savetovaliste";  // ZAMENI ime_baze
    private static final String USER = "root";  // ZAMENI ako koristiš drugi username
    private static final String PASSWORD = "jovavaske123";  // ZAMENI ako koristiš lozinku

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
