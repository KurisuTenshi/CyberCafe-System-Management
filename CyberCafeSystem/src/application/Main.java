package application;
		

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;



public class Main extends Application {
	@Override
	public void start(Stage login) throws IOException {
	    try {
	        // Correctly load the FXML file and link the controller class
	    	
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
	        Parent root = loader.load(); // This loads the FXML file into a parent node
	        Scene scene = new Scene(root);
	        login.setResizable(false);
	        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	        login.setScene(scene);
	        login.show();
	        Image icon = new Image(getClass().getResourceAsStream("cs26logo.png"));
	        login.getIcons().add(icon);

	        // Access the controller if needed (if you have a controller class)
//	        MainController controller = loader.getController();
	        // Perform any initial setup for the controller if required
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public static void main(String[] args) {
		launch(args);
		
		String URL = "jdbc:mysql://localhost:3306/CyberCafeSystemManagement";
		String Username = "root";
		String Password = "";
		
        try {
            // Establish the connection
            Connection conn = DriverManager.getConnection(URL, Username, Password);
            System.out.println("Connected to the database!");
            
            // Close the connection
            conn.close();
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }

		
	}
}
