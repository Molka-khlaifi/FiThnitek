package controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller de la page d'accueil Conducteur.
 * - Tab 1 = Revenus chargée en premier
 * - Pop-up notifications au démarrage
 * - Toggle disponibilité
 * - KPIs du jour mis à jour automatiquement
 */
public class ConducteurHomePageController {

    /* ── FXML ── */
    @FXML private TabPane mainTabPane;
    @FXML private Tab     tabRevenus;

    @FXML private Label lblConducteurNom;
    @FXML private Label lblNote;

    // KPIs
    @FXML private Label kpiRevenuJour;
    @FXML private Label kpiTrajetsJour;
    @FXML private Label kpiKmJour;
    @FXML private Label kpiReservationsEnAttente;

    // Disponibilité
    @FXML private Circle       circleDisponibilite;
    @FXML private Label        lblDisponibilite;
    @FXML private ToggleButton toggleDispo;

    // Notifications
    @FXML private Button btnNotifications;
    @FXML private Label  notifBadge;
    @FXML private VBox   notificationPanel;
    @FXML private VBox   notifListContainer;

    // Status bar
    @FXML private Label lblStatusConduc;
    @FXML private Label lblDateTime;

    private Timeline clockTimeline;
    private boolean  panelOpen = false;

    /* ════════════════════════════════════════════════════════════════════ */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Sélectionner tab Revenus en premier
        mainTabPane.getSelectionModel().select(tabRevenus);

        // 2. Infos conducteur
        loadConducteurInfo();

        // 3. KPIs du jour
        loadKPIs();

        // 4. Horloge
        startClock();

        // 5. Notifications au démarrage (après 800ms pour laisser la scène se charger)
        PauseTransition startupDelay = new PauseTransition(Duration.millis(800));
        startupDelay.setOnFinished(e -> showStartupNotifications());
        startupDelay.play();
    }

    /* ── Infos conducteur ───────────────────────────────────── */
    private void loadConducteurInfo() {
        // TODO : SessionManager.getCurrentUser()
        lblConducteurNom.setText("Conducteur");
        lblNote.setText("4.8");
    }

    /* ── KPIs ───────────────────────────────────────────────── */
    private void loadKPIs() {
        // TODO : services
        kpiRevenuJour.setText("— TND");
        kpiTrajetsJour.setText("—");
        kpiKmJour.setText("— km");
        kpiReservationsEnAttente.setText("—");
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

    /* ── Notifications au démarrage ─────────────────────────── */
    /**
     * Affiche les notifications non lues sous forme de toasts empilés
     * qui apparaissent depuis le bas de l'écran au démarrage.
     */
    private void showStartupNotifications() {
        // TODO : récupérer depuis notifService.getUnread(conducteurId)
        List<String> notifs = List.of(
                "🆕  Nouvelle réservation de Ahmed B. pour Tunis → Sousse",
                "✅  Votre trajet d'hier a été confirmé"
        );

        if (notifs.isEmpty()) return;

        // Mise à jour badge
        notifBadge.setText(String.valueOf(notifs.size()));
        notifBadge.setVisible(true);

        // Afficher chaque notif comme un toast avec délai
        for (int i = 0; i < notifs.size(); i++) {
            final String message = notifs.get(i);
            final int index = i;
            PauseTransition delay = new PauseTransition(Duration.millis(300 + index * 600L));
            delay.setOnFinished(e -> showToast(message));
            delay.play();
        }
    }

    /**
     * Crée et anime un toast de notification en bas à droite de l'écran.
     */
    private void showToast(String message) {
        // TODO : créer un Label/VBox stylisé, l'ajouter en overlay StackPane,
        //        puis FadeIn + PauseTransition(3s) + FadeOut
        System.out.println("[Toast] " + message); // placeholder
    }

    /* ── Panel Notifications ─────────────────────────────────── */
    @FXML
    private void toggleNotificationPanel() {
        if (panelOpen) {
            closeNotificationPanel();
        } else {
            openNotificationPanel();
        }
    }

    private void openNotificationPanel() {
        notificationPanel.setVisible(true);
        notificationPanel.setManaged(true);
        // TODO : charger les notifs dans notifListContainer
        // Animation slide-in depuis la droite
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
        // TODO : notifService.markAllRead(conducteurId)
        notifBadge.setVisible(false);
        notifListContainer.getChildren().clear();
    }

    /* ── Disponibilité ───────────────────────────────────────── */
    @FXML
    private void toggleDisponibilite() {
        boolean disponible = toggleDispo.isSelected();
        if (disponible) {
            lblDisponibilite.setText("Indisponible");
            circleDisponibilite.getStyleClass().setAll("dot-indisponible");
            lblStatusConduc.setText("Indisponible");
        } else {
            lblDisponibilite.setText("Disponible");
            circleDisponibilite.getStyleClass().setAll("dot-disponible");
            lblStatusConduc.setText("Disponible");
        }
        // TODO : mettre à jour en base via conducteurService.setDisponibilite(id, disponible)
    }

    /* ── Logout ──────────────────────────────────────────────── */
    @FXML
    private void logout() {
        if (clockTimeline != null) clockTimeline.stop();
        // TODO : SceneManager.switchTo("Login.fxml");
    }
}