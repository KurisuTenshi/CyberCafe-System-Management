package application;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage login) throws IOException {
        try {
            // Load the login FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserLogin.fxml"));
            Parent root = loader.load();
            Scene loginScene = new Scene(root);
            Image icon = new Image(getClass().getResourceAsStream("cs26logo.png"));
	        login.getIcons().add(icon);
            login.setFullScreen(true);
            login.setFullScreenExitHint(""); // Remove default exit hint (e.g., press ESC to exit)
            login.setFullScreenExitKeyCombination(null); // Disable ESC to exit fullscreen
            loginScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            login.setScene(loginScene);
            login.show();

            } catch (Exception e) {
            e.printStackTrace();
        }
    }	

    public static void main(String[] args) {
        launch(args);


    }
}

