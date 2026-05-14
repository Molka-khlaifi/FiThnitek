package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.SessionManager;
import util.NavigationManager;

import java.io.IOException;

public class ConducteurHomePageController {

    @FXML private Label nomUserLabel;

    @FXML private AnchorPane profilContent;
    @FXML private AnchorPane trajetsContent;
    @FXML private AnchorPane vehiculesContent;
    @FXML private AnchorPane maintenanceContent;
    @FXML private AnchorPane forumContent;
    @FXML private AnchorPane revenueContent;
    @FXML private AnchorPane reclamationsContent;

    @FXML private TabPane mainTabPane;

    @FXML private Button btnProfil;
    @FXML private Button btnTrajets;
    @FXML private Button btnVehicules;
    @FXML private Button btnMaintenance;
    @FXML private Button btnForum;
    @FXML private Button btnRevenue;
    @FXML private Button btnReclamations;
    @FXML private VBox sidebarVBox;

    private Button[] allButtons;

    @FXML
    public void initialize() {


        // Afficher le nom de l'utilisateur
        if (SessionManager.isLoggedIn() && SessionManager.getCurrentUser() != null) {
            nomUserLabel.setText(
                    SessionManager.getCurrentUser().getNom() + " " +
                            SessionManager.getCurrentUser().getPrenom()
            );
        }

        // Initialiser les boutons
        allButtons = new Button[]{
                btnProfil, btnTrajets, btnVehicules,
                btnMaintenance, btnForum, btnRevenue, btnReclamations
        };

        // Enregistrer les conteneurs dans le NavigationManager
        NavigationManager.registerTabContainer("PROFIL",        profilContent);
        NavigationManager.registerTabContainer("TRAJETS",       trajetsContent);
        NavigationManager.registerTabContainer("VEHICULES",     vehiculesContent);
        NavigationManager.registerTabContainer("MAINTENANCE",   maintenanceContent);
        NavigationManager.registerTabContainer("FORUM",         forumContent);
        NavigationManager.registerTabContainer("REVENU",        revenueContent);
        NavigationManager.registerTabContainer("RECLAMATIONS",  reclamationsContent);

        // ✅ Attendre que le nœud soit attaché à la scène pour récupérer le Stage
        nomUserLabel.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                // Si la fenêtre est déjà disponible
                if (newScene.getWindow() instanceof Stage) {
                    NavigationManager.setPrimaryStage((Stage) newScene.getWindow());
                }
                // Sinon attendre qu'elle le soit
                newScene.windowProperty().addListener((obs2, oldWin, newWin) -> {
                    if (newWin instanceof Stage) {
                        NavigationManager.setPrimaryStage((Stage) newWin);
                    }
                });
            }
        });

        // Charger toutes les vues dans leurs conteneurs respectifs
        chargerVueParDefaut("PROFIL",       "/Profile.fxml");
        chargerVueParDefaut("TRAJETS",      "/dashboard.fxml");
        chargerVueParDefaut("VEHICULES",    "/Vehicules.fxml");
        chargerVueParDefaut("MAINTENANCE",  "/Maintenance.fxml");
        chargerVueParDefaut("FORUM",        "/ListeForum.fxml");
        chargerVueParDefaut("REVENU",       "/Revenue.fxml");
        chargerVueParDefaut("RECLAMATIONS", "/Reclamations.fxml");

        // Onglet actif par défaut : REVENU
        selectTab("REVENU");
    }

    // ---------------------------------------------------------------
    //  Chargement d'une vue dans son conteneur
    // ---------------------------------------------------------------

    private void chargerVueParDefaut(String tabName, String fxmlPath) {
        if (getClass().getResource(fxmlPath) != null) {
            NavigationManager.navigateInTab(tabName, fxmlPath);
        } else {
            System.err.println("⚠️ Fichier FXML non trouvé: " + fxmlPath);
            // Afficher un placeholder dans le conteneur
            AnchorPane container = getContainerByName(tabName);
            if (container != null) {
                Label placeholder = new Label("⚙️  " + tabName + "\nContenu en cours de développement");
                placeholder.setStyle(
                        "-fx-font-size: 15px;" +
                                "-fx-text-fill: #888;" +
                                "-fx-text-alignment: center;" +
                                "-fx-alignment: CENTER;"
                );
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
            case "PROFIL":       return profilContent;
            case "TRAJETS ET RESERVATIONS":      return trajetsContent;
            case "VEHICULES":    return vehiculesContent;
            case "FORUM":        return forumContent;
            case "REVENU":       return revenueContent;
            case "RECLAMATIONS": return reclamationsContent;
            default:             return null;
        }
    }

    // ---------------------------------------------------------------
    //  Sélection d'un onglet dans le TabPane
    // ---------------------------------------------------------------

    private void selectTab(String tabName) {
        if (mainTabPane == null) {
            System.err.println("❌ mainTabPane est null");
            return;
        }
        for (Tab tab : mainTabPane.getTabs()) {
            String id   = tab.getId()   != null ? tab.getId().toUpperCase()   : "";
            String text = tab.getText() != null ? tab.getText().toUpperCase() : "";
            if (id.contains(tabName) || text.contains(tabName)) {
                mainTabPane.getSelectionModel().select(tab);
                return;
            }
        }
        System.err.println("⚠️ Onglet introuvable: " + tabName);
    }


    // ---------------------------------------------------------------
    //  Déconnexion
    // ---------------------------------------------------------------

    @FXML
    void deconnecterAction() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Voulez-vous vraiment vous déconnecter ?");
        alert.setContentText("Vous serez redirigé vers la page de connexion.");

        ButtonType oui = new ButtonType("Oui",     ButtonBar.ButtonData.OK_DONE);
        ButtonType non = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(oui, non);

        alert.showAndWait().ifPresent(response -> {
            if (response == oui) {
                SessionManager.logout();
                try {
                    Parent root  = FXMLLoader.load(getClass().getResource("/Login.fxml"));
                    Stage  stage = (Stage) nomUserLabel.getScene().getWindow();
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