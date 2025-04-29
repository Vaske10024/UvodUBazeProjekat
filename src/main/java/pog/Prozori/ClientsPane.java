package pog.Prozori;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pog.Model.Client;
import pog.DatabaseUtil;
import pog.Model.User;

import java.sql.*;

public class ClientsPane extends VBox {
    private TableView<Client> tableView;
    private User user;

    public ClientsPane(User user) {
        this.user = user;
        setPadding(new Insets(20));
        setSpacing(10);

        tableView = new TableView<>();
        TableColumn<Client, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        TableColumn<Client, String> nameColumn = new TableColumn<>("Ime");
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        TableColumn<Client, String> surnameColumn = new TableColumn<>("Prezime");
        surnameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSurname()));
        TableColumn<Client, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));

        tableView.getColumns().addAll(idColumn, nameColumn, surnameColumn, emailColumn);

        Button refreshButton = new Button("Osveži");
        Button addClientButton = new Button("Dodaj klijenta");

        refreshButton.setOnAction(e -> loadClients());
        addClientButton.setOnAction(e -> showAddClientForm());

        getChildren().addAll(tableView, refreshButton, addClientButton);
        loadClients();
    }

    private void loadClients() {
        tableView.getItems().clear();
        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, ime, prezime, email FROM Klijent");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tableView.getItems().add(new Client(
                        rs.getInt("id"),
                        rs.getString("ime"),
                        rs.getString("prezime"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri učitavanju klijenata: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    private void showAddClientForm() {
        Stage dialog = new Stage();
        dialog.setTitle("Dodaj klijenta");

        GridPane form = new GridPane();
        form.setPadding(new Insets(20));
        form.setHgap(10);
        form.setVgap(10);

        Label nameLabel = new Label("Ime:");
        TextField nameField = new TextField();
        Label surnameLabel = new Label("Prezime:");
        TextField surnameField = new TextField();
        Label birthDateLabel = new Label("Datum rođenja:");
        DatePicker birthDatePicker = new DatePicker();
        Label genderLabel = new Label("Pol:");
        ChoiceBox<String> genderChoice = new ChoiceBox<>();
        genderChoice.getItems().addAll("M", "Ž");
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label phoneLabel = new Label("Telefon:");
        TextField phoneField = new TextField();
        Label experienceLabel = new Label("Prethodna iskustva:");
        CheckBox experienceCheck = new CheckBox();
        Label problemLabel = new Label("Opis problema:");
        TextArea problemArea = new TextArea();
        Button submitButton = new Button("Potvrdi");

        form.add(nameLabel, 0, 0);
        form.add(nameField, 1, 0);
        form.add(surnameLabel, 0, 1);
        form.add(surnameField, 1, 1);
        form.add(birthDateLabel, 0, 2);
        form.add(birthDatePicker, 1, 2);
        form.add(genderLabel, 0, 3);
        form.add(genderChoice, 1, 3);
        form.add(emailLabel, 0, 4);
        form.add(emailField, 1, 4);
        form.add(phoneLabel, 0, 5);
        form.add(phoneField, 1, 5);
        form.add(experienceLabel, 0, 6);
        form.add(experienceCheck, 1, 6);
        form.add(problemLabel, 0, 7);
        form.add(problemArea, 1, 7);
        form.add(submitButton, 1, 8);

        submitButton.setOnAction(e -> {
            try (Connection conn = DatabaseUtil.getConnection()) {
                CallableStatement cstmt = conn.prepareCall("{CALL DodajKlijenta(?, ?, ?, ?, ?, ?, ?, ?)}");
                cstmt.setString(1, nameField.getText());
                cstmt.setString(2, surnameField.getText());
                cstmt.setDate(3, birthDatePicker.getValue() != null ? Date.valueOf(birthDatePicker.getValue()) : null);
                cstmt.setString(4, genderChoice.getValue());
                cstmt.setString(5, emailField.getText());
                cstmt.setString(6, phoneField.getText());
                cstmt.setBoolean(7, experienceCheck.isSelected());
                cstmt.setString(8, problemArea.getText());
                cstmt.execute();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Klijent dodat");
                alert.showAndWait();
                dialog.close();
                loadClients();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Greška: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        Scene scene = new Scene(form, 400, 500);
        dialog.setScene(scene);
        dialog.show();
    }
}
