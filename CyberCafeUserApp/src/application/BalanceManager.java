package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class BalanceManager {
    private static final String URL = "jdbc:mysql://localhost:3306/CyberCafeSystemManagement";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";
    private static final double RATE_PER_HOUR = 50.0; // 50 per hour
    private static final double RATE_PER_MINUTE = RATE_PER_HOUR / 60.0; // Calculate per-minute rate
    private static Timeline balanceUpdateTimer;
    private static LocalDateTime lastUpdateTime;

    public static void startBalanceTracking() {
        User currentUser = UserSession.getCurrentUser();
        if (currentUser == null) return;

        lastUpdateTime = LocalDateTime.now();
        
        // Create a timer that updates every minute
        balanceUpdateTimer = new Timeline(
            new KeyFrame(Duration.minutes(1), event -> updateBalance())
        );
        balanceUpdateTimer.setCycleCount(Animation.INDEFINITE);
        balanceUpdateTimer.play();
    }

    public static void stopBalanceTracking() {
        if (balanceUpdateTimer != null) {
            balanceUpdateTimer.stop();
            // Perform final balance update
            updateBalance();
        }
    }

    private static void updateBalance() {
        User currentUser = UserSession.getCurrentUser();
        if (currentUser == null) return;

        try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD)) {
            // Get current balance
            String balanceQuery = "SELECT Balance FROM users WHERE UserID = ?";
            PreparedStatement balanceStmt = conn.prepareStatement(balanceQuery);
            balanceStmt.setInt(1, currentUser.getUserID());
            ResultSet rs = balanceStmt.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble("Balance");
                LocalDateTime now = LocalDateTime.now();
                
                // Calculate minutes elapsed since last update
                long minutesElapsed = java.time.Duration.between(lastUpdateTime, now).toMinutes();
                double charge = minutesElapsed * RATE_PER_MINUTE;
                
                // Update balance only if user has sufficient funds
                if (currentBalance >= charge) {
                    double newBalance = currentBalance - charge;
                    
                    // Update the balance in database
                    String updateQuery = "UPDATE users SET Balance = ? WHERE UserID = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setDouble(1, newBalance);
                    updateStmt.setInt(2, currentUser.getUserID());
                    updateStmt.executeUpdate();
                    
                    lastUpdateTime = now;
                } else {
                    // Handle insufficient funds
                    handleInsufficientFunds();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void handleInsufficientFunds() {
        // Run on JavaFX Application Thread
        javafx.application.Platform.runLater(() -> {
            UtilityClass.showAlert(
                javafx.scene.control.Alert.AlertType.WARNING,
                "Low Balance",
                "Your balance is insufficient to continue. Please add funds to your account."
            );
            
            // Trigger logout
            User currentUser = UserSession.getCurrentUser();
            if (currentUser != null) {
                UserSession.endSessionWithDuration();
                // Close the application
                System.exit(0);
            }
        });
    }
}