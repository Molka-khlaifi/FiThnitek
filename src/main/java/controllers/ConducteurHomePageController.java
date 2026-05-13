package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.SessionManager;

import java.io.IOException;

public class ConducteurHomePageController {

    @FXML private AnchorPane mainContainer;
    @FXML private Label nomUserLabel;
    @FXML private TabPane tabPane;

    @FXML
    public void initialize() {

        if (SessionManager.isLoggedIn() && SessionManager.getCurrentUser() != null) {
            nomUserLabel.setText(
                    SessionManager.getCurrentUser().getNom() + " " +
                            SessionManager.getCurrentUser().getPrenom()
            );
        }

        // Ajouter un listener pour charger les vues quand un onglet est sélectionné
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                loadViewForTab(newTab);
            }
        });

        // Sélectionner l'onglet Revenue par défaut
        selectTab("Revenue");
    }

    private void selectTab(String tabText) {
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(tabText)) {
                tabPane.getSelectionModel().select(tab);
                loadViewForTab(tab);
                break;
            }
        }
    }

    private void loadViewForTab(Tab tab) {
        String fxmlPath = null;

        switch (tab.getText()) {
            case "Trajets":
                fxmlPath = "/Trajets.fxml";
                break;
            case "Réservations":
                fxmlPath = "/Reservations.fxml";
                break;
            case "Véhicules":
                fxmlPath = "/Vehicules.fxml";
                break;
            case "Maintenance":
                fxmlPath = "/Maintenance.fxml";
                break;
            case "Forum":
                fxmlPath = "/ListeForum.fxml";
                break;
            case "Revenue":
                fxmlPath = "/Revenue.fxml";
                break;
            case "Réclamations":
                fxmlPath = "/Reclamations.fxml";
                break;
            case "Profil":
                fxmlPath = "/Profile.fxml";
                break;
        }

        if (fxmlPath != null) {
            loadView(fxmlPath);
        }
    }

    // ───────── LOAD VIEW SAFE ─────────
    private void loadView(String fxmlPath) {
        try {
            if (mainContainer == null) {
                System.err.println("❌ mainContainer NULL");
                return;
            }

            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));

            if (view == null) {
                System.err.println("❌ FXML introuvable : " + fxmlPath);
                return;
            }

            mainContainer.getChildren().setAll(view);

            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

        } catch (Exception e) {
            System.err.println("❌ Erreur chargement : " + fxmlPath);
            e.printStackTrace();
        }
    }

    // ───────── LOGOUT ─────────
    @FXML
    void deconnecterAction() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Voulez-vous vraiment vous déconnecter ?");
        alert.setContentText("Vous serez redirigé vers la page de connexion.");

        ButtonType oui = new ButtonType("Oui", ButtonBar.ButtonData.OK_DONE);
        ButtonType non = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(oui, non);

        alert.showAndWait().ifPresent(response -> {
            if (response == oui) {
                SessionManager.logout();

                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
                    Stage stage = (Stage) mainContainer.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Fi Thnitek — Connexion");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}