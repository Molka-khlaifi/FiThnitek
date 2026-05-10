package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

public class ConducteurHomePageController {

    @FXML private TabPane conducteurTabs;

    @FXML private AnchorPane trajetTab;
    @FXML private AnchorPane reservationTab;
    @FXML private AnchorPane vehiculeTab;
    @FXML private AnchorPane maintenanceTab;
    @FXML private AnchorPane forumTab;
    @FXML private AnchorPane revenueTab;
    @FXML private AnchorPane reclamationTab;
    @FXML private AnchorPane profileTab;

    // 🔥 TAB REVENUE (par défaut)
    @FXML private Tab revenueTabButton;

    @FXML
    public void initialize() {

        loadView("/Trajets.fxml", trajetTab);
        loadView("/Reservations.fxml", reservationTab);
        loadView("/Vehicules.fxml", vehiculeTab);
        loadView("/Maintenance.fxml", maintenanceTab);
        loadView("/ListeForum.fxml", forumTab);
        loadView("/Revenue.fxml", revenueTab);
        loadView("/Reclamations.fxml", reclamationTab);
        loadView("/Profile.fxml", profileTab);

        // 🔥 OUVRIR REVENUE PAR DÉFAUT
        conducteurTabs.getSelectionModel().select(revenueTabButton);
    }

    // 🔥 MÉTHODE GÉNÉRIQUE
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