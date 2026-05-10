package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

public class PassagerHomePageController {

    @FXML private TabPane passagerTabs;

    @FXML private AnchorPane trajetTab;
    @FXML private AnchorPane reservationTab;
    @FXML private AnchorPane forumTab;
    @FXML private AnchorPane reclamationTab;
    @FXML private AnchorPane profileTab;

    // 🔥 TAB PAR DÉFAUT
    @FXML private Tab trajetTabButton;

    @FXML
    public void initialize() {

        loadView("/Trajets.fxml", trajetTab);
        loadView("/Reservations.fxml", reservationTab);
        loadView("/ListeForum.fxml", forumTab);
        loadView("/Reclamations.fxml", reclamationTab);
        loadView("/Profile.fxml", profileTab);

        // 🔥 OUVRIR TRAJETS PAR DÉFAUT
        passagerTabs.getSelectionModel().select(trajetTabButton);
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