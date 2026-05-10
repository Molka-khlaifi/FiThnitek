package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utils.SessionManager;

import java.io.IOException;

public class PassagerHomePageController {

    @FXML private TabPane passagerTabs;
    @FXML private Tab trajetTabButton;
    @FXML private Label nomUserLabel;

    @FXML private AnchorPane trajetTab;
    @FXML private AnchorPane reservationTab;
    @FXML private AnchorPane forumTab;
    @FXML private AnchorPane reclamationTab;
    @FXML private AnchorPane profileTab;

    @FXML
    public void initialize() {
        // Afficher le nom de l'utilisateur connecté
        if (SessionManager.isLoggedIn()) {
            nomUserLabel.setText(
                    SessionManager.getCurrentUser().getNom() + " " +
                            SessionManager.getCurrentUser().getPrenom()
            );
        }

        loadView("/Trajets.fxml", trajetTab);
        loadView("/Reservations.fxml", reservationTab);
        loadView("/ListeForum.fxml", forumTab);
        loadView("/Reclamations.fxml", reclamationTab);
        loadView("/Profile.fxml", profileTab);

        passagerTabs.getSelectionModel().select(trajetTabButton);
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
                    Stage stage = (Stage) passagerTabs.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Fi Thnitek — Connexion");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadView(String fxmlPath, AnchorPane container) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            container.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
        } catch (Exception e) {
            System.out.println("Erreur chargement : " + fxmlPath);
            e.printStackTrace();
        }
    }
}