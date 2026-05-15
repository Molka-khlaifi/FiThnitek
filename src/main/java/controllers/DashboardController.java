package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private BorderPane mainPane;

    @FXML
    private Label lblPage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showTrajets();
    }

    @FXML
    private void showTrajets() {
        loadPage("/trajet.fxml", "Gestion des Trajets");
    }

    @FXML
    private void showAjouterTrajet() {
        loadPage("/ajouter_trajet.fxml", "Nouveau trajet");
    }

    @FXML
    private void showReservations() {
        loadPage("/reservationTrajet.fxml", "Gestion des Réservations");
    }

    @FXML
    private void showMap() {
        loadPage("/MapTrajet.fxml", "Carte des trajets");
    }

    @FXML
    private void showStats() {
        loadPage("/StatistiquesTrajet.fxml", "Statistiques trajets");
    }

    private void loadPage(String fxmlPath, String title) {
        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            showError("Vue introuvable : " + fxmlPath);
            return;
        }

        try {
            Parent page = FXMLLoader.load(resource);
            mainPane.setCenter(page);
            if (lblPage != null) {
                lblPage.setText(title);
            }
        } catch (IOException e) {
            showError("Impossible de charger " + fxmlPath + "\n" + e.getMessage());
        }
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
}
