package pog.Prozori;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import pog.DatabaseUtil;
import pog.Model.User;
import pog.NoviPocetakApp;
import pog.Model.Session;

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
        TableColumn<Session, Double> priceColumn = new TableColumn<>("Cena po satu (RSD)");
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getCenaPoSatu()).asObject());

        table.getColumns().addAll(idColumn, dateColumn, timeColumn, clientColumn, durationColumn, priceColumn);
        return table;
    }

    private void loadPastSessions(TableView<Session> table) {
        table.getItems().clear();
        try (Connection conn = DatabaseUtil.getConnection()) {
            CallableStatement cstmt = conn.prepareCall("{CALL GetPastSessionsByTherapist(?)}");
            cstmt.setInt(1, user.getId());
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()) {
                table.getItems().add(new Session(
                        rs.getInt("id"),
                        rs.getDate("datum").toLocalDate(),
                        rs.getTime("vreme_pocetka").toLocalTime(),
                        rs.getString("client_name"),
                        rs.getInt("trajanje"),
                        rs.getDouble("cena_po_satu")
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
            CallableStatement cstmt = conn.prepareCall("{CALL GetFutureSessionsByTherapist(?)}");
            cstmt.setInt(1, user.getId());
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()) {
                table.getItems().add(new Session(
                        rs.getInt("id"),
                        rs.getDate("datum").toLocalDate(),
                        rs.getTime("vreme_pocetka").toLocalTime(),
                        rs.getString("client_name"),
                        rs.getInt("trajanje"),
                        rs.getDouble("cena_po_satu")
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
        submitButton.setDisable("kandidat".equals(user.getTip())); // Initially disabled for kandidat

        TextField supervisorField;
        if ("kandidat".equals(user.getTip())) {
            Label supervisorLabel = new Label("Supervizor:");
            supervisorLabel.getStyleClass().add("label");
            supervisorField = new TextField();
            supervisorField.setEditable(false);
            supervisorField.getStyleClass().add("text-field");
            form.add(supervisorLabel, 0, 6);
            form.add(supervisorField, 1, 6);
        } else {
            supervisorField = null;
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
        form.add(submitButton, 1, supervisorField != null ? 7 : 6);

        try (Connection conn = DatabaseUtil.getConnection()) {
            CallableStatement cstmt = conn.prepareCall("{CALL GetAllClients()}");
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()) {
                clientCombo.getItems().add(rs.getString("name"));
            }
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri učitavanju klijenata: " + ex.getMessage());
            alert.showAndWait();
        }

        // Update priceField based on client selection and duration
        clientCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try (Connection conn = DatabaseUtil.getConnection()) {
                    int clientId = getClientId(newVal, conn);
                    CallableStatement checkStmt = conn.prepareCall("{CALL CheckClientSessionCount(?, ?)}");
                    checkStmt.setInt(1, clientId);
                    checkStmt.registerOutParameter(2, Types.INTEGER);
                    checkStmt.execute();
                    int brojSeansi = checkStmt.getInt(2);
                    int duration = durationField.getText().isEmpty() ? 60 : Integer.parseInt(durationField.getText());
                    if (brojSeansi == 0 && duration <= 60) {
                        priceField.setText("0.00");
                        priceField.setDisable(true);
                    } else {
                        priceField.setText("5000.00");
                        priceField.setDisable(false);
                    }
                } catch (SQLException | NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri proveri klijenta: " + ex.getMessage());
                    alert.showAndWait();
                }
            }
        });

        // Update priceField when duration changes
        durationField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty() && clientCombo.getValue() != null) {
                try (Connection conn = DatabaseUtil.getConnection()) {
                    int clientId = getClientId(clientCombo.getValue(), conn);
                    CallableStatement checkStmt = conn.prepareCall("{CALL CheckClientSessionCount(?, ?)}");
                    checkStmt.setInt(1, clientId);
                    checkStmt.registerOutParameter(2, Types.INTEGER);
                    checkStmt.execute();
                    int brojSeansi = checkStmt.getInt(2);
                    int duration = Integer.parseInt(newVal);
                    if (brojSeansi == 0 && duration <= 60) {
                        priceField.setText("0.00");
                        priceField.setDisable(true);
                    } else {
                        priceField.setText("5000.00");
                        priceField.setDisable(false);
                    }
                } catch (SQLException | NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri proveri klijenta: " + ex.getMessage());
                    alert.showAndWait();
                }
            }
        });

        // Update supervisorField and submitButton when date or time changes (for kandidat)
        if ("kandidat".equals(user.getTip())) {
            Runnable updateSupervisor = () -> {
                if (datePicker.getValue() != null && timeField.getText().matches("\\d{2}:\\d{2}")) {
                    try (Connection conn = DatabaseUtil.getConnection()) {
                        CallableStatement cstmt = conn.prepareCall("{CALL GetActiveSupervisor(?, ?, ?, ?, ?)}");
                        cstmt.setInt(1, user.getId());
                        cstmt.setDate(2, Date.valueOf(datePicker.getValue()));
                        cstmt.setTime(3, Time.valueOf(timeField.getText() + ":00"));
                        cstmt.registerOutParameter(4, Types.INTEGER);
                        cstmt.registerOutParameter(5, Types.VARCHAR);
                        cstmt.execute();
                        int supervisorId = cstmt.getInt(4);
                        String supervisorName = cstmt.getString(5);
                        if (supervisorId == 0) {
                            supervisorField.setText("Nema aktivnog supervizora");
                            supervisorField.getStyleClass().add("text-field-error");
                            submitButton.setDisable(true);
                            Alert alert = new Alert(Alert.AlertType.WARNING, "Nema aktivnog supervizora za izabrani datum i vreme. Izaberite drugo vreme.");
                            alert.showAndWait();
                        } else {
                            supervisorField.setText(supervisorName);
                            supervisorField.getStyleClass().remove("text-field-error");
                            submitButton.setDisable(false);
                        }
                    } catch (SQLException ex) {
                        supervisorField.setText("Greška pri učitavanju supervizora");
                        supervisorField.getStyleClass().add("text-field-error");
                        submitButton.setDisable(true);
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri učitavanju supervizora: " + ex.getMessage());
                        alert.showAndWait();
                    }
                } else {
                    supervisorField.setText("");
                    supervisorField.getStyleClass().remove("text-field-error");
                    submitButton.setDisable(true);
                }
            };

            datePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateSupervisor.run());
            timeField.textProperty().addListener((obs, oldVal, newVal) -> updateSupervisor.run());
        }

        submitButton.setOnAction(e -> {
            try (Connection conn = DatabaseUtil.getConnection()) {
                int clientId = getClientId(clientCombo.getValue(), conn);
                int duration = Integer.parseInt(durationField.getText());
                double cenaPoSatu = Double.parseDouble(priceField.getText());

                // Validate inputs
                if (clientCombo.getValue() == null || datePicker.getValue() == null || timeField.getText().isEmpty() || duration <= 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Popunite sva obavezna polja!");
                    alert.showAndWait();
                    return;
                }
                if (!timeField.getText().matches("\\d{2}:\\d{2}")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Vreme mora biti u formatu HH:MM!");
                    alert.showAndWait();
                    return;
                }
                if (cenaPoSatu < 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Cena ne može biti negativna!");
                    alert.showAndWait();
                    return;
                }
                // For kandidat, check if supervisor exists
                if ("kandidat".equals(user.getTip())) {
                    CallableStatement cstmt = conn.prepareCall("{CALL GetActiveSupervisor(?, ?, ?, ?, ?)}");
                    cstmt.setInt(1, user.getId());
                    cstmt.setDate(2, Date.valueOf(datePicker.getValue()));
                    cstmt.setTime(3, Time.valueOf(timeField.getText() + ":00"));
                    cstmt.registerOutParameter(4, Types.INTEGER);
                    cstmt.registerOutParameter(5, Types.VARCHAR);
                    cstmt.execute();
                    int supervisorId = cstmt.getInt(4);
                    if (supervisorId == 0) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Nema aktivnog supervizora za izabrani datum i vreme!");
                        alert.showAndWait();
                        return;
                    }
                }

                CallableStatement cstmt = conn.prepareCall("{CALL DodajSesiju(?, ?, ?, ?, ?, ?, ?)}");
                cstmt.setInt(1, clientId);
                cstmt.setInt(2, user.getId());
                cstmt.setDate(3, Date.valueOf(datePicker.getValue()));
                cstmt.setTime(4, Time.valueOf(timeField.getText() + ":00"));
                cstmt.setInt(5, duration);
                cstmt.setString(6, notesArea.getText());
                cstmt.setDouble(7, cenaPoSatu);
                cstmt.execute();

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Sesija zakazana");
                alert.showAndWait();
                loadFutureSessions((TableView<Session>) ((TabPane) getChildren().get(0)).getTabs().get(1).getContent());
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
            CallableStatement cstmt = conn.prepareCall("{CALL GetSessionsByTherapist(?)}");
            cstmt.setInt(1, user.getId());
            ResultSet rs = cstmt.executeQuery();
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
                    CallableStatement notesStmt = conn.prepareCall("{CALL GetSessionNotes(?, ?)}");
                    notesStmt.setInt(1, sessionId);
                    notesStmt.registerOutParameter(2, Types.VARCHAR);
                    notesStmt.execute();
                    notesArea.setText(notesStmt.getString(2) != null ? notesStmt.getString(2) : "");

                    CallableStatement testStmt = conn.prepareCall("{CALL GetTestResult(?)}");
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

                    CallableStatement disclosureStmt = conn.prepareCall("{CALL GetDisclosureData(?)}");
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
                CallableStatement notesStmt = conn.prepareCall("{CALL UpdateSessionNotes(?, ?)}");
                notesStmt.setInt(1, sessionId);
                notesStmt.setString(2, notesArea.getText());
                notesStmt.execute();

                if (testConductedCheck.isSelected()) {
                    CallableStatement testStmt = conn.prepareCall("{CALL UpsertTestResult(?, ?, ?)}");
                    int testId = 1; // Placeholder, replace with actual logic
                    testStmt.setInt(1, sessionId);
                    testStmt.setInt(2, testId);
                    testStmt.setString(3, testResultArea.getText());
                    testStmt.execute();
                } else {
                    CallableStatement deleteTestStmt = conn.prepareCall("{CALL DeleteTestResult(?)}");
                    deleteTestStmt.setInt(1, sessionId);
                    deleteTestStmt.execute();
                }

                if (discloseCheck.isSelected()) {
                    CallableStatement disclosureStmt = conn.prepareCall("{CALL UpsertDisclosureData(?, ?, ?)}");
                    disclosureStmt.setInt(1, sessionId);
                    disclosureStmt.setString(2, reasonField.getText());
                    disclosureStmt.setString(3, disclosedToField.getText());
                    disclosureStmt.execute();
                } else {
                    CallableStatement deleteDisclosureStmt = conn.prepareCall("{CALL DeleteDisclosureData(?)}");
                    deleteDisclosureStmt.setInt(1, sessionId);
                    deleteDisclosureStmt.execute();
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
        CallableStatement cstmt = conn.prepareCall("{CALL GetClientIdByName(?, ?)}");
        cstmt.setString(1, clientName);
        cstmt.registerOutParameter(2, Types.INTEGER);
        cstmt.execute();
        int clientId = cstmt.getInt(2);
        if (clientId == 0) {
            throw new SQLException("Klijent nije pronađen");
        }
        return clientId;
    }

    private int getSessionId(String sessionInfo, Connection conn) throws SQLException {
        CallableStatement cstmt = conn.prepareCall("{CALL GetSessionIdByInfo(?, ?)}");
        cstmt.setString(1, sessionInfo);
        cstmt.registerOutParameter(2, Types.INTEGER);
        cstmt.execute();
        int sessionId = cstmt.getInt(2);
        if (sessionId == 0) {
            throw new SQLException("Sesija nije pronađena");
        }
        return sessionId;
    }
}