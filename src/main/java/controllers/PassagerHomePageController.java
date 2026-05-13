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

public class PassagerHomePageController {

    @FXML private Label nomUserLabel;

    @FXML private AnchorPane trajetsContent;
    @FXML private AnchorPane reservationsContent;
    @FXML private AnchorPane forumContent;
    @FXML private AnchorPane reclamationsContent;
    @FXML private AnchorPane profilContent;

    @FXML private TabPane mainTabPane;

    @FXML private Button btnTrajets;
    @FXML private Button btnReservations;
    @FXML private Button btnForum;
    @FXML private Button btnReclamations;
    @FXML private Button btnProfil;

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
                btnTrajets, btnReservations, btnForum,
                btnReclamations, btnProfil
        };

        // Enregistrer les conteneurs dans le NavigationManager
        NavigationManager.registerTabContainer("TRAJETS",      trajetsContent);
        NavigationManager.registerTabContainer("RESERVATIONS", reservationsContent);
        NavigationManager.registerTabContainer("FORUM",        forumContent);
        NavigationManager.registerTabContainer("RECLAMATIONS", reclamationsContent);
        NavigationManager.registerTabContainer("PROFIL",       profilContent);

        // ✅ Attendre que le nœud soit attaché à la scène pour récupérer le Stage
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

        // Charger toutes les vues dans leurs conteneurs respectifs
        chargerVueParDefaut("TRAJETS",      "/TrajetsPassager.fxml");
        chargerVueParDefaut("RESERVATIONS", "/Reservations.fxml");
        chargerVueParDefaut("FORUM",        "/ListeForum.fxml");
        chargerVueParDefaut("RECLAMATIONS", "/Reclamations.fxml");
        chargerVueParDefaut("PROFIL",       "/Profile.fxml");

        // Onglet actif par défaut : TRAJETS
        setActive(btnTrajets);
        selectTab("TRAJETS");
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
            case "TRAJETS":      return trajetsContent;
            case "RESERVATIONS": return reservationsContent;
            case "FORUM":        return forumContent;
            case "RECLAMATIONS": return reclamationsContent;
            case "PROFIL":       return profilContent;
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
    //  Actions boutons sidebar
    // ---------------------------------------------------------------

    @FXML void showTrajets()      { setActive(btnTrajets);      selectTab("TRAJETS");      }
    @FXML void showReservations() { setActive(btnReservations); selectTab("RESERVATIONS"); }
    @FXML void showForum()        { setActive(btnForum);        selectTab("FORUM");        }
    @FXML void showReclamations() { setActive(btnReclamations); selectTab("RECLAMATIONS"); }
    @FXML void showProfil()       { setActive(btnProfil);       selectTab("PROFIL");       }

    // ---------------------------------------------------------------
    //  Style bouton actif / inactif
    // ---------------------------------------------------------------

    private void setActive(Button btn) {
        for (Button b : allButtons) {
            if (b == null) continue;
            b.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: #B0B8D8;" +
                            "-fx-font-size: 13px;" +
                            "-fx-alignment: CENTER_LEFT;" +
                            "-fx-padding: 0 0 0 20;" +
                            "-fx-cursor: hand;"
            );
        }
        if (btn != null) {
            btn.setStyle(
                    "-fx-background-color: #3D3D8A;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-alignment: CENTER_LEFT;" +
                            "-fx-padding: 0 0 0 20;" +
                            "-fx-cursor: hand;"
            );
        }
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