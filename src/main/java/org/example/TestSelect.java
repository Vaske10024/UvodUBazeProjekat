package org.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestSelect {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM fakultet")) {

            while (rs.next()) {
                System.out.println("ID: " + rs.getString("id_fakulteta") + rs.getString("naziv")+ rs.getString("oblast")+ rs.getString("id_univerziteta"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
