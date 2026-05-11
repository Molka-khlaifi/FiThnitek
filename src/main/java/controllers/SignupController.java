package controllers;

import models.Role;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.Main;
import util.DBConnection;

import java.io.IOException;
import java.sql.*;

public class SignupController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField cinField;
    @FXML private TextField telephoneField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private VBox conducteurFields;
    @FXML private TextField marqueField;
    @FXML private TextField modeleField;
    @FXML private TextField immatriculationField;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        // Masquer le rôle ADMIN pour les utilisateurs normaux
        roleComboBox.setItems(FXCollections.observableArrayList(Role.PASSAGER, Role.CONDUCTEUR));
    }

    @FXML
    void handleRoleSelection(ActionEvent event) {
        Role selectedRole = roleComboBox.getValue();
        boolean isConducteur = (selectedRole == Role.CONDUCTEUR);
        conducteurFields.setVisible(isConducteur);
        conducteurFields.setManaged(isConducteur);
    }

    @FXML
    void handleSignup(ActionEvent event) {
        if (!validateFields()) return;

        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConn();
            conn.setAutoCommit(false);

            // 1. Insertion dans la table utilisateur
            String userSql = "INSERT INTO utilisateur (nom, prenom, cin, telephone, email, password, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement userPstmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            userPstmt.setString(1, nomField.getText());
            userPstmt.setString(2, prenomField.getText());
            userPstmt.setString(3, cinField.getText());
            userPstmt.setString(4, telephoneField.getText());
            userPstmt.setString(5, emailField.getText());
            userPstmt.setString(6, passwordField.getText());
            userPstmt.setString(7, roleComboBox.getValue().toString());
            
            userPstmt.executeUpdate();
            
            ResultSet rs = userPstmt.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);
                
                // 2. Insertion dans les tables spécifiques
                if (roleComboBox.getValue() == Role.CONDUCTEUR) {
                    String condSql = "INSERT INTO conducteur_info (utilisateur_id, marque_voiture, modele_voiture, immatriculation) VALUES (?, ?, ?, ?)";
                    PreparedStatement condPstmt = conn.prepareStatement(condSql);
                    condPstmt.setInt(1, userId);
                    condPstmt.setString(2, marqueField.getText());
                    condPstmt.setString(3, modeleField.getText());
                    condPstmt.setString(4, immatriculationField.getText());
                    condPstmt.executeUpdate();
                } else if (roleComboBox.getValue() == Role.PASSAGER) {
                    String passSql = "INSERT INTO passager_info (utilisateur_id) VALUES (?)";
                    PreparedStatement passPstmt = conn.prepareStatement(passSql);
                    passPstmt.setInt(1, userId);
                    passPstmt.executeUpdate();
                }
            }

            conn.commit();
            showMessage("Compte créé avec succès !", "#27ae60");
            
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignore rollback errors */ }
            }
            
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("cin")) {
                    showMessage("Ce numéro de CIN est déjà utilisé.", "#e74c3c");
                } else if (e.getMessage().contains("email")) {
                    showMessage("Cette adresse email est déjà utilisée.", "#e74c3c");
                } else {
                    showMessage("Ces informations existent déjà dans notre système.", "#e74c3c");
                }
            } else {
                e.printStackTrace();
                showMessage("Une erreur technique est survenue lors de l'inscription.", "#e74c3c");
            }
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    private boolean validateFields() {
        String nom = nomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String phone = telephoneField.getText().trim();
        String cin = cinField.getText().trim();
        Role role = roleComboBox.getValue();

        if (nom.isEmpty() || email.isEmpty() || password.isEmpty() || role == null || cin.isEmpty()) {
            showMessage("Veuillez remplir tous les champs obligatoires (*).", "#e74c3c");
            return false;
        }

        // Validation Email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showMessage("Format d'email invalide.", "#e74c3c");
            return false;
        }

        // Validation Téléphone (ex: 8 chiffres)
        if (!phone.isEmpty() && !phone.matches("^[0-9]{8,15}$")) {
            showMessage("Le numéro de téléphone doit contenir entre 8 et 15 chiffres.", "#e74c3c");
            return false;
        }

        // Validation CIN (ex: 8 chiffres)
        if (!cin.matches("^[0-9A-Za-z]{8,12}$")) {
            showMessage("Le CIN doit contenir entre 8 et 12 caractères alphanumériques.", "#e74c3c");
            return false;
        }

        // Validation Mot de passe (min 6 caractères)
        if (password.length() < 6) {
            showMessage("Le mot de passe doit faire au moins 6 caractères.", "#e74c3c");
            return false;
        }

        if (role == Role.CONDUCTEUR) {
            if (marqueField.getText().trim().isEmpty() || immatriculationField.getText().trim().isEmpty()) {
                showMessage("Veuillez remplir les informations du véhicule.", "#e74c3c");
                return false;
            }
        }
        return true;
    }

    private void showMessage(String msg, String color) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    @FXML
    void goToLogin(ActionEvent event) throws IOException {
        Main.changeScene("login.fxml", "Fi Thnitek - Connexion");
    }
}
