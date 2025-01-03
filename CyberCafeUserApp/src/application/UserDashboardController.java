package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

public class UserDashboardController {
	
	 private static final String URL = "jdbc:mysql://localhost:3306/CyberCafeSystemManagement";
	    private static final String DB_USERNAME = "root";
	    private static final String DB_PASSWORD = "";

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Button myAccountButton;

    @FXML
    private Button priceRangeButton;

    @FXML
    private Button hourPackagesButton;

    @FXML
    private Button editPasswordButton;

    @FXML
    private Button logoutButton;

    @FXML
    private void initialize() {
        // Set up mouse click events
        myAccountButton.setOnMouseClicked(event -> switchScene("MyAccount.fxml"));
        priceRangeButton.setOnMouseClicked(event -> switchScene("PriceRange.fxml"));
        hourPackagesButton.setOnMouseClicked(event -> switchScene("HourPackages.fxml"));
        editPasswordButton.setOnMouseClicked(event -> switchScene("EditPassword.fxml"));
        logoutButton.setOnMouseClicked(event -> logout());
    }

    

    @FXML
    private void logout() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Logout Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to log out?");
        confirmationAlert.setContentText("Your session will be ended.");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Stop balance tracking - ADD THIS LINE
                BalanceManager.stopBalanceTracking();
                
                if (UserSession.endSessionWithDuration()) {
                    // Reset computer unit status
                    resetComputerUnitStatus();
                    
                    Alert logoutAlert = new Alert(Alert.AlertType.INFORMATION);
                    logoutAlert.setTitle("Logout Successful");
                    logoutAlert.setHeaderText(null);
                    logoutAlert.setContentText("You have successfully logged out.");
                    logoutAlert.showAndWait();

                    Stage currentStage = (Stage) logoutButton.getScene().getWindow();
                    currentStage.close();
                    System.exit(0);
                }
            }
        });
    }

    private void resetComputerUnitStatus() {
        String query = "UPDATE computerunits SET Status = 'Available', UserID = NULL WHERE UnitID =?" ;
        try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD);
        		PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, UnitConfig.getUnitID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private int loggedInUserID;

    public void setLoggedInUserID(int userID) {
        this.loggedInUserID = userID;
    }



    private void switchScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Get the controller and set the UserID if it's EditPasswordController
            Object controller = loader.getController();
            if (controller instanceof EditPasswordController) {
                // Get UserID from UserSession
                User currentUser = UserSession.getCurrentUser();
                if (currentUser != null) {
                    ((EditPasswordController) controller).setLoggedInUserID(currentUser.getUserID());
                }
            }

            // Create a StackPane to center the content
            BorderPane centeredPane = new BorderPane();
//            centeredPane.setPrefSize(500,715);
            centeredPane.getChildren().add(root);
            BorderPane.setAlignment(root, Pos.CENTER);
            
            mainBorderPane.setCenter(centeredPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
