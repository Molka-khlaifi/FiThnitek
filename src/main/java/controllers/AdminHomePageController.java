package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

public class AdminHomePageController {

    @FXML private TabPane adminTabs;

    @FXML private AnchorPane usersTab;
    @FXML private AnchorPane vehiclesTab;
    @FXML private AnchorPane maintenanceTab;
    @FXML private AnchorPane tripsTab;
    @FXML private AnchorPane forumTab;
    @FXML private AnchorPane claimsTab;
    @FXML private AnchorPane revenueTab;
    @FXML private AnchorPane statsTab;

    @FXML
    public void initialize() {

        loadView("/Users.fxml", usersTab);
        loadView("/Vehicles.fxml", vehiclesTab);
        loadView("/Maintenance.fxml", maintenanceTab);
        loadView("/Trips.fxml", tripsTab);
        loadView("/AdminForumView.fxml", forumTab);
        loadView("/Reclamations.fxml", claimsTab);
        loadView("/Revenue.fxml", revenueTab);
        loadView("/Stats.fxml", statsTab);
        adminTabs.getSelectionModel().select(5);

    }

    // 🔥 MÉTHODE GÉNÉRIQUE DE CHARGEMENT
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