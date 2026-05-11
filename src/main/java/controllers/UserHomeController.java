package controllers;

import models.Role;
import models.UserEntry;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.Main;
import util.DBConnection;
import util.SessionManager;

import java.io.IOException;
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
    @FXML private TextField profileMarqueField;
    @FXML private TextField profileModeleField;
    @FXML private TextField profileImmatField;
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
    }

    private void setupConducteurHome() {
        earningsCard.setVisible(true);
        earningsCard.setManaged(true);

        try (Connection conn = DBConnection.getInstance().getConn();
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

            try (Connection conn = DBConnection.getInstance().getConn();
                 PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM conducteur_info WHERE utilisateur_id = ?")) {
                pstmt.setInt(1, currentUser.getId());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    profileMarqueField.setText(rs.getString("marque_voiture"));
                    profileModeleField.setText(rs.getString("modele_voiture"));
                    profileImmatField.setText(rs.getString("immatriculation"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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

        try (Connection conn = DBConnection.getInstance().getConn()) {
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

            if (currentUser.getRole() == Role.CONDUCTEUR) {
                String condQuery = "UPDATE conducteur_info SET marque_voiture = ?, modele_voiture = ?, immatriculation = ? WHERE utilisateur_id = ?";
                try (PreparedStatement cpstmt = conn.prepareStatement(condQuery)) {
                    cpstmt.setString(1, profileMarqueField.getText().trim());
                    cpstmt.setString(2, profileModeleField.getText().trim());
                    cpstmt.setString(3, profileImmatField.getText().trim());
                    cpstmt.setInt(4, currentUser.getId());
                    cpstmt.executeUpdate();
                }
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
        Main.changeScene("login.fxml", "Fi Thnitek - Connexion");
    }
}
