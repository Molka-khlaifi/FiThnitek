package controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private BorderPane mainPane;
    @FXML private VBox navMenu;
    @FXML private Label lblPage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadPage("trajet", "Trajets");
    }

    @FXML
    private void showTrajets() { loadPage("trajet", "Gestion des Trajets"); }

    @FXML
    private void showReservations() { loadPage("reservation", "Réservations"); }

    @FXML
    private void showMap() { loadPage("map", "Carte des Trajets"); }

    @FXML
    private void showStats() { loadPage("statistiques", "Statistiques & Analyses"); }

    @FXML
    private void showAjouterTrajet() { loadPage("ajouter_trajet", "Nouveau Trajet"); }

    private void loadPage(String fxml, String title) {
        try {
            Node page = FXMLLoader.load(getClass().getResource("/" + fxml + ".fxml"));
            FadeTransition ft = new FadeTransition(Duration.millis(300), page);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            mainPane.setCenter(page);
            ft.play();
            if (lblPage != null) lblPage.setText(title);
        } catch (IOException e) {
            System.err.println("Cannot load page: " + fxml + ".fxml → " + e.getMessage());
        }
    }
}