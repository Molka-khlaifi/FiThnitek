package com.example.couvoiturage.controller;

import com.example.couvoiturage.HelloApplication;
import com.example.couvoiturage.model.Role;
import com.example.couvoiturage.model.UserEntry;
import com.example.couvoiturage.util.*;
import com.example.couvoiturage.util.DatabaseConnection;
import com.example.couvoiturage.util.LogUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField captchaField;
    @FXML private Canvas captchaCanvas;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        CaptchaUtil.generateCaptcha(captchaCanvas);
    }

    @FXML
    void refreshCaptcha(ActionEvent event) {
        CaptchaUtil.generateCaptcha(captchaCanvas);
        captchaField.clear();
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        String captchaInput = captchaField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Veuillez remplir tous les champs.", "#e74c3c");
            return;
        }

        if (!CaptchaUtil.validate(captchaInput)) {
            showMessage("Code de vérification incorrect.", "#e74c3c");
            refreshCaptcha(null);
            return;
        }

        // Fetch user by email only, then verify hash
        String query = "SELECT * FROM utilisateur WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                boolean banned = rs.getBoolean("banned");

                if (banned) {
                    showMessage("Votre compte a été banni. Contactez l'administrateur.", "#e74c3c");
                    LogUtil.log(email, "Tentative de connexion refusée - compte banni");
                    return;
                }

                if (PasswordUtil.verify(password, storedHash)) {
                    String roleStr = rs.getString("role");
                    Role role = Role.valueOf(roleStr);

                    UserEntry user = new UserEntry(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("cin"),
                        rs.getString("telephone"),
                        rs.getString("email"),
                        "", // ne pas stocker le mot de passe en session
                        role,
                        banned
                    );
                    SessionManager.setCurrentUser(user);
                LogUtil.log(email, "Connexion réussie");
                
                if (role == Role.ADMIN) {
                        HelloApplication.changeScene("admin_dashboard.fxml", "Fi Thnitek - Admin");
                    } else {
                        HelloApplication.changeScene("user_home.fxml", "Fi Thnitek - Accueil");
                    }
                } else {
                    showMessage("Email ou mot de passe incorrect.", "#e74c3c");
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
        HelloApplication.changeScene("signup.fxml", "Fi Thnitek - Inscription");
    }

    @FXML
    void goToForgotPassword(ActionEvent event) throws IOException {
        HelloApplication.changeScene("forgot_password.fxml", "Fi Thnitek - Récupération");
    }
}
