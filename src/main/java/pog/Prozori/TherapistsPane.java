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

public class TherapistsPane extends VBox {
    private TableView<User> tableView;
    private User user;

    public TherapistsPane(User user) {
        this.user = user;
        setPadding(new Insets(20));
        setSpacing(10);
        getStyleClass().add("content-pane");

        tableView = new TableView<>();
        tableView.getStyleClass().add("table-view");

        TableColumn<User, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        TableColumn<User, String> nameColumn = new TableColumn<>("Ime");
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        TableColumn<User, String> surnameColumn = new TableColumn<>("Prezime");
        surnameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSurname()));
        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        TableColumn<User, String> tipColumn = new TableColumn<>("Tip");
        tipColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTip()));

        tableView.getColumns().addAll(idColumn, nameColumn, surnameColumn, emailColumn, tipColumn);

        Button refreshButton = new Button("Osveži");
        refreshButton.getStyleClass().add("button");

        refreshButton.setOnAction(e -> loadTherapists());

        getChildren().addAll(tableView, refreshButton);
        loadTherapists();
    }

    private void loadTherapists() {
        tableView.getItems().clear();
        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, ime, prezime, email, tip FROM Terapeut");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tableView.getItems().add(new User(
                        rs.getInt("id"),
                        rs.getString("ime"),
                        rs.getString("prezime"),
                        rs.getString("email"),
                        rs.getString("tip")
                ));
            }
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri učitavanju terapeuta: " + ex.getMessage());
            alert.showAndWait();
        }
    }
}