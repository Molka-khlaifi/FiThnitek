package com.example.couvoiturage.controller;

import com.example.couvoiturage.HelloApplication;
import com.example.couvoiturage.model.Role;
import com.example.couvoiturage.util.DatabaseConnection;
import com.example.couvoiturage.util.LogUtil;
import com.example.couvoiturage.util.PasswordUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
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

    // Document fields
    @FXML private Label carteGrisFileLabel;
    @FXML private Label permisFileLabel;
    @FXML private Label messageLabel;

    private String carteGrisPath = null;
    private String permisPath = null;

    @FXML
    public void initialize() {
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
    void handleChooseCarteGris(ActionEvent event) {
        File file = openFileChooser("Sélectionner la Carte Grise");
        if (file != null) {
            carteGrisPath = file.getAbsolutePath();
            carteGrisFileLabel.setText(file.getName());
        }
    }

    @FXML
    void handleChoosePermis(ActionEvent event) {
        File file = openFileChooser("Sélectionner le Permis de Conduire");
        if (file != null) {
            permisPath = file.getAbsolutePath();
            permisFileLabel.setText(file.getName());
        }
    }

    private File openFileChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Documents (PDF, Images)", "*.pdf", "*.png", "*.jpg", "*.jpeg"),
            new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );
        Stage stage = (Stage) nomField.getScene().getWindow();
        return fileChooser.showOpenDialog(stage);
    }

    @FXML
    void handleSignup(ActionEvent event) {
        if (!validateFields()) return;

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Insertion dans la table utilisateur (mot de passe hashé)
            String userSql = "INSERT INTO utilisateur (nom, prenom, cin, telephone, email, password, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement userPstmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            userPstmt.setString(1, nomField.getText().trim());
            userPstmt.setString(2, prenomField.getText().trim());
            userPstmt.setString(3, cinField.getText().trim());
            userPstmt.setString(4, telephoneField.getText().trim());
            userPstmt.setString(5, emailField.getText().trim());
            userPstmt.setString(6, PasswordUtil.hash(passwordField.getText())); // BCrypt hash
            userPstmt.setString(7, roleComboBox.getValue().toString());

            userPstmt.executeUpdate();

            ResultSet rs = userPstmt.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);

                // 2. Insertion dans les tables spécifiques
                if (roleComboBox.getValue() == Role.CONDUCTEUR) {
                    String condSql = "INSERT INTO conducteur_info (utilisateur_id, carte_gris_path, permis_path) VALUES (?, ?, ?)";
                    PreparedStatement condPstmt = conn.prepareStatement(condSql);
                    condPstmt.setInt(1, userId);
                    condPstmt.setString(2, carteGrisPath);
                    condPstmt.setString(3, permisPath);
                    condPstmt.executeUpdate();
                } else if (roleComboBox.getValue() == Role.PASSAGER) {
                    String passSql = "INSERT INTO passager_info (utilisateur_id) VALUES (?)";
                    PreparedStatement passPstmt = conn.prepareStatement(passSql);
                    passPstmt.setInt(1, userId);
                    passPstmt.executeUpdate();
                }
            }

            conn.commit();
            LogUtil.log(emailField.getText(), "Nouvelle inscription (" + roleComboBox.getValue() + ")");
            showMessage("Compte créé avec succès ! En attente de validation.", "#27ae60");
            clearForm();

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
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

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showMessage("Format d'email invalide.", "#e74c3c");
            return false;
        }

        if (!phone.isEmpty() && !phone.matches("^[0-9]{8,15}$")) {
            showMessage("Le numéro de téléphone doit contenir entre 8 et 15 chiffres.", "#e74c3c");
            return false;
        }

        if (!cin.matches("^[0-9A-Za-z]{8,12}$")) {
            showMessage("Le CIN doit contenir entre 8 et 12 caractères alphanumériques.", "#e74c3c");
            return false;
        }

        if (password.length() < 6) {
            showMessage("Le mot de passe doit faire au moins 6 caractères.", "#e74c3c");
            return false;
        }

        if (role == Role.CONDUCTEUR) {
            if (carteGrisPath == null || permisPath == null) {
                showMessage("Veuillez joindre votre Carte Grise et votre Permis de Conduire.", "#e74c3c");
                return false;
            }
        }
        return true;
    }

    private void clearForm() {
        nomField.clear();
        prenomField.clear();
        cinField.clear();
        telephoneField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.setValue(null);
        carteGrisPath = null;
        permisPath = null;
        carteGrisFileLabel.setText("Aucun fichier sélectionné");
        permisFileLabel.setText("Aucun fichier sélectionné");
        conducteurFields.setVisible(false);
        conducteurFields.setManaged(false);
    }

    private void showMessage(String msg, String color) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    @FXML
    void goToLogin(ActionEvent event) throws IOException {
        HelloApplication.changeScene("login.fxml", "Fi Thnitek - Connexion");
    }
}
