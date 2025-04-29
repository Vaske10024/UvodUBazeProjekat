package pog.Prozori;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import pog.DatabaseUtil;
import pog.NoviPocetakApp;
import pog.Model.Session;
import pog.Model.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class SessionsPane extends VBox {
    private NoviPocetakApp app;
    private User user;

    public SessionsPane(NoviPocetakApp app, User user) {
        this.app = app;
        this.user = user;
        setPadding(new Insets(20));
        setSpacing(10);

        TabPane tabPane = new TabPane();

        Tab pastSessionsTab = new Tab("Prošle sesije");
        pastSessionsTab.setClosable(false);
        TableView<Session> pastTable = createSessionTable();
        loadPastSessions(pastTable);
        pastSessionsTab.setContent(pastTable);

        Tab futureSessionsTab = new Tab("Buduće sesije");
        futureSessionsTab.setClosable(false);
        TableView<Session> futureTable = createSessionTable();
        loadFutureSessions(futureTable);
        futureSessionsTab.setContent(futureTable);

        Tab scheduleTab = new Tab("Zakaži sesiju");
        scheduleTab.setClosable(false);
        scheduleTab.setContent(createScheduleForm());

        Tab notesTab = new Tab("Beleške sa sesija");
        notesTab.setClosable(false);
        notesTab.setContent(createNotesForm());

        tabPane.getTabs().addAll(pastSessionsTab, futureSessionsTab, scheduleTab, notesTab);
        getChildren().add(tabPane);
    }

    private TableView<Session> createSessionTable() {
        TableView<Session> table = new TableView<>();
        TableColumn<Session, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        TableColumn<Session, LocalDate> dateColumn = new TableColumn<>("Datum");
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDate()));
        TableColumn<Session, LocalTime> timeColumn = new TableColumn<>("Vreme");
        timeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getTime()));
        TableColumn<Session, String> clientColumn = new TableColumn<>("Klijent");
        clientColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getClientName()));
        TableColumn<Session, Integer> durationColumn = new TableColumn<>("Trajanje (min)");
        durationColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getDuration()).asObject());

        table.getColumns().addAll(idColumn, dateColumn, timeColumn, clientColumn, durationColumn);
        return table;
    }

    private void loadPastSessions(TableView<Session> table) {
        table.getItems().clear();
        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT s.id, s.datum, s.vreme_pocetka, CONCAT(k.ime, ' ', k.prezime) AS client_name, s.trajanje " +
                            "FROM Sesija s JOIN Klijent k ON s.klijent_id = k.id " +
                            "WHERE s.terapeut_id = ? AND s.datum < CURDATE()"
            );
            pstmt.setInt(1, user.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                table.getItems().add(new Session(
                        rs.getInt("id"),
                        rs.getDate("datum").toLocalDate(),
                        rs.getTime("vreme_pocetka").toLocalTime(),
                        rs.getString("client_name"),
                        rs.getInt("trajanje")
                ));
            }
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri učitavanju prošlih sesija: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    private void loadFutureSessions(TableView<Session> table) {
        table.getItems().clear();
        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT s.id, s.datum, s.vreme_pocetka, CONCAT(k.ime, ' ', k.prezime) AS client_name, s.trajanje " +
                            "FROM Sesija s JOIN Klijent k ON s.klijent_id = k.id " +
                            "WHERE s.terapeut_id = ? AND s.datum >= CURDATE()"
            );
            pstmt.setInt(1, user.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                table.getItems().add(new Session(
                        rs.getInt("id"),
                        rs.getDate("datum").toLocalDate(),
                        rs.getTime("vreme_pocetka").toLocalTime(),
                        rs.getString("client_name"),
                        rs.getInt("trajanje")
                ));
            }
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri učitavanju budućih sesija: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    private GridPane createScheduleForm() {
        GridPane form = new GridPane();
        form.setPadding(new Insets(20));
        form.setHgap(10);
        form.setVgap(10);

        Label clientLabel = new Label("Klijent:");
        ComboBox<String> clientCombo = new ComboBox<>();
        Label dateLabel = new Label("Datum:");
        DatePicker datePicker = new DatePicker();
        Label timeLabel = new Label("Vreme:");
        TextField timeField = new TextField("10:00");
        Label durationLabel = new Label("Trajanje (min):");
        TextField durationField = new TextField("60");
        Label priceLabel = new Label("Cena po satu:");
        TextField priceField = new TextField("5000.00");
        Label notesLabel = new Label("Beleške:");
        TextArea notesArea = new TextArea();
        Button submitButton = new Button("Zakaži");

        form.add(clientLabel, 0, 0);
        form.add(clientCombo, 1, 0);
        form.add(dateLabel, 0, 1);
        form.add(datePicker, 1, 1);
        form.add(timeLabel, 0, 2);
        form.add(timeField, 1, 2);
        form.add(durationLabel, 0, 3);
        form.add(durationField, 1, 3);
        form.add(priceLabel, 0, 4);
        form.add(priceField, 1, 4);
        form.add(notesLabel, 0, 5);
        form.add(notesArea, 1, 5);
        form.add(submitButton, 1, 6);

        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, CONCAT(ime, ' ', prezime) AS name FROM Klijent");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                clientCombo.getItems().add(rs.getString("name"));
            }
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri učitavanju klijenata: " + ex.getMessage());
            alert.showAndWait();
        }

        submitButton.setOnAction(e -> {
            try (Connection conn = DatabaseUtil.getConnection()) {
                CallableStatement cstmt = conn.prepareCall("{CALL DodajSesiju(?, ?, ?, ?, ?, ?, ?)}");
                int clientId = getClientId(clientCombo.getValue(), conn);
                cstmt.setInt(1, clientId);
                cstmt.setInt(2, user.getId());
                cstmt.setDate(3, datePicker.getValue() != null ? Date.valueOf(datePicker.getValue()) : null);
                cstmt.setTime(4, Time.valueOf(timeField.getText() + ":00"));
                cstmt.setInt(5, Integer.parseInt(durationField.getText()));
                cstmt.setString(6, notesArea.getText());
                cstmt.setDouble(7, Double.parseDouble(priceField.getText()));
                cstmt.execute();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Sesija zakazana");
                alert.showAndWait();
            } catch (SQLException | NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Greška: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        return form;
    }

    private GridPane createNotesForm() {
        GridPane form = new GridPane();
        form.setPadding(new Insets(20));
        form.setHgap(10);
        form.setVgap(10);

        Label sessionLabel = new Label("Sesija:");
        ComboBox<String> sessionCombo = new ComboBox<>();
        Label notesLabel = new Label("Beleške:");
        TextArea notesArea = new TextArea();
        Button submitButton = new Button("Sačuvaj");

        form.add(sessionLabel, 0, 0);
        form.add(sessionCombo, 1, 0);
        form.add(notesLabel, 0, 1);
        form.add(notesArea, 1, 1);
        form.add(submitButton, 1, 2);

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
            Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri učitavanju sesija: " + ex.getMessage());
            alert.showAndWait();
        }

        submitButton.setOnAction(e -> {
            try (Connection conn = DatabaseUtil.getConnection()) {
                PreparedStatement pstmt = conn.prepareStatement("UPDATE Sesija SET beleske = ? WHERE id = ?");
                pstmt.setString(1, notesArea.getText());
                int sessionId = getSessionId(sessionCombo.getValue(), conn);
                pstmt.setInt(2, sessionId);
                pstmt.executeUpdate();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Beleške sačuvane");
                alert.showAndWait();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Greška: " + ex.getMessage());
            }
        });

        return form;
    }

    private int getClientId(String clientName, Connection conn) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM Klijent WHERE CONCAT(ime, ' ', prezime) = ?");
        pstmt.setString(1, clientName);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        }
        throw new SQLException("Klijent nije pronađen");
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
