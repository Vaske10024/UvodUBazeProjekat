package pog.Prozori;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import pog.DatabaseUtil;
import pog.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DebtPane extends VBox {
    private TableView<Debt> tableView;
    private User user;

    public DebtPane(User user) {
        this.user = user;
        setPadding(new Insets(20));
        setSpacing(10);
        getStyleClass().add("form");

        tableView = new TableView<>();
        tableView.getStyleClass().add("table-view");
        TableColumn<Debt, String> clientColumn = new TableColumn<>("Klijent");
        clientColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getClientName()));
        TableColumn<Debt, Double> debtColumn = new TableColumn<>("Dugovanje");
        debtColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getDebt()).asObject());

        tableView.getColumns().addAll(clientColumn, debtColumn);

        Button refreshButton = new Button("Osveži");
        refreshButton.getStyleClass().add("button");
        refreshButton.setOnAction(e -> loadDebts());

        getChildren().addAll(tableView, refreshButton);
        loadDebts();
    }

    private void loadDebts() {
        tableView.getItems().clear();
        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT k.id, CONCAT(k.ime, ' ', k.prezime) AS client_name, " +
                            "(SUM(s.cena_po_satu * (s.trajanje / 60.0)) - COALESCE(SUM(p.iznos), 0)) AS debt " +
                            "FROM Klijent k " +
                            "LEFT JOIN Sesija s ON k.id = s.klijent_id " +
                            "LEFT JOIN Placanje p ON s.id = p.sesija_id " +
                            "WHERE s.terapeut_id = ? " +
                            "GROUP BY k.id, client_name " +
                            "HAVING debt > 0"
            );
            pstmt.setInt(1, user.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tableView.getItems().add(new Debt(
                        rs.getString("client_name"),
                        rs.getDouble("debt")
                ));
            }
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri učitavanju dugovanja: " + ex.getMessage());
            alert.showAndWait();
        }
    }
}

class Debt {
    private final String clientName;
    private final double debt;

    public Debt(String clientName, double debt) {
        this.clientName = clientName;
        this.debt = debt;
    }

    public String getClientName() { return clientName; }
    public double getDebt() { return debt; }
}