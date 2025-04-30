package pog.Prozori;

import javafx.geometry  .Insets;
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
        getStyleClass().add("content-pane");

        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("tab-pane");

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
        table.getStyleClass().add("table-view");

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
        form.getStyleClass().add("form");

        Label clientLabel = new Label("Klijent:");
        clientLabel.getStyleClass().add("label");
        ComboBox<String> clientCombo = new ComboBox<>();
        clientCombo.getStyleClass().add("combo-box");
        Label dateLabel = new Label("Datum:");
        dateLabel.getStyleClass().add("label");
        DatePicker datePicker = new DatePicker();
        datePicker.getStyleClass().add("date-picker");
        Label timeLabel = new Label("Vreme:");
        timeLabel.getStyleClass().add("label");
        TextField timeField = new TextField("10:00");
        timeField.getStyleClass().add("text-field");
        Label durationLabel = new Label("Trajanje (min):");
        durationLabel.getStyleClass().add("label");
        TextField durationField = new TextField("60");
        durationField.getStyleClass().add("text-field");
        Label priceLabel = new Label("Cena po satu:");
        priceLabel.getStyleClass().add("label");
        TextField priceField = new TextField("5000.00");
        priceField.getStyleClass().add("text-field");
        Label notesLabel = new Label("Beleške:");
        notesLabel.getStyleClass().add("label");
        TextArea notesArea = new TextArea();
        notesArea.getStyleClass().add("text-area");
        Button submitButton = new Button("Zakaži");
        submitButton.getStyleClass().add("button");

        ComboBox<String> supervisorCombo = null;
        if ("kandidat".equals(user.getTip())) {
            Label supervisorLabel = new Label("Supervizor:");
            supervisorLabel.getStyleClass().add("label");
            supervisorCombo = new ComboBox<>();
            supervisorCombo.getStyleClass().add("combo-box");
            try (Connection conn = DatabaseUtil.getConnection()) {
                PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT id, CONCAT(ime, ' ', prezime) AS name FROM Terapeut WHERE tip = 'sertifikovani'"
                );
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    supervisorCombo.getItems().add(rs.getString("name"));
                }
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri učitavanju supervizora: " + ex.getMessage());
                alert.showAndWait();
            }
            form.add(supervisorLabel, 0, 6);
            form.add(supervisorCombo, 1, 6);
        }

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
        form.add(submitButton, 1, supervisorCombo != null ? 7 : 6);

        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, CONCAT(ime, ' ', prezime) AS name FROM Klijent");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                clientCombo.getItems().add(rs.getString("name"));
            }
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri učitavanju klijenata: " + ex.getMessage());
            alert.showAndWait();
            /// sdafsdfsfgdgsdfgsdf
        }

        final ComboBox<String> finalSupervisorCombo = supervisorCombo;
        submitButton.setOnAction(e -> {
            try (Connection conn = DatabaseUtil.getConnection()) {
                int clientId = getClientId(clientCombo.getValue(), conn);
                PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM Sesija WHERE klijent_id = ?");
                checkStmt.setInt(1, clientId);
                ResultSet rs = checkStmt.executeQuery();
                double cost = 0.0;
                if (rs.next() && rs.getInt(1) > 0) {
                    cost = Double.parseDouble(priceField.getText()) * (Integer.parseInt(durationField.getText()) / 60.0);
                }

                CallableStatement cstmt = conn.prepareCall("{CALL DodajSesiju(?, ?, ?, ?, ?, ?, ?, ?)}");
                cstmt.setInt(1, clientId);
                cstmt.setInt(2, user.getId());
                cstmt.setDate(3, datePicker.getValue() != null ? Date.valueOf(datePicker.getValue()) : null);
                cstmt.setTime(4, Time.valueOf(timeField.getText() + ":00"));
                cstmt.setInt(5, Integer.parseInt(durationField.getText()));
                cstmt.setString(6, notesArea.getText());
                cstmt.setDouble(7, cost);
                cstmt.setInt(8, "kandidat".equals(user.getTip()) && finalSupervisorCombo != null && finalSupervisorCombo.getValue() != null ? getSupervisorId(finalSupervisorCombo.getValue(), conn) : 0);
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
        form.getStyleClass().add("form");

        Label sessionLabel = new Label("Sesija:");
        sessionLabel.getStyleClass().add("label");
        ComboBox<String> sessionCombo = new ComboBox<>();
        sessionCombo.getStyleClass().add("combo-box");
        Label notesLabel = new Label("Beleške:");
        notesLabel.getStyleClass().add("label");
        TextArea notesArea = new TextArea();
        notesArea.getStyleClass().add("text-area");
        CheckBox testConductedCheck = new CheckBox("Test sproveden");
        testConductedCheck.getStyleClass().add("check-box");
        Label testAreaLabel = new Label("Oblast testa:");
        testAreaLabel.getStyleClass().add("label");
        TextField testAreaField = new TextField();
        testAreaField.getStyleClass().add("text-field");
        Label testNameLabel = new Label("Naziv testa:");
        testNameLabel.getStyleClass().add("label");
        TextField testNameField = new TextField();
        testNameField.getStyleClass().add("text-field");
        Label testCostLabel = new Label("Cena testa:");
        testCostLabel.getStyleClass().add("label");
        TextField testCostField = new TextField();
        testCostField.getStyleClass().add("text-field");
        Label testResultLabel = new Label("Rezultat testa:");
        testResultLabel.getStyleClass().add("label");
        TextArea testResultArea = new TextArea();
        testResultArea.getStyleClass().add("text-area");
        CheckBox discloseCheck = new CheckBox("Objavi podatke");
        discloseCheck.getStyleClass().add("check-box");
        Label reasonLabel = new Label("Razlog:");
        reasonLabel.getStyleClass().add("label");
        TextField reasonField = new TextField();
        reasonField.getStyleClass().add("text-field");
        Label disclosedToLabel = new Label("Objavljeno za:");
        disclosedToLabel.getStyleClass().add("label");
        TextField disclosedToField = new TextField();
        disclosedToField.getStyleClass().add("text-field");
        Button submitButton = new Button("Sačuvaj");
        submitButton.getStyleClass().add("button");

        form.add(sessionLabel, 0, 0);
        form.add(sessionCombo, 1, 0);
        form.add(notesLabel, 0, 1);
        form.add(notesArea, 1, 1);
        form.add(testConductedCheck, 0, 2);
        form.add(testAreaLabel, 0, 3);
        form.add(testAreaField, 1, 3);
        form.add(testNameLabel, 0, 4);
        form.add(testNameField, 1, 4);
        form.add(testCostLabel, 0, 5);
        form.add(testCostField, 1, 5);
        form.add(testResultLabel, 0, 6);
        form.add(testResultArea, 1, 6);
        form.add(discloseCheck, 0, 7);
        form.add(reasonLabel, 0, 8);
        form.add(reasonField, 1, 8);
        form.add(disclosedToLabel, 0, 9);
        form.add(disclosedToField, 1, 9);
        form.add(submitButton, 1, 10);

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

        sessionCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try (Connection conn = DatabaseUtil.getConnection()) {
                    int sessionId = getSessionId(newVal, conn);
                    PreparedStatement notesStmt = conn.prepareStatement("SELECT beleske FROM Sesija WHERE id = ?");
                    notesStmt.setInt(1, sessionId);
                    ResultSet notesRs = notesStmt.executeQuery();
                    if (notesRs.next()) {
                        notesArea.setText(notesRs.getString("beleske"));
                    } else {
                        notesArea.setText("");
                    }

                    PreparedStatement testStmt = conn.prepareStatement("SELECT * FROM Test_rezultat WHERE sesija_id = ?");
                    testStmt.setInt(1, sessionId);
                    ResultSet testRs = testStmt.executeQuery();
                    if (testRs.next()) {
                        testConductedCheck.setSelected(true);
                        testAreaField.setText(testRs.getString("oblast"));
                        testNameField.setText(testRs.getString("naziv"));
                        testCostField.setText(String.valueOf(testRs.getDouble("cena")));
                        testResultArea.setText(testRs.getString("rezultat"));
                    } else {
                        testConductedCheck.setSelected(false);
                        testAreaField.setText("");
                        testNameField.setText("");
                        testCostField.setText("");
                        testResultArea.setText("");
                    }

                    PreparedStatement disclosureStmt = conn.prepareStatement("SELECT * FROM Objava_podataka WHERE sesija_id = ?");
                    disclosureStmt.setInt(1, sessionId);
                    ResultSet disclosureRs = disclosureStmt.executeQuery();
                    if (disclosureRs.next()) {
                        discloseCheck.setSelected(true);
                        reasonField.setText(disclosureRs.getString("svrha"));
                        disclosedToField.setText(disclosureRs.getString("opis"));
                    } else {
                        discloseCheck.setSelected(false);
                        reasonField.setText("");
                        disclosedToField.setText("");
                    }
                } catch (SQLException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri učitavanju podataka: " + ex.getMessage());
                    alert.showAndWait();
                }
            }
        });

        submitButton.setOnAction(e -> {
            try (Connection conn = DatabaseUtil.getConnection()) {
                int sessionId = getSessionId(sessionCombo.getValue(), conn);
                PreparedStatement notesStmt = conn.prepareStatement("UPDATE Sesija SET beleske = ? WHERE id = ?");
                notesStmt.setString(1, notesArea.getText());
                notesStmt.setInt(2, sessionId);
                notesStmt.executeUpdate();

                if (testConductedCheck.isSelected()) {
                    PreparedStatement testStmt = conn.prepareStatement(
                            "INSERT INTO Test_rezultat (sesija_id, test_id, rezultat) VALUES (?, ?, ?) " +
                                    "ON DUPLICATE KEY UPDATE rezultat = ?"
                    );
                    int testId = 1; // Placeholder, replace with actual logic
                    testStmt.setInt(1, sessionId);
                    testStmt.setInt(2, testId);
                    testStmt.setString(3, testResultArea.getText());
                    testStmt.setString(4, testResultArea.getText());
                    testStmt.executeUpdate();
                } else {
                    PreparedStatement deleteTestStmt = conn.prepareStatement("DELETE FROM Test_rezultat WHERE sesija_id = ?");
                    deleteTestStmt.setInt(1, sessionId);
                    deleteTestStmt.executeUpdate();
                }

                if (discloseCheck.isSelected()) {
                    PreparedStatement disclosureStmt = conn.prepareStatement(
                            "INSERT INTO Objava_podataka (sesija_id, svrha, opis) VALUES (?, ?, ?) " +
                                    "ON DUPLICATE KEY UPDATE svrha = ?, opis = ?"
                    );
                    disclosureStmt.setInt(1, sessionId);
                    disclosureStmt.setString(2, reasonField.getText());
                    disclosureStmt.setString(3, disclosedToField.getText());
                    disclosureStmt.setString(4, reasonField.getText());
                    disclosureStmt.setString(5, disclosedToField.getText());
                    disclosureStmt.executeUpdate();
                } else {
                    PreparedStatement deleteDisclosureStmt = conn.prepareStatement("DELETE FROM Objava_podataka WHERE sesija_id = ?");
                    deleteDisclosureStmt.setInt(1, sessionId);
                    deleteDisclosureStmt.executeUpdate();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Podaci sačuvani");
                alert.showAndWait();
            } catch (SQLException | NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Greška: " + ex.getMessage());
                alert.showAndWait();
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

    private int getSupervisorId(String supervisorName, Connection conn) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM Terapeut WHERE CONCAT(ime, ' ', prezime) = ?");
        pstmt.setString(1, supervisorName);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        }
        throw new SQLException("Supervizor nije pronađen");
    }
}