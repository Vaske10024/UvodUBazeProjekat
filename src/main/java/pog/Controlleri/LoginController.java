package pog.Controlleri;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import pog.DatabaseUtil;
import pog.NoviPocetakApp;
import pog.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    private NoviPocetakApp app;
    private VBox view;

    public LoginController(NoviPocetakApp app) {
        this.app = app;
        view = new VBox(20);
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(40));
        view.getStyleClass().add("content-pane");

        // Large welcome message
        Label welcomeLabel = new Label("DOBRODOŠAO PSIHOTERAPEUTU!");
        welcomeLabel.getStyleClass().add("welcome-label");

        // Login form
        GridPane loginForm = new GridPane();
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setHgap(10);
        loginForm.setVgap(10);
        loginForm.setPadding(new Insets(20));
        loginForm.getStyleClass().add("form");

        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("label");
        TextField emailField = new TextField();
        emailField.setPromptText("Unesite vaš email");
        emailField.getStyleClass().add("text-field");

        Button loginButton = new Button("Prijavi se");
        loginButton.getStyleClass().add("button");
        Button registerButton = new Button("Registruj se");
        registerButton.getStyleClass().add("button");

        loginForm.add(emailLabel, 0, 0);
        loginForm.add(emailField, 1, 0);
        loginForm.add(loginButton, 0, 1);
        loginForm.add(registerButton, 1, 1);

        // Add welcome message and login form to the main view
        view.getChildren().addAll(welcomeLabel, loginForm);

        // Event handlers
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

    public VBox getView() {
        return view;
    }
}