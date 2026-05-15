package controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class PassagerHomePageController implements Initializable {

    @FXML private TabPane mainTabPane;
    @FXML private Tab tabTrajets;

    @FXML private Label lblPassagerNom;
    @FXML private Label lblNotePassager;
    @FXML private Label lblBienvenue;
    @FXML private Label lblProchainTrajet;

    @FXML private Label kpiTrajetsEffectues;
    @FXML private Label kpiDepensesMois;
    @FXML private Label kpiReservationsActives;

    @FXML private TextField tfRechercheTrajet;

    @FXML private Button btnNotifications;
    @FXML private Label notifBadge;
    @FXML private VBox notificationPanel;
    @FXML private VBox notifListContainer;

    @FXML private Label lblStatusPassager;
    @FXML private Label lblDateTime;

    private Timeline clockTimeline;
    private boolean panelOpen = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainTabPane.getSelectionModel().select(tabTrajets);
        loadPassagerInfo();
        loadQuickStats();
        startClock();

        PauseTransition startupDelay = new PauseTransition(Duration.millis(800));
        startupDelay.setOnFinished(e -> showStartupNotifications());
        startupDelay.play();
    }

    private void loadPassagerInfo() {
        String nom = "Passager";
        lblPassagerNom.setText(nom);
        lblBienvenue.setText("Bonjour, " + nom);
        lblNotePassager.setText("5.0");
        lblProchainTrajet.setText("Votre prochain trajet: -");
    }

    private void loadQuickStats() {
        kpiTrajetsEffectues.setText("-");
        kpiDepensesMois.setText("- TND");
        kpiReservationsActives.setText("-");
    }

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

    private void showStartupNotifications() {
        List<String> notifs = List.of("Un conducteur a accepte votre reservation.");

        if (notifs.isEmpty()) {
            return;
        }

        notifBadge.setText(String.valueOf(notifs.size()));
        notifBadge.setVisible(true);

        for (int i = 0; i < notifs.size(); i++) {
            final String msg = notifs.get(i);
            final int idx = i;
            PauseTransition delay = new PauseTransition(Duration.millis(400 + idx * 700L));
            delay.setOnFinished(e -> showToast(msg));
            delay.play();
        }
    }

    private void showToast(String message) {
        System.out.println("[Toast Passager] " + message);
    }

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

        TranslateTransition transition = new TranslateTransition(Duration.millis(250), notificationPanel);
        transition.setFromX(340);
        transition.setToX(0);
        transition.play();

        panelOpen = true;
    }

    @FXML
    private void closeNotificationPanel() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(200), notificationPanel);
        transition.setFromX(0);
        transition.setToX(340);
        transition.setOnFinished(e -> {
            notificationPanel.setVisible(false);
            notificationPanel.setManaged(false);
        });
        transition.play();

        panelOpen = false;
    }

    @FXML
    private void markAllRead() {
        notifBadge.setVisible(false);
        notifListContainer.getChildren().clear();
    }

    @FXML
    private void rechercherTrajet() {
        String query = tfRechercheTrajet.getText().trim();
        if (query.isEmpty()) {
            return;
        }

        mainTabPane.getSelectionModel().select(tabTrajets);
    }

    @FXML
    private void logout() {
        if (clockTimeline != null) {
            clockTimeline.stop();
        }
    }
}
