package controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller de la page d'accueil Passager.
 *
 * Spécificités :
 *  - Tab 1 = Trajets & Réservations (chargée en premier)
 *  - Bouton "Véhicules disponibles" dans la toolbar du tab Trajets
 *    → ouvre GestionVehicules.fxml dans une fenêtre modale
 *  - Notifications pop-up au démarrage (toasts animés)
 *  - Panneau notifications latéral slide-in
 */
public class PassagerHomePageController implements Initializable {

    /* ── FXML ── */
    @FXML private TabPane mainTabPane;
    @FXML private Tab     tabTrajets;

    @FXML private Label lblPassagerNom;
    @FXML private Label lblNotePassager;
    @FXML private Label lblBienvenue;
    @FXML private Label lblProchainTrajet;

    // Quick stats
    @FXML private Label kpiTrajetsEffectues;
    @FXML private Label kpiDepensesMois;
    @FXML private Label kpiReservationsActives;

    // Search
    @FXML private TextField tfRechercheTrajet;

    // Notifications
    @FXML private Button btnNotifications;
    @FXML private Label  notifBadge;
    @FXML private VBox   notificationPanel;
    @FXML private VBox   notifListContainer;

    // Bouton véhicules (dans la toolbar du tab trajets)
    @FXML private Button btnVoirVehicules;

    // Status bar
    @FXML private Label lblStatusPassager;
    @FXML private Label lblDateTime;

    private Timeline clockTimeline;
    private boolean  panelOpen = false;

    /* ════════════════════════════════════════════════════════════════════ */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Onglet Trajets & Réservations en premier
        mainTabPane.getSelectionModel().select(tabTrajets);

        // 2. Infos passager
        loadPassagerInfo();

        // 3. Quick stats
        loadQuickStats();

        // 4. Horloge
        startClock();

        // 5. Pop-up notifications démarrage (délai 800ms)
        PauseTransition startupDelay = new PauseTransition(Duration.millis(800));
        startupDelay.setOnFinished(e -> showStartupNotifications());
        startupDelay.play();
    }

    /* ── Infos passager ─────────────────────────────────────── */
    private void loadPassagerInfo() {
        // TODO : SessionManager.getCurrentUser()
        String nom = "Passager"; // userService.getCurrentUser().getNom()
        lblPassagerNom.setText(nom);
        lblBienvenue.setText("Bonjour, " + nom + " 👋");
        lblNotePassager.setText("5.0");

        // Prochain trajet
        // Reservation prochaine = reservationService.getProchaine(userId)
        lblProchainTrajet.setText("Votre prochain trajet: —");
    }

    /* ── Quick stats ────────────────────────────────────────── */
    private void loadQuickStats() {
        // TODO : appeler les services
        kpiTrajetsEffectues.setText("—");
        kpiDepensesMois.setText("— TND");
        kpiReservationsActives.setText("—");
    }

    /* ── Horloge ─────────────────────────────────────────────── */
    private void startClock() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss");
        clockTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e ->
                        lblDateTime.setText(LocalDateTime.now().format(fmt))
                )
        );
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
    }

    /* ── Notifications démarrage ─────────────────────────────── */
    private void showStartupNotifications() {
        // TODO : notifService.getUnread(passagerId)
        List<String> notifs = List.of(
                "🚗  Un conducteur a accepté votre réservation !"
        );

        if (notifs.isEmpty()) return;

        notifBadge.setText(String.valueOf(notifs.size()));
        notifBadge.setVisible(true);

        for (int i = 0; i < notifs.size(); i++) {
            final String msg = notifs.get(i);
            final int idx = i;
            PauseTransition d = new PauseTransition(Duration.millis(400 + idx * 700L));
            d.setOnFinished(e -> showToast(msg));
            d.play();
        }
    }

    private void showToast(String message) {
        // TODO : overlay toast animé (FadeIn 300ms → pause 3s → FadeOut 300ms)
        System.out.println("[Toast Passager] " + message);
    }

    /* ── Panneau notifications ───────────────────────────────── */
    @FXML
    private void toggleNotificationPanel() {
        if (panelOpen) closeNotificationPanel();
        else openNotificationPanel();
    }

    private void openNotificationPanel() {
        notificationPanel.setVisible(true);
        notificationPanel.setManaged(true);
        TranslateTransition tt = new TranslateTransition(Duration.millis(250), notificationPanel);
        tt.setFromX(340);
        tt.setToX(0);
        tt.play();
        panelOpen = true;
    }

    @FXML
    private void closeNotificationPanel() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(200), notificationPanel);
        tt.setFromX(0);
        tt.setToX(340);
        tt.setOnFinished(e -> {
            notificationPanel.setVisible(false);
            notificationPanel.setManaged(false);
        });
        tt.play();
        panelOpen = false;
    }

    @FXML
    private void markAllRead() {
        notifBadge.setVisible(false);
        notifListContainer.getChildren().clear();
        // TODO : notifService.markAllRead(passagerId)
    }

    /* ── Bouton Véhicules (dans tab Trajets) ─────────────────── */
    /**
     * Ouvre GestionVehicules.fxml dans une fenêtre modale.
     * Accessible via le bouton "Véhicules disponibles" dans la toolbar du tab Trajets.
     */
    @FXML
    private void ouvrirVehiculesDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/passager/GestionVehicules.fxml")
            );
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.setTitle("Véhicules disponibles");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root, 900, 600));
            dialog.setResizable(true);
            dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            // TODO : afficher une alerte utilisateur
        }
    }

    /* ── Recherche rapide trajet ─────────────────────────────── */
    @FXML
    private void rechercherTrajet() {
        String query = tfRechercheTrajet.getText().trim();
        if (query.isEmpty()) return;

        // Basculer vers le tab Trajets et transmettre la recherche
        mainTabPane.getSelectionModel().select(tabTrajets);

        // TODO : récupérer le controller TrajetsReservations via lookup
        // et appeler trajetsController.filtrer(query)
    }

    /* ── Logout ──────────────────────────────────────────────── */
    @FXML
    private void logout() {
        if (clockTimeline != null) clockTimeline.stop();
        // TODO : SceneManager.switchTo("Login.fxml");
    }
}