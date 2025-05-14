package pog.Prozori;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pog.DatabaseUtil;
import pog.Model.User;

import java.sql.*;
import java.time.LocalDate;

public class PaymentsPane extends VBox {
    private TableView<Payment> paymentsTable;
    private TableView<ClientDue> duesTable;
    private User user;
    private Label noDuesLabel;

    public PaymentsPane(User user) {
        this.user = user;
        setPadding(new Insets(20));
        setSpacing(10);
        getStyleClass().add("content-pane");

        // Payments Table
        paymentsTable = new TableView<>();
        paymentsTable.getStyleClass().add("table-view");

        TableColumn<Payment, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Payment, String> clientColumn = new TableColumn<>("Klijent");
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        TableColumn<Payment, Double> amountColumn = new TableColumn<>("Iznos");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<Payment, String> currencyColumn = new TableColumn<>("Valuta");
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currency"));
        TableColumn<Payment, LocalDate> dateColumn = new TableColumn<>("Datum");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<Payment, String> methodColumn = new TableColumn<>("Način plaćanja");
        methodColumn.setCellValueFactory(new PropertyValueFactory<>("method"));
        TableColumn<Payment, Integer> installmentColumn = new TableColumn<>("Rata");
        installmentColumn.setCellValueFactory(new PropertyValueFactory<>("installment"));

        paymentsTable.getColumns().addAll(idColumn, clientColumn, amountColumn, currencyColumn, dateColumn, methodColumn, installmentColumn);

        // Client Dues Table
        duesTable = new TableView<>();
        duesTable.getStyleClass().add("table-view");

        TableColumn<ClientDue, String> dueClientColumn = new TableColumn<>("Klijent");
        dueClientColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        TableColumn<ClientDue, Double> balanceColumn = new TableColumn<>("Dugovanje (RSD)");
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));

        duesTable.getColumns().addAll(dueClientColumn, balanceColumn);

        // No dues message
        noDuesLabel = new Label("Nema dugovanja za ovog terapeuta.");
        noDuesLabel.setVisible(false);
        duesTable.setPlaceholder(noDuesLabel);

        Button refreshButton = new Button("Osveži");
        refreshButton.getStyleClass().add("button");
        Button addPaymentButton = new Button("Dodaj plaćanje");
        addPaymentButton.getStyleClass().add("button");

        refreshButton.setOnAction(e -> {
            loadPayments();
            loadClientDues();
        });
        addPaymentButton.setOnAction(e -> showAddPaymentForm());

        getChildren().addAll(new Label("Plaćanja"), paymentsTable, new Label("Dugovanja klijenata"), duesTable, noDuesLabel, refreshButton, addPaymentButton);
        loadPayments();
        loadClientDues();
    }

    private void loadPayments() {
        paymentsTable.getItems().clear();
        try (Connection conn = DatabaseUtil.getConnection()) {
            CallableStatement cstmt = conn.prepareCall("{CALL GetPaymentsByTherapist(?)}");
            cstmt.setInt(1, user.getId());
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()) {
                paymentsTable.getItems().add(new Payment(
                        rs.getInt("id"),
                        rs.getString("client_name"),
                        rs.getDouble("iznos"),
                        rs.getString("valuta_kod"),
                        rs.getDate("datum_uplate").toLocalDate(),
                        rs.getString("nacin"),
                        rs.getObject("rata") != null ? rs.getInt("rata") : null
                ));
            }
            System.out.println("Loaded " + paymentsTable.getItems().size() + " payments for therapist ID: " + user.getId());
        } catch (SQLException ex) {
            System.err.println("SQL Error in loadPayments: " + ex.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri učitavanju plaćanja: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    private void loadClientDues() {
        duesTable.getItems().clear();
        noDuesLabel.setVisible(false);
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Check sessions exist
            CallableStatement sessionStmt = conn.prepareCall("{CALL CheckSessionCount(?, ?)}");
            sessionStmt.setInt(1, user.getId());
            sessionStmt.registerOutParameter(2, Types.INTEGER);
            sessionStmt.execute();
            int sessionCount = sessionStmt.getInt(2);
            System.out.println("Found " + sessionCount + " sessions for therapist ID: " + user.getId());

            if (sessionCount == 0) {
                noDuesLabel.setVisible(true);
                return;
            }

            // Load client dues
            CallableStatement cstmt = conn.prepareCall("{CALL GetClientDuesByTherapist(?)}");
            cstmt.setInt(1, user.getId());
            ResultSet rs = cstmt.executeQuery();
            int rowCount = 0;
            while (rs.next()) {
                double totalCost = rs.getDouble("total_cost");
                double totalPaid = rs.getDouble("total_paid");
                double balance = rs.getDouble("balance");
                System.out.println("Client: " + rs.getString("client_name") + ", Total Cost: " + totalCost + ", Total Paid: " + totalPaid + ", Balance: " + balance);
                duesTable.getItems().add(new ClientDue(
                        rs.getString("client_name"),
                        balance
                ));
                rowCount++;
            }
            System.out.println("Loaded " + rowCount + " client dues for therapist ID: " + user.getId());
            if (rowCount == 0) {
                noDuesLabel.setVisible(true);
            }
        } catch (SQLException ex) {
            System.err.println("SQL Error in loadClientDues: " + ex.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Greška pri učitavanju dugovanja: " + ex.getMessage());
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
        Label currencyLabel = new Label("Valuta:");
        currencyLabel.getStyleClass().add("label");
        ComboBox<String> currencyCombo = new ComboBox<>();
        currencyCombo.getItems().addAll("RSD", "EUR", "USD", "CHF", "GBP");
        currencyCombo.setValue("RSD");
        currencyCombo.getStyleClass().add("combo-box");
        Label methodLabel = new Label("Način plaćanja:");
        methodLabel.getStyleClass().add("label");
        ComboBox<String> methodCombo = new ComboBox<>();
        methodCombo.getItems().addAll("gotovina", "kartica");
        methodCombo.getStyleClass().add("combo-box");
        Label installmentsLabel = new Label("Broj rata:");
        installmentsLabel.getStyleClass().add("label");
        ComboBox<String> installmentsCombo = new ComboBox<>();
        installmentsCombo.getItems().addAll("1", "2");
        installmentsCombo.setValue("1");
        installmentsCombo.setDisable(true);
        installmentsCombo.getStyleClass().add("combo-box");

        currencyCombo.setOnAction(e -> installmentsCombo.setDisable(!currencyCombo.getValue().equals("RSD")));

        Button submitButton = new Button("Potvrdi");
        submitButton.getStyleClass().add("button");

        form.add(sessionLabel, 0, 0);
        form.add(sessionCombo, 1, 0);
        form.add(amountLabel, 0, 1);
        form.add(amountField, 1, 1);
        form.add(currencyLabel, 0, 2);
        form.add(currencyCombo, 1, 2);
        form.add(methodLabel, 0, 3);
        form.add(methodCombo, 1, 3);
        form.add(installmentsLabel, 0, 4);
        form.add(installmentsCombo, 1, 4);
        form.add(submitButton, 1, 5);

        // Load sessions into ComboBox
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

        submitButton.setOnAction(e -> {
            try (Connection conn = DatabaseUtil.getConnection()) {
                if (sessionCombo.getValue() == null || amountField.getText().isEmpty() || methodCombo.getValue() == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Popunite sva polja!");
                    alert.showAndWait();
                    return;
                }
                int sessionId = getSessionId(sessionCombo.getValue(), conn);
                double totalAmount = Double.parseDouble(amountField.getText());
                if (totalAmount <= 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Iznos mora biti veći od 0!");
                    alert.showAndWait();
                    return;
                }
                String currency = currencyCombo.getValue();
                String method = methodCombo.getValue();
                int installments = Integer.parseInt(installmentsCombo.getValue());
                LocalDate date = LocalDate.now();

                CallableStatement cstmt = conn.prepareCall("{CALL DodajPlacanje(?, ?, ?, ?, ?, ?, ?)}");
                double installmentAmount = totalAmount / installments;

                for (int i = 1; i <= installments; i++) {
                    cstmt.setInt(1, sessionId);
                    cstmt.setNull(2, Types.INTEGER); // test_rezultat_id
                    cstmt.setDouble(3, installmentAmount);
                    cstmt.setString(4, currency);
                    cstmt.setString(5, method);
                    cstmt.setObject(6, currency.equals("RSD") && installments > 1 ? i : null, Types.INTEGER);
                    cstmt.setDate(7, Date.valueOf(date));
                    cstmt.execute();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Plaćanje dodano");
                alert.showAndWait();
                dialog.close();
                loadPayments();
                loadClientDues();
            } catch (SQLException | NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Greška: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        Scene scene = new Scene(form, 400, 350);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialog.setScene(scene);
        dialog.show();
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

    public static class Payment {
        private final int id;
        private final String clientName;
        private final double amount;
        private final String currency;
        private final LocalDate date;
        private final String method;
        private final Integer installment;

        public Payment(int id, String clientName, double amount, String currency, LocalDate date, String method, Integer installment) {
            this.id = id;
            this.clientName = clientName;
            this.amount = amount;
            this.currency = currency;
            this.date = date;
            this.method = method;
            this.installment = installment;
        }

        public int getId() { return id; }
        public String getClientName() { return clientName; }
        public double getAmount() { return amount; }
        public String getCurrency() { return currency; }
        public LocalDate getDate() { return date; }
        public String getMethod() { return method; }
        public Integer getInstallment() { return installment; }
    }

    public static class ClientDue {
        private final String clientName;
        private final double balance;

        public ClientDue(String clientName, double balance) {
            this.clientName = clientName;
            this.balance = balance;
        }

        public String getClientName() { return clientName; }
        public double getBalance() { return balance; }
    }
}