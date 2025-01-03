package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    	
    @FXML
    private Button loginButton;

    @FXML
    private Button clearButton;

    private int loggedInAdminID; // Store the AdminID of the logged-in user

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Username or password cannot be empty!");
            return;
        }

        if (validateLogin(username, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, " + username + "!");
            loadNextScene();
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password!");
        }
    }

    @FXML
    private void handleClear() {
        usernameField.clear();
        passwordField.clear();
    }

    private boolean validateLogin(String username, String password) {
        boolean isValid = false;

        String URL = "jdbc:mysql://localhost:3306/CyberCafeSystemManagement";
        String Username = "root";
        String Password = "";

        String query = "SELECT AdminID FROM Admins WHERE AdminUsername = ? AND Password = ?";

        try (Connection conn = DriverManager.getConnection(URL, Username, Password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                isValid = true;
                loggedInAdminID = rs.getInt("AdminID"); // Retrieve and store the AdminID
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isValid;
    }

    private void loadNextScene() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Scene scene = new Scene(loader.load());

            // Pass the AdminID to the DashboardController
            DashboardController dashboardController = loader.getController();
            dashboardController.setLoggedInAdminID(loggedInAdminID);

            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
