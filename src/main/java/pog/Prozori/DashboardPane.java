package pog.Prozori;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import pog.DatabaseUtil;
import pog.Model.User;

import java.sql.*;

public class DashboardPane extends VBox {
    private User user;

    public DashboardPane(User user) {
        this.user = user;
        setPadding(new Insets(20));
        setSpacing(10);
        getStyleClass().add("content-pane");

        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("tab-pane");


        Tab performanceTab = new Tab("Performance");
        performanceTab.setClosable(false);
        TableView<PerformanceRecord> performanceTable = createPerformanceTable();
        loadPerformanceData(performanceTable);
        performanceTab.setContent(performanceTable);


        Tab peakHoursTab = new Tab("Peak Hours");
        peakHoursTab.setClosable(false);
        TableView<PeakHourRecord> peakHoursTable = createPeakHoursTable();
        loadPeakHoursData(peakHoursTable);
        peakHoursTab.setContent(peakHoursTable);


        Tab complianceTab = new Tab("Compliance");
        complianceTab.setClosable(false);
        TableView<ComplianceRecord> complianceTable = createComplianceTable();
        loadComplianceData(complianceTable);
        complianceTab.setContent(complianceTable);


        Tab educationTab = new Tab("Education");
        educationTab.setClosable(false);
        TableView<EducationRecord> educationTable = createEducationTable();
        loadEducationData(educationTable);
        educationTab.setContent(educationTable);

        tabPane.getTabs().addAll(performanceTab, peakHoursTab, complianceTab, educationTab);
        getChildren().add(tabPane);
    }

