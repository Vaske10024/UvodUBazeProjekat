package org.example;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Uspesna konekcija sa bazom!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
