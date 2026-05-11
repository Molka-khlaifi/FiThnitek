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

    @FXML private Button btnTrajets;
    @FXML private Button btnReservations;
    @FXML private Button btnVehicules;
    @FXML private Button btnMaintenance;
    @FXML private Button btnForum;
    @FXML private Button btnRevenue;
    @FXML private Button btnReclamations;
    @FXML private Button btnProfil;

    private Button[] allButtons;

    @FXML
    public void initialize() {

        if (SessionManager.isLoggedIn() && SessionManager.getCurrentUser() != null) {
            nomUserLabel.setText(
                    SessionManager.getCurrentUser().getNom() + " " +
                            SessionManager.getCurrentUser().getPrenom()
            );
        }

        allButtons = new Button[]{
                btnTrajets, btnReservations, btnVehicules,
                btnMaintenance, btnForum, btnRevenue,
                btnReclamations, btnProfil
        };

        setActive(btnRevenue);
        loadView("/Revenue.fxml");
    }

    @FXML void showTrajets()      { setActive(btnTrajets);      loadView("/Trajets.fxml"); }
    @FXML void showReservations() { setActive(btnReservations); loadView("/Reservations.fxml"); }
    @FXML void showVehicules()    { setActive(btnVehicules);    loadView("/Vehicules.fxml"); }
    @FXML void showMaintenance()  { setActive(btnMaintenance);  loadView("/Maintenance.fxml"); }
    @FXML void showForum()        { setActive(btnForum);        loadView("/ListeForum.fxml"); }
    @FXML void showRevenue()      { setActive(btnRevenue);      loadView("/Revenue.fxml"); }
    @FXML void showReclamations() { setActive(btnReclamations); loadView("/Reclamations.fxml"); }
    @FXML void showProfil()       { setActive(btnProfil);       loadView("/Profile.fxml"); }

    // ───────── STYLE ACTIVE BUTTON ─────────
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
                            "-fx-alignment: CENTER_LEFT;" +
                            "-fx-padding: 0 0 0 20;" +
                            "-fx-cursor: hand;"
            );
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