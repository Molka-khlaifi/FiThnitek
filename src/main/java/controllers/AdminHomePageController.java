package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.SessionManager;
import util.NavigationManager;

import java.io.IOException;

public class AdminHomePageController {

    @FXML private Label nomUserLabel;

    // Conteneurs des onglets
    @FXML private AnchorPane profilContent;
    @FXML private AnchorPane usersContent;
    @FXML private AnchorPane vehiclesContent;
    @FXML private AnchorPane maintenanceContent;
    @FXML private AnchorPane tripsContent;
    @FXML private AnchorPane forumContent;
    @FXML private AnchorPane claimsContent;
    @FXML private AnchorPane revenueContent;
    @FXML private AnchorPane statsContent;

    @FXML private TabPane mainTabPane;

    @FXML
    public void initialize() {
        // Afficher le nom de l'utilisateur
        if (SessionManager.isLoggedIn() && SessionManager.getCurrentUser() != null) {
            nomUserLabel.setText(
                    SessionManager.getCurrentUser().getNom() + " " +
                            SessionManager.getCurrentUser().getPrenom()
            );
        }

        // Enregistrer les conteneurs dans le NavigationManager
        NavigationManager.registerTabContainer("PROFIL", profilContent);
        NavigationManager.registerTabContainer("USERS", usersContent);
        NavigationManager.registerTabContainer("VEHICLES", vehiclesContent);
        NavigationManager.registerTabContainer("TRIPS", tripsContent);
        NavigationManager.registerTabContainer("FORUM", forumContent);
        NavigationManager.registerTabContainer("CLAIMS", claimsContent);
        NavigationManager.registerTabContainer("REVENUE", revenueContent);
        NavigationManager.registerTabContainer("STATS", statsContent);

        // Attendre que le nœud soit attaché à la scène
        nomUserLabel.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                if (newScene.getWindow() instanceof Stage) {
                    NavigationManager.setPrimaryStage((Stage) newScene.getWindow());
                }
                newScene.windowProperty().addListener((obs2, oldWin, newWin) -> {
                    if (newWin instanceof Stage) {
                        NavigationManager.setPrimaryStage((Stage) newWin);
                    }
                });
            }
        });

        // Charger les vues par défaut
        chargerVueParDefaut("USERS", "/AdminUsers.fxml");
        chargerVueParDefaut("VEHICLES", "/AdminVehicles.fxml");
        chargerVueParDefaut("TRIPS", "/AdminTrips.fxml");
        chargerVueParDefaut("FORUM", "/ForumAdmin.fxml");
        chargerVueParDefaut("CLAIMS", "/AdminClaims.fxml");
        chargerVueParDefaut("REVENUE", "/AdminRevenue.fxml");
        chargerVueParDefaut("STATS", "/AdminStats.fxml");
        chargerVueParDefaut("PROFIL", "/AdminProfile.fxml");

        // Onglet actif par défaut
        selectTab("USERS");
    }

    private void chargerVueParDefaut(String tabName, String fxmlPath) {
        if (getClass().getResource(fxmlPath) != null) {
            NavigationManager.navigateInTab(tabName, fxmlPath);
        } else {
            System.err.println("⚠️ Fichier FXML non trouvé: " + fxmlPath);
            AnchorPane container = getContainerByName(tabName);
            if (container != null) {
                Label placeholder = new Label("⚙️  " + tabName + "\nContenu en cours de développement");
                placeholder.setStyle("-fx-font-size: 15px; -fx-text-fill: #888; -fx-alignment: CENTER;");
                AnchorPane.setTopAnchor(placeholder, 0.0);
                AnchorPane.setBottomAnchor(placeholder, 0.0);
                AnchorPane.setLeftAnchor(placeholder, 0.0);
                AnchorPane.setRightAnchor(placeholder, 0.0);
                container.getChildren().setAll(placeholder);
            }
        }
    }

    private AnchorPane getContainerByName(String tabName) {
        switch (tabName) {
            case "PROFIL": return profilContent;
            case "USERS": return usersContent;
            case "VEHICLES": return vehiclesContent;
            case "MAINTENANCE": return maintenanceContent;
            case "TRIPS": return tripsContent;
            case "FORUM": return forumContent;
            case "CLAIMS": return claimsContent;
            case "REVENUE": return revenueContent;
            case "STATS": return statsContent;
            default: return null;
        }
    }

    private void selectTab(String tabName) {
        if (mainTabPane == null) return;
        for (Tab tab : mainTabPane.getTabs()) {
            String text = tab.getText() != null ? tab.getText().toUpperCase() : "";
            if (text.contains(tabName)) {
                mainTabPane.getSelectionModel().select(tab);
                break;
            }
        }
    }

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
                    Stage stage = (Stage) nomUserLabel.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("FiThnitek — Connexion");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}