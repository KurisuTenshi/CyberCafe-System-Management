package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class UserAccountsController implements Initializable {
    
	@FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField contactField;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button clearButton;
    @FXML private Button registerButton;
    
    // FXML Table Elements
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> userIdColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> firstNameColumn;
    @FXML private TableColumn<User, String> lastNameColumn;
    @FXML private TableColumn<User, String> contactColumn;
    @FXML private TableColumn<User, Double> balanceColumn;
    
    // Balance Update Elements
    @FXML private TextField userIdField;
    @FXML private TextField balanceField;
    
    // Database connection details
    private final String URL = "jdbc:mysql://localhost:3306/CyberCafeSystemManagement";
    private final String USERNAME = "root";
    private final String PASSWORD = "";
    
    // Observable list for TableView
    private ObservableList<User> userList;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the table columns first
        initializeTableColumns();
        // Then load the data
        loadTableData1();
        // Setup button handlers
        setupEventHandlers();
    }
    
    private void setupEventHandlers() {
		// TODO Auto-generated method stub
		
	}

	private void initializeTableColumns() {
        // Make sure these property names match exactly with your User class getter methods
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
    }
    
    private void loadTableData1() {
        // Initialize the ObservableList
        userList = FXCollections.observableArrayList();
        
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT UserID, Username, FirstName, LastName, Contact, Balance FROM users";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    User user = new User(
                        rs.getInt("UserID"),
                        rs.getString("Username"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Contact"),
                        rs.getDouble("Balance")
                    );
                    userList.add(user);
                }
                
                // Set the items to the table
                userTable.setItems(userList);
            }
            
        } catch (SQLException e) {
            showAlert("Error", "Error loading table data: " + e.getMessage());
            e.printStackTrace(); // This will help with debugging
        }
    }
    
    // ... rest of the controller code remains the same ...

    
    @FXML
    private void registerUser() {
        if (!validateForm()) {
            showAlert("Validation Error", "Please fill all fields correctly.");
            return;
        }
        
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO users (FirstName, LastName, Email, Contact, Username, Password, Balance) " +
                        "VALUES (?, ?, ?, ?, ?, ?, 0.00)";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, firstNameField.getText());
            pstmt.setString(2, lastNameField.getText());
            pstmt.setString(3, emailField.getText());
            pstmt.setString(4, contactField.getText());
            pstmt.setString(5, usernameField.getText());
            pstmt.setString(6, passwordField.getText());
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                showAlert("Success", "User registered successfully!");
                clearForm();
                loadTableData1();
            }
            
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                showAlert("Error", "Username already exists!");
            } else {
                showAlert("Error", "Database error: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void updateBalance() {
        if (userIdField.getText().isEmpty() || balanceField.getText().isEmpty()) {
            showAlert("Error", "Please enter User ID and Balance amount");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdField.getText());
            double balanceToAdd = Double.parseDouble(balanceField.getText());
            
            try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
                String sql = "UPDATE users SET Balance = Balance + ? WHERE UserID = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setDouble(1, balanceToAdd);
                pstmt.setInt(2, userId);
                
                int result = pstmt.executeUpdate();
                if (result > 0) {
                    showAlert("Success", "Balance updated successfully!");
                    loadTableData1();
                    userIdField.clear();
                    balanceField.clear();
                } else {
                    showAlert("Error", "User not found!");
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers");
        } catch (SQLException e) {
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }
    
    private void loadTableData() {
        userList = FXCollections.observableArrayList();
        
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT UserID, Username, FirstName, LastName, Contact, Balance FROM users";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            
            while (rs.next()) {
                userList.add(new User(
                    rs.getInt("UserID"),
                    rs.getString("Username"),
                    rs.getString("FirstName"),
                    rs.getString("LastName"),
                    rs.getString("Contact"),
                    rs.getDouble("Balance")
                ));
            }
            
            userTable.setItems(userList);
            
        } catch (SQLException e) {
            showAlert("Error", "Error loading table data: " + e.getMessage());
        }
    }
    
    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        contactField.clear();
        emailField.clear();
        usernameField.clear();
        passwordField.clear();
    }
    
    private boolean validateForm() {
        return !firstNameField.getText().isEmpty() &&
               !lastNameField.getText().isEmpty() &&
               !contactField.getText().isEmpty() &&
               !emailField.getText().isEmpty() &&
               !usernameField.getText().isEmpty() &&
               !passwordField.getText().isEmpty();
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}