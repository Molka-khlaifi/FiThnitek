package controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller de la page d'accueil Admin.
 * - Charge la tab Réclamations en premier (tabReclamations sélectionnée par défaut)
 * - Met à jour les stats en temps réel
 * - Gère l'horloge dans la status bar
 */
public class AdminHomePageController implements Initializable {

    /* ── FXML Injections ── */
    @FXML private TabPane mainTabPane;
    @FXML private Tab     tabReclamations;

    @FXML private Label lblAdminName;
    @FXML private Circle avatarCircle;

    // Stats bar
    @FXML private Label lblTotalUsers;
    @FXML private Label lblTotalVehicules;
    @FXML private Label lblTotalTrajets;
    @FXML private Label lblReclamationsEnAttente;
    @FXML private Label lblRevenuTotal;

    // Notifications
    @FXML private Button btnNotifications;
    @FXML private Label  notifBadge;

    // Status bar
    @FXML private Label lblStatus;
    @FXML private Label lblDateTime;

    /* ── Timeline horloge ── */
    private Timeline clockTimeline;

    /* ════════════════════════════════════════════════════════════════════ */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Sélectionner la première tab (Réclamations)
        mainTabPane.getSelectionModel().select(tabReclamations);

        // 2. Charger les infos de l'admin connecté
        loadAdminInfo();

        // 3. Charger les statistiques globales
        loadStats();

        // 4. Lancer l'horloge
        startClock();

        // 5. Charger les notifications non lues
        loadNotifBadge();
    }

    /* ── Chargement infos admin ─────────────────────────────── */
    private void loadAdminInfo() {
        // TODO : récupérer depuis la session / service utilisateur
        // SessionManager.getCurrentUser()
        lblAdminName.setText("Admin");
    }

    /* ── Stats ──────────────────────────────────────────────── */
    private void loadStats() {
        // TODO : appeler les services correspondants
        // lblTotalUsers.setText(String.valueOf(userService.count()));
        // lblTotalVehicules.setText(String.valueOf(vehiculeService.count()));
        // lblTotalTrajets.setText(String.valueOf(trajetService.count()));
        // lblReclamationsEnAttente.setText(String.valueOf(reclamationService.countEnAttente()));
        // lblRevenuTotal.setText(revenueService.getTotalFormatted() + " TND");

        // Valeurs placeholder
        lblTotalUsers.setText("—");
        lblTotalVehicules.setText("—");
        lblTotalTrajets.setText("—");
        lblReclamationsEnAttente.setText("—");
        lblRevenuTotal.setText("—");
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

    /* ── Notifications ───────────────────────────────────────── */
    private void loadNotifBadge() {
        // TODO : compter les notifications non lues
        int count = 0; // notifService.countUnread()
        if (count > 0) {
            notifBadge.setText(String.valueOf(count));
            notifBadge.setVisible(true);
        } else {
            notifBadge.setVisible(false);
        }
    }

    @FXML
    private void openNotifications() {
        // TODO : ouvrir un dialog ou panneau latéral de notifications
    }

    /* ── Actions ─────────────────────────────────────────────── */
    @FXML
    private void logout() {
        if (clockTimeline != null) clockTimeline.stop();
        // TODO : fermer cette scène, ouvrir LoginView.fxml
        // SceneManager.switchTo("Login.fxml");
    }
}