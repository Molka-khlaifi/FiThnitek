package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.SessionManager;

import java.io.IOException;

public class AdminHomePageController {

    @FXML private AnchorPane mainContainer;
    @FXML private Label nomUserLabel;

    @FXML private Button btnUsers;
    @FXML private Button btnVehicles;
    @FXML private Button btnMaintenance;
    @FXML private Button btnTrips;
    @FXML private Button btnForum;
    @FXML private Button btnClaims;
    @FXML private Button btnRevenue;
    @FXML private Button btnStats;

    private Button[] allButtons;

    @FXML
    public void initialize() {
        if (SessionManager.isLoggedIn()) {
            nomUserLabel.setText(
                    SessionManager.getCurrentUser().getNom() + " " +
                            SessionManager.getCurrentUser().getPrenom()
            );
        }
        allButtons = new Button[]{btnUsers, btnVehicles, btnMaintenance,
                btnTrips, btnForum, btnClaims, btnRevenue, btnStats};
        // Vue par défaut : Réclamations (index 5)
        setActive(btnClaims);
        loadView("/Reclamations.fxml");
    }

    @FXML void showUsers()       { setActive(btnUsers);       loadView("/Users.fxml"); }
    @FXML void showVehicles()    { setActive(btnVehicles);    loadView("/Vehicles.fxml"); }
    @FXML void showMaintenance() { setActive(btnMaintenance); loadView("/Maintenance.fxml"); }
    @FXML void showTrips()       { setActive(btnTrips);       loadView("/dashboardTrajet.fxml"); }
    @FXML void showForum()       { setActive(btnForum);       loadView("/AdminForumView.fxml"); }
    @FXML void showClaims()      { setActive(btnClaims);      loadView("/Reclamations.fxml"); }
    @FXML void showRevenue()     { setActive(btnRevenue);     loadView("/Revenue.fxml"); }
    @FXML void showStats()       { setActive(btnStats);       loadView("/Stats.fxml"); }

    private void setActive(Button btn) {
        for (Button b : allButtons) {
            b.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: #B0B8D8;" +
                            "-fx-font-size: 13px;" +
                            "-fx-alignment: CENTER_LEFT;" +
                            "-fx-padding: 0 0 0 20;" +
                            "-fx-background-radius: 0;" +
                            "-fx-cursor: hand;"
            );
        }
        btn.setStyle(
                "-fx-background-color: #3D3D8A;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-alignment: CENTER_LEFT;" +
                        "-fx-padding: 0 0 0 20;" +
                        "-fx-background-radius: 0;" +
                        "-fx-cursor: hand;"
        );
    }

    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainContainer.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
        } catch (Exception e) {
            System.out.println("Erreur chargement : " + fxmlPath);
            e.printStackTrace();
        }
    }

    @FXML
    void deconnecterAction() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Voulez-vous vraiment vous déconnecter ?");
        alert.setContentText("Vous serez redirigé vers la page de connexion.");
        alert.getDialogPane().setStyle("-fx-background-color: #F5F6FA; -fx-font-size: 13px;");
        ButtonType btnOui = new ButtonType("Oui, déconnecter", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNon = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnOui, btnNon);
        alert.showAndWait().ifPresent(response -> {
            if (response == btnOui) {
                SessionManager.logout();
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
                    Stage stage = (Stage) mainContainer.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Fi Thnitek — Connexion");
                    stage.show();
                } catch (IOException e) { e.printStackTrace(); }
            }
        });
    }
}