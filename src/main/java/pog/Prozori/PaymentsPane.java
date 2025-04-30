package pog.Prozori;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pog.DatabaseUtil;
import pog.Model.Payment;
import pog.Model.User;

import java.sql.*;
import java.time.LocalDate;

public class PaymentsPane extends VBox {
    private TableView<Payment> tableView;
    private User user;

    public PaymentsPane(User user) {
        this.user = user;
        setPadding(new Insets(20));
        setSpacing(10);
        getStyleClass().add("content-pane");

        tableView = new TableView<>();
        tableView.getStyleClass().add("table-view");

        TableColumn<Payment, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        TableColumn<Payment, String> clientColumn = new TableColumn<>("Klijent");
        clientColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getClientName()));
        TableColumn<Payment, Double> amountColumn = new TableColumn<>("Iznos");
        amountColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getAmount()).asObject());
        TableColumn<Payment, LocalDate> dateColumn = new TableColumn<>("Datum");
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDate()));
        TableColumn<Payment, String> statusColumn = new TableColumn<>("Način plaćanja");
        statusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

        tableView.getColumns().addAll(idColumn, clientColumn, amountColumn, dateColumn, statusColumn);

        Button refreshButton = new Button("Osveži");
        refreshButton.getStyleClass().add("button");
        Button addPaymentButton = new Button("Dodaj plaćanje");
        addPaymentButton.getStyleClass().add("button");

        refreshButton.setOnAction(e -> loadPayments());
        addPaymentButton.setOnAction(e -> showAddPaymentForm());

        getChildren().addAll(tableView, refreshButton, addPaymentButton);
        loadPayments();
    }

    private void loadPayments() {
        tableView.getItems().clear();
        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT p.id, CONCAT(k.ime, ' ', k.prezime) AS client_name, p.iznos, p.datum_uplate, p.nacin AS status " +
                            "FROM Placanje p JOIN Sesija s ON p.sesija_id = s.id JOIN Klijent k ON s.klijent_id = k.id " +
                            "WHERE s.terapeut_id = ?"
            );
            pstmt.setInt(1, user.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tableView.getItems().add(new Payment(
                        rs.getInt("id"),
                        rs.getString("client_name"),
                        rs.getDouble("iznos"),
                        rs.getDate("datum_uplate").toLocalDate(),
                        rs.getString("status")
                ));
            }
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri učitavanju plaćanja: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    private void showAddPaymentForm() {
        Stage dialog = new Stage();
        dialog.setTitle("Dodaj plaćanje");

        GridPane form = new GridPane();
        form.setPadding(new Insets(20));
        form.setHgap(10);
        form.setVgap(10);
        form.getStyleClass().add("form");

        Label sessionLabel = new Label("Sesija:");
        sessionLabel.getStyleClass().add("label");
        ComboBox<String> sessionCombo = new ComboBox<>();
        sessionCombo.getStyleClass().add("combo-box");
        Label amountLabel = new Label("Iznos:");
        amountLabel.getStyleClass().add("label");
        TextField amountField = new TextField();
        amountField.getStyleClass().add("text-field");
        Label methodLabel = new Label("Način plaćanja:");
        methodLabel.getStyleClass().add("label");
        ChoiceBox<String> methodChoice = new ChoiceBox<>();
        methodChoice.getItems().addAll("Gotovina", "Kartica");
        methodChoice.getStyleClass().add("combo-box");
        Button submitButton = new Button("Potvrdi");
        submitButton.getStyleClass().add("button");

        form.add(sessionLabel, 0, 0);
        form.add(sessionCombo, 1, 0);
        form.add(amountLabel, 0, 1);
        form.add(amountField, 1, 1);
        form.add(methodLabel, 0, 2);
        form.add(methodChoice, 1, 2);
        form.add(submitButton, 1, 3);

        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT s.id, CONCAT(k.ime, ' ', k.prezime, ' - ', s.datum) AS session_info " +
                            "FROM Sesija s JOIN Klijent k ON s.klijent_id = k.id WHERE s.terapeut_id = ?"
            );
            pstmt.setInt(1, user.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sessionCombo.getItems().add(rs.getString("session_info"));
            }
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Greška: " + ex.getMessage());
            alert.showAndWait();
        }

        submitButton.setOnAction(e -> {
            try (Connection conn = DatabaseUtil.getConnection()) {
                CallableStatement cstmt = conn.prepareCall("{CALL DodajPlacanje(?, ?, ?, ?, ?)}");
                int sessionId = getSessionId(sessionCombo.getValue(), conn);
                cstmt.setInt(1, getClientIdFromSession(sessionId, conn));
                cstmt.setInt(2, sessionId);
                cstmt.setDouble(3, Double.parseDouble(amountField.getText()));
                cstmt.setString(4, methodChoice.getValue());
                cstmt.setDate(5, Date.valueOf(LocalDate.now()));
                cstmt.execute();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Plaćanje dodano");
                alert.showAndWait();
                dialog.close();
                loadPayments();
            } catch (SQLException | NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Greška: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        Scene scene = new Scene(form, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialog.setScene(scene);
        dialog.show();
    }

    private int getClientIdFromSession(int sessionId, Connection conn) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT klijent_id FROM Sesija WHERE id = ?");
        pstmt.setInt(1, sessionId);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("klijent_id");
        }
        throw new SQLException("Sesija nije pronađena");
    }

    private int getSessionId(String sessionInfo, Connection conn) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(
                "SELECT s.id FROM Sesija s JOIN Klijent k ON s.klijent_id = k.id " +
                        "WHERE CONCAT(k.ime, ' ', k.prezime, ' - ', s.datum) = ?"
        );
        pstmt.setString(1, sessionInfo);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        }
        throw new SQLException("Sesija nije pronađena");
    }
}