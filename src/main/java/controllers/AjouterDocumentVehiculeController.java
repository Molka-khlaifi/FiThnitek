package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import models.DocumentVehicule;
import models.Vehicule;
import services.DocumentVehiculeService;

import java.io.IOException;
import java.sql.SQLException;

public class AjouterDocumentVehiculeController {

    @FXML
    private Label vehiculeLabel;

    @FXML
    private ComboBox<String> typeDocumentComboBox;

    @FXML
    private TextField nomFichierTextField;

    @FXML
    private TextField cheminFichierTextField;

    @FXML
    private Label messageLabel;

    private DocumentVehiculeService documentVehiculeService = new DocumentVehiculeService();

    private Vehicule vehiculeActuel;

    @FXML
    public void initialize() {
        typeDocumentComboBox.getItems().addAll(
                "CARTE_GRISE",
                "ASSURANCE",
                "VISITE_TECHNIQUE",
                "VIGNETTE",
                "AUTRE"
        );

        typeDocumentComboBox.setValue("ASSURANCE");
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehiculeActuel = vehicule;

        vehiculeLabel.setText(
                vehicule.getMarque() + " " +
                        vehicule.getModele() + " - " +
                        vehicule.getImmatriculation()
        );
    }

    @FXML
    private void ajouterDocumentAction() {
        if (vehiculeActuel == null) {
            messageLabel.setText("Aucun véhicule sélectionné.");
            return;
        }

        if (typeDocumentComboBox.getValue() == null
                || nomFichierTextField.getText().isEmpty()
                || cheminFichierTextField.getText().isEmpty()) {

            messageLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        if (!cheminFichierTextField.getText().startsWith("uploads/documents/")) {
            messageLabel.setText("Le chemin doit commencer par uploads/documents/");
            return;
        }

        DocumentVehicule document = new DocumentVehicule(
                vehiculeActuel.getIdVehicule(),
                typeDocumentComboBox.getValue(),
                nomFichierTextField.getText(),
                cheminFichierTextField.getText(),
                "EN_ATTENTE"
        );
        documentVehiculeService.add(document);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Document ajouté avec succès !");
        alert.show();

        messageLabel.setText("Document ajouté avec succès !");
        viderChamps();
    }

    @FXML
    private void retourAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DocumentsVehicule.fxml"));
            Parent root = loader.load();

            DocumentsVehiculeController controller = loader.getController();
            controller.setVehicule(vehiculeActuel);

            messageLabel.getScene().setRoot(root);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void viderChamps() {
        nomFichierTextField.clear();
        cheminFichierTextField.clear();
        typeDocumentComboBox.setValue("ASSURANCE");
    }
}