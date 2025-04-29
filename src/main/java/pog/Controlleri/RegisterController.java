package pog.Controlleri;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import pog.DatabaseUtil;
import pog.NoviPocetakApp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class RegisterController {
    private NoviPocetakApp app;
    private GridPane view;

    public RegisterController(NoviPocetakApp app) {
        this.app = app;
        view = new GridPane();
        view.setPadding(new Insets(20));
        view.setHgap(10);
        view.setVgap(10);

        Label nameLabel = new Label("Ime:");
        TextField nameField = new TextField();
        Label surnameLabel = new Label("Prezime:");
        TextField surnameField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label jmbgLabel = new Label("JMBG:");
        TextField jmbgField = new TextField();
        Label birthDateLabel = new Label("Datum rođenja:");
        DatePicker birthDatePicker = new DatePicker();
        Label phoneLabel = new Label("Telefon:");
        TextField phoneField = new TextField();
        Label typeLabel = new Label("Tip:");
        ChoiceBox<String> typeChoice = new ChoiceBox<>();
        typeChoice.getItems().addAll("kandidat", "sertifikovani");
        typeChoice.setValue("kandidat");
        Button submitButton = new Button("Podnesi");
        Button backButton = new Button("Nazad");

        view.add(nameLabel, 0, 0);
        view.add(nameField, 1, 0);
        view.add(surnameLabel, 0, 1);
        view.add(surnameField, 1, 1);
        view.add(emailLabel, 0, 2);
        view.add(emailField, 1, 2);
        view.add(jmbgLabel, 0, 3);
        view.add(jmbgField, 1, 3);
        view.add(birthDateLabel, 0, 4);
        view.add(birthDatePicker, 1, 4);
        view.add(phoneLabel, 0, 5);
        view.add(phoneField, 1, 5);
        view.add(typeLabel, 0, 6);
        view.add(typeChoice, 1, 6);
        view.add(submitButton, 0, 7);
        view.add(backButton, 1, 7);

        submitButton.setOnAction(e -> {
            try (Connection conn = DatabaseUtil.getConnection()) {
                CallableStatement cstmt = conn.prepareCall("{CALL DodajTerapeuta(?, ?, ?, ?, ?, ?, ?)}");
                cstmt.setString(1, jmbgField.getText());
                cstmt.setString(2, nameField.getText());
                cstmt.setString(3, surnameField.getText());
                cstmt.setDate(4, birthDatePicker.getValue() != null ? java.sql.Date.valueOf(birthDatePicker.getValue()) : null);
                cstmt.setString(5, emailField.getText());
                cstmt.setString(6, phoneField.getText());
                cstmt.setString(7, typeChoice.getValue());
                cstmt.execute();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Registracija uspešna");
                alert.showAndWait();
                app.showLoginScene();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Greška: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        backButton.setOnAction(e -> app.showLoginScene());
    }

    public GridPane getView() {
        return view;
    }
}
