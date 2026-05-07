package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class loginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label erreurLabel;

    // Mock credentials — replace with real user table later
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    @FXML
    private void login() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            erreurLabel.setText("Please fill in all fields.");
            return;
        }

        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/mainMenu.fxml"));
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Fi Thniytek - Admin Panel");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            erreurLabel.setText("Invalid username or password.");
            passwordField.clear();
        }
    }
}