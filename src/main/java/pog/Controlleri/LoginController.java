package pog.Controlleri;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import pog.DatabaseUtil;
import pog.NoviPocetakApp;
import pog.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    private NoviPocetakApp app;
    private GridPane view;

    public LoginController(NoviPocetakApp app) {
        this.app = app;
        view = new GridPane();
        view.setAlignment(Pos.CENTER);
        view.setHgap(10);
        view.setVgap(10);
        view.setPadding(new Insets(20));

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Button loginButton = new Button("Prijavi se");
        Button registerButton = new Button("Registruj se");

        view.add(emailLabel, 0, 0);
        view.add(emailField, 1, 0);
        view.add(loginButton, 0, 1);
        view.add(registerButton, 1, 1);

        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            User user = authenticate(email);
            if (user != null) {
                app.showMainScene(user);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Korisnik sa ovim email-om ne postoji");
                alert.showAndWait();
            }
        });

        registerButton.setOnAction(e -> app.showRegisterScene());
    }

    private User authenticate(String email) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, ime, prezime, email, tip FROM Terapeut WHERE email = ?");
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("ime"), rs.getString("prezime"), rs.getString("email"), rs.getString("tip"));
            }
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri autentikaciji: " + ex.getMessage());
            alert.showAndWait();
        }
        return null;
    }

    public GridPane getView() {
        return view;
    }
}