    private TableView<PerformanceRecord> createPerformanceTable() {
        TableView<PerformanceRecord> table = new TableView<>();
        table.getStyleClass().add("table-view");

        TableColumn<PerformanceRecord, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        TableColumn<PerformanceRecord, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().therapistNameProperty());
        TableColumn<PerformanceRecord, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> cellData.getValue().therapistTypeProperty());
        TableColumn<PerformanceRecord, Integer> sessionCountCol = new TableColumn<>("Sessions");
        sessionCountCol.setCellValueFactory(cellData -> cellData.getValue().sessionCountProperty().asObject());
        TableColumn<PerformanceRecord, Double> revenueCol = new TableColumn<>("Revenue (RSD)");
        revenueCol.setCellValueFactory(cellData -> cellData.getValue().totalRevenueProperty().asObject());
        TableColumn<PerformanceRecord, Double> avgDurationCol = new TableColumn<>("Avg Duration (min)");
        avgDurationCol.setCellValueFactory(cellData -> cellData.getValue().avgDurationProperty().asObject());
        TableColumn<PerformanceRecord, Integer> rankCol = new TableColumn<>("Rank");
        rankCol.setCellValueFactory(cellData -> cellData.getValue().revenueRankProperty().asObject());

        table.getColumns().addAll(idCol, nameCol, typeCol, sessionCountCol, revenueCol, avgDurationCol, rankCol);
        return table;
    }

    private void loadPerformanceData(TableView<PerformanceRecord> table) {
        ObservableList<PerformanceRecord> data = FXCollections.observableArrayList();
        String call = "{CALL GetTherapistPerformance()}";
        try (Connection conn = DatabaseUtil.getConnection();
             CallableStatement stmt = conn.prepareCall(call);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                data.add(new PerformanceRecord(
                        rs.getInt("id"),
                        rs.getString("therapist_name"),
                        rs.getString("therapist_type"),
                        rs.getInt("session_count"),
                        rs.getDouble("total_revenue_rsd"),
                        rs.getDouble("avg_session_duration_min"),
                        rs.getInt("revenue_rank")
                ));
            }
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Error loading performance data: " + ex.getMessage()).showAndWait();
        }
        table.setItems(data);
    }

    private TableView<PeakHourRecord> createPeakHoursTable() {
        TableView<PeakHourRecord> table = new TableView<>();
        table.getStyleClass().add("table-view");

        TableColumn<PeakHourRecord, Integer> hourCol = new TableColumn<>("Hour");
        hourCol.setCellValueFactory(cellData -> cellData.getValue().hourProperty().asObject());
        TableColumn<PeakHourRecord, Integer> sessionCountCol = new TableColumn<>("Session Count");
        sessionCountCol.setCellValueFactory(cellData -> cellData.getValue().sessionCountProperty().asObject());
        TableColumn<PeakHourRecord, Integer> clientCountCol = new TableColumn<>("Unique Clients");
        clientCountCol.setCellValueFactory(cellData -> cellData.getValue().uniqueClientsProperty().asObject());
        TableColumn<PeakHourRecord, Integer> therapistCountCol = new TableColumn<>("Active Therapists");
        therapistCountCol.setCellValueFactory(cellData -> cellData.getValue().activeTherapistsProperty().asObject());
        TableColumn<PeakHourRecord, Double> percentCol = new TableColumn<>("% of Total");
        percentCol.setCellValueFactory(cellData -> cellData.getValue().percentageProperty().asObject());

        table.getColumns().addAll(hourCol, sessionCountCol, clientCountCol, therapistCountCol, percentCol);
        return table;
    }

    private void loadPeakHoursData(TableView<PeakHourRecord> table) {
        ObservableList<PeakHourRecord> data = FXCollections.observableArrayList();
        String call = "{CALL GetPeakSessionHours()}";
        try (Connection conn = DatabaseUtil.getConnection();
             CallableStatement stmt = conn.prepareCall(call);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                data.add(new PeakHourRecord(
                        rs.getInt("session_hour"),
                        rs.getInt("session_count"),
                        rs.getInt("unique_clients"),
                        rs.getInt("active_therapists"),
                        rs.getDouble("percentage_of_total")
                ));
            }
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Error loading peak hours data: " + ex.getMessage()).showAndWait();
        }
        table.setItems(data);
    }

    private TableView<SupervisorRecord> createSupervisorTable() {
        TableView<SupervisorRecord> table = new TableView<>();
        table.getStyleClass().add("table-view");

        TableColumn<SupervisorRecord, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> cellData.getValue().supervisorIdProperty().asObject());
        TableColumn<SupervisorRecord, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().supervisorNameProperty());
        TableColumn<SupervisorRecord, Integer> candidateCol = new TableColumn<>("Candidates Supervised");
        candidateCol.setCellValueFactory(cellData -> cellData.getValue().candidatesSupervisedProperty().asObject());
        TableColumn<SupervisorRecord, Integer> sessionCol = new TableColumn<>("Supervised Sessions");
        sessionCol.setCellValueFactory(cellData -> cellData.getValue().supervisedSessionsProperty().asObject());
        TableColumn<SupervisorRecord, Integer> minutesCol = new TableColumn<>("Total Minutes");
        minutesCol.setCellValueFactory(cellData -> cellData.getValue().totalSupervisedMinutesProperty().asObject());
        TableColumn<SupervisorRecord, String> loadCol = new TableColumn<>("Workload");
        loadCol.setCellValueFactory(cellData -> cellData.getValue().workloadCategoryProperty());

        table.getColumns().addAll(idCol, nameCol, candidateCol, sessionCol, minutesCol, loadCol);
        return table;
    }

    private void loadSupervisorData(TableView<SupervisorRecord> table) {
        ObservableList<SupervisorRecord> data = FXCollections.observableArrayList();
        String call = "{CALL GetSupervisorLoad()}";
        try (Connection conn = DatabaseUtil.getConnection();
             CallableStatement stmt = conn.prepareCall(call);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                data.add(new SupervisorRecord(
                        rs.getInt("supervizor_id"),
                        rs.getString("supervisor_name"),
                        rs.getInt("candidates_supervised"),
                        rs.getInt("supervised_sessions"),
                        rs.getInt("total_supervised_minutes"),
                        rs.getString("workload_category")
                ));
            }
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Error loading supervisor data: " + ex.getMessage()).showAndWait();
        }
        table.setItems(data);
    }

    private TableView<ComplianceRecord> createComplianceTable() {
        TableView<ComplianceRecord> table = new TableView<>();
        table.getStyleClass().add("table-view");

        TableColumn<ComplianceRecord, Integer> idCol = new TableColumn<>("Session ID");
        idCol.setCellValueFactory(cellData -> cellData.getValue().sessionIdProperty().asObject());
        TableColumn<ComplianceRecord, String> nameCol = new TableColumn<>("Kandidat");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().kandidatNameProperty());
        TableColumn<ComplianceRecord, Date> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        TableColumn<ComplianceRecord, Time> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        TableColumn<ComplianceRecord, Integer> durationCol = new TableColumn<>("Duration");
        durationCol.setCellValueFactory(cellData -> cellData.getValue().durationProperty().asObject());
        TableColumn<ComplianceRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> cellData.getValue().complianceStatusProperty());

        table.getColumns().addAll(idCol, nameCol, dateCol, timeCol, durationCol, statusCol);
        return table;
    }

    private void loadComplianceData(TableView<ComplianceRecord> table) {
        ObservableList<ComplianceRecord> data = FXCollections.observableArrayList();
        String call = "{CALL GetSupervisionCompliance()}";
        try (Connection conn = DatabaseUtil.getConnection();
             CallableStatement stmt = conn.prepareCall(call);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                data.add(new ComplianceRecord(
                        rs.getInt("session_id"),
                        rs.getString("kandidat_name"),
                        rs.getDate("datum"),
                        rs.getTime("vreme_pocetka"),
                        rs.getInt("trajanje"),
                        rs.getString("compliance_status")
                ));
            }
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Error loading compliance data: " + ex.getMessage()).showAndWait();
        }
        table.setItems(data);
    }

    private TableView<EducationRecord> createEducationTable() {
        TableView<EducationRecord> table = new TableView<>();
        table.getStyleClass().add("table-view");

        TableColumn<EducationRecord, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> cellData.getValue().therapistTypeProperty());
        TableColumn<EducationRecord, String> facultyCol = new TableColumn<>("Faculty");
        facultyCol.setCellValueFactory(cellData -> cellData.getValue().facultyProperty());
        TableColumn<EducationRecord, String> centerCol = new TableColumn<>("Training Center");
        centerCol.setCellValueFactory(cellData -> cellData.getValue().trainingCenterProperty());
        TableColumn<EducationRecord, Integer> therapistCountCol = new TableColumn<>("Therapists");
        therapistCountCol.setCellValueFactory(cellData -> cellData.getValue().therapistCountProperty().asObject());
        TableColumn<EducationRecord, Integer> sessionCountCol = new TableColumn<>("Sessions");
        sessionCountCol.setCellValueFactory(cellData -> cellData.getValue().sessionCountProperty().asObject());
        TableColumn<EducationRecord, Double> revenueCol = new TableColumn<>("Revenue (RSD)");
        revenueCol.setCellValueFactory(cellData -> cellData.getValue().totalRevenueProperty().asObject());
        TableColumn<EducationRecord, Double> rateCol = new TableColumn<>("Avg Hourly Rate");
        rateCol.setCellValueFactory(cellData -> cellData.getValue().avgHourlyRateProperty().asObject());
        TableColumn<EducationRecord, Integer> rankCol = new TableColumn<>("Rank");
        rankCol.setCellValueFactory(cellData -> cellData.getValue().rankWithinTypeProperty().asObject());

        table.getColumns().addAll(typeCol, facultyCol, centerCol, therapistCountCol, sessionCountCol, revenueCol, rateCol, rankCol);
        return table;
    }

    private void loadEducationData(TableView<EducationRecord> table) {
        ObservableList<EducationRecord> data = FXCollections.observableArrayList();
        String call = "{CALL GetEducationalImpact()}";
        try (Connection conn = DatabaseUtil.getConnection();
             CallableStatement stmt = conn.prepareCall(call);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                data.add(new EducationRecord(
                        rs.getString("therapist_type"),
                        rs.getString("faculty"),
                        rs.getString("training_center"),
                        rs.getInt("therapist_count"),
                        rs.getInt("session_count"),
                        rs.getDouble("total_revenue_rsd"),
                        rs.getDouble("avg_hourly_rate"),
                        rs.getInt("rank_within_type")
                ));
            }
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Error loading education data: " + ex.getMessage()).showAndWait();
        }
        table.setItems(data);
    }

    // Model classes
    public static class PerformanceRecord {
        private final IntegerProperty id = new SimpleIntegerProperty();
        private final StringProperty therapistName = new SimpleStringProperty();
        private final StringProperty therapistType = new SimpleStringProperty();
        private final IntegerProperty sessionCount = new SimpleIntegerProperty();
        private final DoubleProperty totalRevenue = new SimpleDoubleProperty();
        private final DoubleProperty avgDuration = new SimpleDoubleProperty();
        private final IntegerProperty revenueRank = new SimpleIntegerProperty();

        public PerformanceRecord(int id, String therapistName, String therapistType, int sessionCount, double totalRevenue, double avgDuration, int revenueRank) {
            this.id.set(id);
            this.therapistName.set(therapistName);
            this.therapistType.set(therapistType);
            this.sessionCount.set(sessionCount);
            this.totalRevenue.set(totalRevenue);
            this.avgDuration.set(avgDuration);
            this.revenueRank.set(revenueRank);
        }

        public IntegerProperty idProperty() { return id; }
        public StringProperty therapistNameProperty() { return therapistName; }
        public StringProperty therapistTypeProperty() { return therapistType; }
        public IntegerProperty sessionCountProperty() { return sessionCount; }
        public DoubleProperty totalRevenueProperty() { return totalRevenue; }
        public DoubleProperty avgDurationProperty() { return avgDuration; }
        public IntegerProperty revenueRankProperty() { return revenueRank; }
    }

    public static class PeakHourRecord {
        private final IntegerProperty hour = new SimpleIntegerProperty();
        private final IntegerProperty sessionCount = new SimpleIntegerProperty();
        private final IntegerProperty uniqueClients = new SimpleIntegerProperty();
        private final IntegerProperty activeTherapists = new SimpleIntegerProperty();
        private final DoubleProperty percentage = new SimpleDoubleProperty();

        public PeakHourRecord(int hour, int sessionCount, int uniqueClients, int activeTherapists, double percentage) {
            this.hour.set(hour);
            this.sessionCount.set(sessionCount);
            this.uniqueClients.set(uniqueClients);
            this.activeTherapists.set(activeTherapists);
            this.percentage.set(percentage);
        }

        public IntegerProperty hourProperty() { return hour; }
        public IntegerProperty sessionCountProperty() { return sessionCount; }
        public IntegerProperty uniqueClientsProperty() { return uniqueClients; }
        public IntegerProperty activeTherapistsProperty() { return activeTherapists; }
        public DoubleProperty percentageProperty() { return percentage; }
    }

    public static class SupervisorRecord {
        private final IntegerProperty supervisorId = new SimpleIntegerProperty();
        private final StringProperty supervisorName = new SimpleStringProperty();
        private final IntegerProperty candidatesSupervised = new SimpleIntegerProperty();
        private final IntegerProperty supervisedSessions = new SimpleIntegerProperty();
        private final IntegerProperty totalSupervisedMinutes = new SimpleIntegerProperty();
        private final StringProperty workloadCategory = new SimpleStringProperty();

        public SupervisorRecord(int supervisorId, String supervisorName, int candidatesSupervised, int supervisedSessions, int totalSupervisedMinutes, String workloadCategory) {
            this.supervisorId.set(supervisorId);
            this.supervisorName.set(supervisorName);
            this.candidatesSupervised.set(candidatesSupervised);
            this.supervisedSessions.set(supervisedSessions);
            this.totalSupervisedMinutes.set(totalSupervisedMinutes);
            this.workloadCategory.set(workloadCategory);
        }

        public IntegerProperty supervisorIdProperty() { return supervisorId; }
        public StringProperty supervisorNameProperty() { return supervisorName; }
        public IntegerProperty candidatesSupervisedProperty() { return candidatesSupervised; }
        public IntegerProperty supervisedSessionsProperty() { return supervisedSessions; }
        public IntegerProperty totalSupervisedMinutesProperty() { return totalSupervisedMinutes; }
        public StringProperty workloadCategoryProperty() { return workloadCategory; }
    }

    public static class ComplianceRecord {
        private final IntegerProperty sessionId = new SimpleIntegerProperty();
        private final StringProperty kandidatName = new SimpleStringProperty();
        private final SimpleObjectProperty<Date> date = new SimpleObjectProperty<>();
        private final SimpleObjectProperty<Time> time = new SimpleObjectProperty<>();
        private final IntegerProperty duration = new SimpleIntegerProperty();
        private final StringProperty complianceStatus = new SimpleStringProperty();

        public ComplianceRecord(int sessionId, String kandidatName, Date date, Time time, int duration, String complianceStatus) {
            this.sessionId.set(sessionId);
            this.kandidatName.set(kandidatName);
            this.date.set(date);
            this.time.set(time);
            this.duration.set(duration);
            this.complianceStatus.set(complianceStatus);
        }

        public IntegerProperty sessionIdProperty() { return sessionId; }
        public StringProperty kandidatNameProperty() { return kandidatName; }
        public SimpleObjectProperty<Date> dateProperty() { return date; }
        public SimpleObjectProperty<Time> timeProperty() { return time; }
        public IntegerProperty durationProperty() { return duration; }
        public StringProperty complianceStatusProperty() { return complianceStatus; }
    }

    public static class EducationRecord {
        private final StringProperty therapistType = new SimpleStringProperty();
        private final StringProperty faculty = new SimpleStringProperty();
        private final StringProperty trainingCenter = new SimpleStringProperty();
        private final IntegerProperty therapistCount = new SimpleIntegerProperty();
        private final IntegerProperty sessionCount = new SimpleIntegerProperty();
        private final DoubleProperty totalRevenue = new SimpleDoubleProperty();
        private final DoubleProperty avgHourlyRate = new SimpleDoubleProperty();
        private final IntegerProperty rankWithinType = new SimpleIntegerProperty();

        public EducationRecord(String therapistType, String faculty, String trainingCenter, int therapistCount, int sessionCount, double totalRevenue, double avgHourlyRate, int rankWithinType) {
            this.therapistType.set(therapistType);
            this.faculty.set(faculty);
            this.trainingCenter.set(trainingCenter);
            this.therapistCount.set(therapistCount);
            this.sessionCount.set(sessionCount);
            this.totalRevenue.set(totalRevenue);
            this.avgHourlyRate.set(avgHourlyRate);
            this.rankWithinType.set(rankWithinType);
        }

        public StringProperty therapistTypeProperty() { return therapistType; }
        public StringProperty facultyProperty() { return faculty; }
        public StringProperty trainingCenterProperty() { return trainingCenter; }
        public IntegerProperty therapistCountProperty() { return therapistCount; }
        public IntegerProperty sessionCountProperty() { return sessionCount; }
        public DoubleProperty totalRevenueProperty() { return totalRevenue; }
        public DoubleProperty avgHourlyRateProperty() { return avgHourlyRate; }
        public IntegerProperty rankWithinTypeProperty() { return rankWithinType; }
    }
}