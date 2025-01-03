package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

public class UserLoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/CyberCafeSystemManagement";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";

    
    @FXML
    private void handleLogin(MouseEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter both username and password.");
            return;
        }

        if (authenticateUser(username, password)) {
            // Start balance tracking - ADD THIS LINE
            BalanceManager.startBalanceTracking();
            
            // Close the login stage
            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();

            // Proceed to the main panel
            proceedToMainPanel();
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
        }
    }

    private boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE Username = ? AND Password = ?";
        try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // If the password is hashed, hash the input and compare it
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // User authenticated
                int userID = rs.getInt("UserID");
                String fetchedUsername = rs.getString("Username");
                LocalDateTime now = LocalDateTime.now();

                // Create a User object
                User user = new User(userID, fetchedUsername);

                // Create a session for the user
                createSession(user);

                // Update the last login time
                updateLastLogin(userID, now);

                return true;
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "No user found with the provided credentials.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database.");
            return false;
        }
    }
    



    private void createSession(User user) {
        LocalDateTime now = LocalDateTime.now();
        String createSessionQuery = "INSERT INTO session (StartTime, Status, UserID, UnitID) VALUES (?, 'active', ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(createSessionQuery, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setTimestamp(1, Timestamp.valueOf(now));
            stmt.setInt(2, user.getUserID());
            stmt.setInt(3, UnitConfig.getUnitID()); // Add UnitID to session
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int sessionID = generatedKeys.getInt(1);
                user.setSessionID(sessionID);
                UserSession.setCurrentUser(user);
                
                // Update computer unit status
                updateComputerUnitStatus(user.getUserID());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to create a new session.");
        }
    }

    private void updateComputerUnitStatus(int userID) {
        String updateQuery = "UPDATE computerunits SET Status = 'In Use', UserID = ? WHERE UnitID = ?";
        try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            
            stmt.setInt(1, userID);
            stmt.setInt(2, UnitConfig.getUnitID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateLastLogin(int userID, LocalDateTime loginTime) {
        String updateLoginTimeQuery = "UPDATE users SET LastLogIn = ? WHERE UserID = ?";
        try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(updateLoginTimeQuery)) {

            stmt.setTimestamp(1, Timestamp.valueOf(loginTime));
            stmt.setInt(2, userID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void proceedToMainPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserDashboard.fxml")); // Adjust path if necessary
            Parent mainRoot = loader.load();
            Scene mainScene = new Scene(mainRoot, 800, 600); // Set specific size for the main scene
            mainScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); // Correct stylesheet path
            Stage mainStage = new Stage();
            mainStage.setResizable(false);
            mainStage.setFullScreen(false); // Set fullscreen to false for the main application stage
            mainStage.setTitle("User Dashboard");
            mainStage.setScene(mainScene);
            mainStage.show(); // Show the main application
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the main panel.");
        }
    }
}
