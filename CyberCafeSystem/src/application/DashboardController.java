package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Label dateTimeLabel; // Label for displaying date and time

    @FXML
    private Label fullName; // Label for displaying the full name

    @FXML
    private BorderPane mainBorderPane; // Reference to your BorderPane in the FXML

    @FXML
    private Button dashboardButton;
    @FXML
    private Button activeSessionButton;
    @FXML
    private Button userAccountsButton;
    @FXML
    private Button transactionsButton;
    @FXML
    private Button reportsButton;

    private int loggedInAdminID;

    @FXML
    public void initialize() {
        initializeDateTime(); // Start the clock when the controller initializes
        // Remove hardcoded fetch call here.
    }

    private void initializeDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime now = LocalDateTime.now();
            dateTimeLabel.setText(now.format(formatter));
        }));

        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    public void setLoggedInAdminID(int adminID) {
        this.loggedInAdminID = adminID;
        fetchAndDisplayAdminName(adminID); // Dynamically fetch and display the admin's name
    }

    private void fetchAndDisplayAdminName(int adminID) {
        String URL = "jdbc:mysql://localhost:3306/CyberCafeSystemManagement";
        String Username = "root";
        String Password = "";

        try (Connection conn = DriverManager.getConnection(URL, Username, Password)) {
            System.out.println("Connected to the database!");
            System.out.println("Fetching admin with ID: " + adminID);

            String query = "SELECT FirstName, LastName FROM Admins WHERE AdminID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, adminID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String firstName = rs.getString("FirstName");
                    String lastName = rs.getString("LastName");
                    String retrievedFullName = firstName + " " + lastName;
                    fullName.setText(retrievedFullName);
                } else {
                    fullName.setText("Admin not found.");
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to retrieve the admin's name.");
            e.printStackTrace();
            fullName.setText("Error fetching admin name");
        }
    }

    @FXML
    public void showDashboardPage(MouseEvent event) {
        loadPage("DashboardContent.fxml");
    }

    @FXML
    public void showActiveSessionPage(MouseEvent event) {
        loadPage("ActiveSessions.fxml");
    }

    @FXML
    public void showUserAccountsPage(MouseEvent event) {
        loadPage("UserAccounts.fxml");
    }

    @FXML
    public void showTransactionsPage(MouseEvent event) {
        loadPage("Transactions.fxml");
    }

    @FXML
    public void showReportsPage(MouseEvent event) {
        loadPage("Reports.fxml");
    }

    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Pane page = loader.load();
            mainBorderPane.setCenter(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
