package com.example.couvoiturage.controller;

import com.example.couvoiturage.HelloApplication;
import com.example.couvoiturage.model.Role;
import com.example.couvoiturage.model.UserEntry;
import com.example.couvoiturage.util.DatabaseConnection;
import com.example.couvoiturage.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserHomeController {

    @FXML private Label welcomeLabel;
    @FXML private VBox earningsCard;
    @FXML private Label earningsLabel;
    @FXML private ListView<String> recentTrajetsList;

    // Profile fields
    @FXML private TextField profileNomField;
    @FXML private TextField profilePrenomField;
    @FXML private TextField profileEmailField;
    @FXML private TextField profilePhoneField;
    @FXML private TextField profileCinField;
    @FXML private VBox profileConducteurFields;
    @FXML private ImageView profileCarteGrisView;
    @FXML private ImageView profilePermisView;
    @FXML private Label profileCarteGrisLabel;
    @FXML private Label profilePermisLabel;
    @FXML private Label profileMessageLabel;

    // Sections
    @FXML private VBox homeSection;
    @FXML private VBox profileSection;

    // Nav Buttons
    @FXML private Button homeNavBtn;
    @FXML private Button profileNavBtn;
    @FXML private Label userNameLabel;

    private UserEntry currentUser;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) return;

        welcomeLabel.setText("Bienvenue, " + currentUser.getPrenom() + " !");
        userNameLabel.setText(currentUser.getPrenom() + " " + currentUser.getNom());
        
        if (currentUser.getRole() == Role.CONDUCTEUR) {
            setupConducteurHome();
        }

        loadProfileData();
        
        // Simuler des trajets récents
        recentTrajetsList.getItems().addAll(
            "Tunis -> Sousse (Hier, 14:00)",
            "Nabeul -> Tunis (Lundi, 08:30)",
            "Bizerte -> Tunis (05/05, 10:00)"
        );

        showProfileSection(null);
    }

    private void setupConducteurHome() {
        earningsCard.setVisible(true);
        earningsCard.setManaged(true);
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT montant_gagne FROM conducteur_info WHERE utilisateur_id = ?")) {
            pstmt.setInt(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                earningsLabel.setText(String.format("%.2f DT", rs.getDouble("montant_gagne")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadProfileData() {
        profileNomField.setText(currentUser.getNom());
        profilePrenomField.setText(currentUser.getPrenom());
        profileEmailField.setText(currentUser.getEmail());
        profilePhoneField.setText(currentUser.getNumeroTelephone());
        profileCinField.setText(currentUser.getCin());

        if (currentUser.getRole() == Role.CONDUCTEUR) {
            profileConducteurFields.setVisible(true);
            profileConducteurFields.setManaged(true);
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM conducteur_info WHERE utilisateur_id = ?")) {
                pstmt.setInt(1, currentUser.getId());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String cgPath = rs.getString("carte_gris_path");
                    String pPath = rs.getString("permis_path");
                    
                    displayImage(cgPath, profileCarteGrisView, profileCarteGrisLabel);
                    displayImage(pPath, profilePermisView, profilePermisLabel);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void displayImage(String path, ImageView imageView, Label errorLabel) {
        if (path != null && !path.isEmpty()) {
            File file = new File(path);
            if (file.exists()) {
                try {
                    String url = file.toURI().toURL().toString();
                    Image image = new Image(url, true); // load in background
                    imageView.setImage(image);
                    imageView.setVisible(true);
                    errorLabel.setVisible(false);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    showImageError(imageView, errorLabel);
                }
            } else {
                showImageError(imageView, errorLabel);
            }
        } else {
            showImageError(imageView, errorLabel);
        }
    }

    private void showImageError(ImageView imageView, Label errorLabel) {
        imageView.setVisible(false);
        errorLabel.setVisible(true);
    }

    @FXML
    void showHomeSection(ActionEvent event) {
        homeSection.setVisible(true);
        homeSection.setManaged(true);
        profileSection.setVisible(false);
        profileSection.setManaged(false);
        setActiveNav(homeNavBtn);
    }

    @FXML
    void showProfileSection(ActionEvent event) {
        homeSection.setVisible(false);
        homeSection.setManaged(false);
        profileSection.setVisible(true);
        profileSection.setManaged(true);
        setActiveNav(profileNavBtn);
    }

    @FXML
    void showTrajetSection(ActionEvent event) {
        // Optionnel : implémenter une section trajet
    }

    private void setActiveNav(Button activeBtn) {
        homeNavBtn.getStyleClass().remove("nav-item-active");
        profileNavBtn.getStyleClass().remove("nav-item-active");
        activeBtn.getStyleClass().add("nav-item-active");
    }

    @FXML
    void handleUpdateProfile(ActionEvent event) {
        String query = "UPDATE utilisateur SET nom = ?, prenom = ?, email = ?, telephone = ?, cin = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, profileNomField.getText().trim());
                pstmt.setString(2, profilePrenomField.getText().trim());
                pstmt.setString(3, profileEmailField.getText().trim());
                pstmt.setString(4, profilePhoneField.getText().trim());
                pstmt.setString(5, profileCinField.getText().trim());
                pstmt.setInt(6, currentUser.getId());
                pstmt.executeUpdate();
            }

            conn.commit();
            
            // Update current session user
            currentUser.setNom(profileNomField.getText().trim());
            currentUser.setPrenom(profilePrenomField.getText().trim());
            currentUser.setEmail(profileEmailField.getText().trim());
            currentUser.setNumeroTelephone(profilePhoneField.getText().trim());
            currentUser.setCin(profileCinField.getText().trim());
            
            welcomeLabel.setText("Bienvenue, " + currentUser.getPrenom() + " !");
            profileMessageLabel.setText("Profil mis à jour avec succès !");
            profileMessageLabel.setStyle("-fx-text-fill: #27ae60;");
            
        } catch (SQLException e) {
            e.printStackTrace();
            profileMessageLabel.setText("Erreur lors de la mise à jour.");
            profileMessageLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    @FXML
    void handleLogout(ActionEvent event) throws IOException {
        SessionManager.logout();
        HelloApplication.changeScene("login.fxml", "Fi Thnitek - Connexion");
    }
}
