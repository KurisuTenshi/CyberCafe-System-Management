package application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EditPasswordController {

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button clearButton;

    @FXML
    private Button confirmButton;

    private int loggedInUserID;

    public void setLoggedInUserID(int userID) {
        this.loggedInUserID = userID;
        System.out.println("UserID set in EditPasswordController: " + userID);
    }

    @FXML
    private void handleClear() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    @FXML
    private void handleConfirm() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled!");
            return;
        }

        if (!validateCurrentPassword(currentPassword)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Current password is incorrect!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Error", "New password and confirmation do not match!");
            return;
        }

        if (updatePasswordInDatabase(newPassword)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Password updated successfully!");
            handleClear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update password. Please try again.");
        }
    }

    private boolean valuseidateLogin(String username, String password) {
        boolean isValid = false;

        String URL = "jdbc:mysql://localhost:3306/CyberCafeSystemManagement";
        String Username = "root";
        String Password = "";

        String query = "SELECT UserID FROM Users WHERE Username = ? AND Password = ?";

        try (Connection conn = DriverManager.getConnection(URL, Username, Password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                loggedInUserID = rs.getInt("UserID"); // Retrieve UserID
                isValid = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isValid;
    }

    private boolean validateCurrentPassword(String currentPassword) {
        boolean isValid = false;
        
        // Get UserID from UserSession if not set
        if (loggedInUserID == 0) {
            User currentUser = UserSession.getCurrentUser();
            if (currentUser != null) {
                loggedInUserID = currentUser.getUserID();
            } else {
                System.out.println("No user found in current session");
                return false;
            }
        }

        String URL = "jdbc:mysql://localhost:3306/CyberCafeSystemManagement";
        String Username = "root";
        String Password = "";

        String query = "SELECT Password FROM users WHERE UserID = ?";
        try (Connection conn = DriverManager.getConnection(URL, Username, Password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, loggedInUserID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("Password");
                isValid = storedPassword.equals(currentPassword);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isValid;
    }

    private boolean updatePasswordInDatabase(String newPassword) {
        String URL = "jdbc:mysql://localhost:3306/CyberCafeSystemManagement";
        String Username = "root";
        String Password = "";

        String updateQuery = "UPDATE Users SET Password = ? WHERE UserID = ?";

        try (Connection conn = DriverManager.getConnection(URL, Username, Password);
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, newPassword);
            stmt.setInt(2, loggedInUserID);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
