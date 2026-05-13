package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.io.IOException;

public class MaintenanceController {

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        messageLabel.setText("Page Maintenance charg\u00e9e. Les rappels r\u00e9els seront connect\u00e9s plus tard.");
    }

    @FXML
    private void monEspaceVehiculeAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/GestionVehicule.fxml"));
            messageLabel.getScene().setRoot(root);
        } catch (IOException e) {
            messageLabel.setText("Erreur lors du retour vers Mon Espace V\u00e9hicule.");
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void impactEnergetiqueAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ImpactEnergetique.fxml"));
            messageLabel.getScene().setRoot(root);
        } catch (IOException e) {
            messageLabel.setText("Erreur lors de l'ouverture de la page Impact \u00e9nerg\u00e9tique.");
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void maintenanceAction() {
        messageLabel.setText("Vous \u00eates d\u00e9j\u00e0 dans la page Maintenance.");
    }

    @FXML
    private void afficherNotificationsAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rappels maintenance");
        alert.setHeaderText("Notifications pr\u00e9ventives");
        alert.setContentText(
                "- Vidange recommand\u00e9e dans 10 km.\n" +
                        "- Assurance \u00e0 renouveler prochainement.\n" +
                        "- Visite technique \u00e0 planifier.\n" +
                        "- Vignette \u00e0 v\u00e9rifier."
        );
        alert.showAndWait();
    }
}
