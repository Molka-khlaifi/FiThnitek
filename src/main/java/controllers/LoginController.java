package controllers;

import utils.DBConnection;
import utils.SessionManager;
import models.Role;
import models.UserEntry;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.Main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Veuillez remplir tous les champs.", "#e74c3c");
            return;
        }

        String query = "SELECT * FROM utilisateur WHERE email = ? AND password = ?";

        try (Connection conn = DBConnection.getInstance().getConn();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Role role = Role.valueOf(rs.getString("role"));

                UserEntry user = new UserEntry(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("cin"),
                        rs.getString("telephone"), // ⚠️ vérifie le nom exact dans ta BDD
                        rs.getString("email"),
                        "",
                        role
                );

                SessionManager.setCurrentUser(user);

                if (role == Role.ADMIN) {
                    Main.changeScene("AdminHomePage.fxml", "Fi Thnitek - Admin");
                } else if (role == Role.CONDUCTEUR) {
                    Main.changeScene("ConducteurHomePage.fxml", "Fi Thnitek - Conducteur");
                } else if (role == Role.PASSAGER) {
                    Main.changeScene("PassagerHomePage.fxml", "Fi Thnitek - Passager");
                } else {
                    showMessage("Rôle inconnu.", "#e74c3c");
                }

            } else {
                showMessage("Email ou mot de passe incorrect.", "#e74c3c");
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showMessage("Erreur : " + e.getMessage(), "#e74c3c");
        }
    }

    private void showMessage(String msg, String color) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    @FXML
    void goToSignup(ActionEvent event) throws IOException {
        Main.changeScene("signup.fxml", "Fi Thnitek - Inscription");
    }
}