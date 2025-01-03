package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.sql.*;
import java.time.LocalDateTime;

public class ActiveSessionsController {

    @FXML private TableView<ActiveSessionsTable> tableView;
    @FXML private TableColumn<ActiveSessionsTable, Integer> colSessionID;
    @FXML private TableColumn<ActiveSessionsTable, Time> colDuration;
    @FXML private TableColumn<ActiveSessionsTable, LocalDateTime> colStartTime;
    @FXML private TableColumn<ActiveSessionsTable, LocalDateTime> colEndTime;
    @FXML private TableColumn<ActiveSessionsTable, String> colStatus;
    @FXML private TableColumn<ActiveSessionsTable, Integer> colUserID;
    @FXML private TableColumn<ActiveSessionsTable, Integer> colUnitID;
    @FXML private TableColumn<ActiveSessionsTable, Integer> colAdminID;

    private ObservableList<ActiveSessionsTable> sessionsList = FXCollections.observableArrayList();
    private Timeline refreshTimeline;

    @FXML
    public void initialize() {
        try {
            // Initialize columns
            colSessionID.setCellValueFactory(new PropertyValueFactory<>("sessionID"));
            colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
            colStartTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
            colEndTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            colUserID.setCellValueFactory(new PropertyValueFactory<>("userID"));
            colUnitID.setCellValueFactory(new PropertyValueFactory<>("unitID"));
            colAdminID.setCellValueFactory(new PropertyValueFactory<>("adminID"));

            // Set up auto-refresh every 5 seconds
            setupAutoRefresh();
            
            // Initial data load
            loadSessionsData();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Initialization Error", 
                     "Failed to initialize table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupAutoRefresh() {
        refreshTimeline = new Timeline(
            new KeyFrame(Duration.seconds(5), event -> loadSessionsData())
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    private void loadSessionsData() {
        String url = "jdbc:mysql://localhost:3306/CyberCafeSystemManagement";
        String username = "root";
        String password = "";

        // Modified query to be case-insensitive and show query results
        String query = "SELECT * FROM session WHERE LOWER(Status) = LOWER('active')";
//        System.out.println("Executing query: " + query);

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();
            sessionsList.clear();
            int rowCount = 0;

            while (rs.next()) {
                rowCount++;
                Time duration = rs.getTime("Duration");
                Timestamp startTimestamp = rs.getTimestamp("StartTime");
                Timestamp endTimestamp = rs.getTimestamp("EndTime");
                
                LocalDateTime startTime = startTimestamp != null ? 
                    startTimestamp.toLocalDateTime() : null;
                LocalDateTime endTime = endTimestamp != null ? 
                    endTimestamp.toLocalDateTime() : null;

                String status = rs.getString("Status");
                Integer sessionId = rs.getInt("SessionID");
                
                // Debug output
//                System.out.println("Loading session: " + sessionId + 
//                                 ", Status: " + status + 
//                                 ", StartTime: " + startTime);

                ActiveSessionsTable session = new ActiveSessionsTable(
                    sessionId,
                    duration,
                    startTime,
                    endTime,
                    status,
                    rs.getInt("AdminID"),
                    rs.getInt("UserID"),
                    rs.getInt("UnitID")
                );
                sessionsList.add(session);
            }

//            System.out.println("Total rows loaded: " + rowCount);
            tableView.setItems(sessionsList);

        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            showAlert(AlertType.ERROR, "Database Error", 
                     "Error loading sessions data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Method to refresh the table data manually
    @FXML
    public void refreshTable() {
        loadSessionsData();
    }

    // Clean up method to stop the timeline when the controller is destroyed
    public void cleanup() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
    }
}