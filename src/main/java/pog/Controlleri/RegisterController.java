package pog.Controlleri;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import pog.DatabaseUtil;
import pog.NoviPocetakApp;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class RegisterController {
    private NoviPocetakApp app;
    private GridPane view;
    private ComboBox<String> fakultetCombo;
    private Map<String, Integer> fakultetMap;

    public RegisterController(NoviPocetakApp app) {
        this.app = app;
        view = new GridPane();
        view.setPadding(new Insets(20));
        view.setHgap(10);
        view.setVgap(10);
        view.getStyleClass().add("form");

        Label nameLabel = new Label("Ime:");
        nameLabel.getStyleClass().add("label");
        TextField nameField = new TextField();
        nameField.getStyleClass().add("text-field");
        Label surnameLabel = new Label("Prezime:");
        surnameLabel.getStyleClass().add("label");
        TextField surnameField = new TextField();
        surnameField.getStyleClass().add("text-field");
        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("label");
        TextField emailField = new TextField();
        emailField.getStyleClass().add("text-field");
        Label jmbgLabel = new Label("JMBG:");
        jmbgLabel.getStyleClass().add("label");
        TextField jmbgField = new TextField();
        jmbgField.getStyleClass().add("text-field");
        Label birthDateLabel = new Label("Datum rođenja:");
        birthDateLabel.getStyleClass().add("label");
        DatePicker birthDatePicker = new DatePicker();
        birthDatePicker.getStyleClass().add("date-picker");
        Label phoneLabel = new Label("Telefon:");
        phoneLabel.getStyleClass().add("label");
        TextField phoneField = new TextField();
        phoneField.getStyleClass().add("text-field");
        Label typeLabel = new Label("Tip:");
        typeLabel.getStyleClass().add("label");
        ChoiceBox<String> typeChoice = new ChoiceBox<>();
        typeChoice.getItems().addAll("kandidat", "sertifikovani");
        typeChoice.setValue("kandidat");
        typeChoice.getStyleClass().add("combo-box");

        // New faculty field
        Label fakultetLabel = new Label("Fakultet:");
        fakultetLabel.getStyleClass().add("label");
        fakultetCombo = new ComboBox<>();
        fakultetCombo.getStyleClass().add("combo-box");
        fakultetMap = new HashMap<>();
        loadFaculties();

        Button submitButton = new Button("Podnesi");
        submitButton.getStyleClass().add("button");
        Button backButton = new Button("Nazad");
        backButton.getStyleClass().add("button");

        // Add to grid
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
        view.add(fakultetLabel, 0, 7);
        view.add(fakultetCombo, 1, 7);
        view.add(submitButton, 0, 8);
        view.add(backButton, 1, 8);

        // Submit action
        submitButton.setOnAction(e -> {
            String selectedFakultet = fakultetCombo.getValue();
            if (selectedFakultet == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Morate izabrati fakultet.");
                alert.showAndWait();
                return;
            }
            try (Connection conn = DatabaseUtil.getConnection()) {
                CallableStatement cstmt = conn.prepareCall("{CALL DodajTerapeuta(?, ?, ?, ?, ?, ?, ?, ?)}");
                cstmt.setString(1, jmbgField.getText());
                cstmt.setString(2, nameField.getText());
                cstmt.setString(3, surnameField.getText());
                cstmt.setDate(4, birthDatePicker.getValue() != null ? java.sql.Date.valueOf(birthDatePicker.getValue()) : null);
                cstmt.setString(5, emailField.getText());
                cstmt.setString(6, phoneField.getText());
                cstmt.setString(7, typeChoice.getValue());
                cstmt.setInt(8, fakultetMap.get(selectedFakultet));
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

    private void loadFaculties() {
        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT id, naziv FROM Fakultet WHERE oblast = 'humanisticke nauke'"
            );
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String naziv = rs.getString("naziv");
                int id = rs.getInt("id");
                fakultetCombo.getItems().add(naziv);
                fakultetMap.put(naziv, id);
            }
            if (!fakultetCombo.getItems().isEmpty()) {
                fakultetCombo.setValue(fakultetCombo.getItems().get(0));
            }
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri učitavanju fakulteta: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    public GridPane getView() {
        return view;
    }
}