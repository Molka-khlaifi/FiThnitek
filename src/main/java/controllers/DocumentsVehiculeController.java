package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.DocumentVehicule;
import models.Vehicule;
import services.DocumentVehiculeService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DocumentsVehiculeController {

    @FXML
    private Label vehiculeLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private TableView<DocumentVehicule> documentsTableView;

    @FXML
    private TableColumn<DocumentVehicule, String> typeDocumentColumn;

    @FXML
    private TableColumn<DocumentVehicule, String> nomFichierColumn;

    @FXML
    private TableColumn<DocumentVehicule, String> cheminFichierColumn;

    @FXML
    private TableColumn<DocumentVehicule, String> statutDocumentColumn;

    private DocumentVehiculeService documentVehiculeService = new DocumentVehiculeService();

    private Vehicule vehiculeActuel;

    @FXML
    public void initialize() {
        typeDocumentColumn.setCellValueFactory(new PropertyValueFactory<>("typeDocument"));
        nomFichierColumn.setCellValueFactory(new PropertyValueFactory<>("nomFichier"));
        cheminFichierColumn.setCellValueFactory(new PropertyValueFactory<>("cheminFichier"));
        statutDocumentColumn.setCellValueFactory(new PropertyValueFactory<>("statutDocument"));
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehiculeActuel = vehicule;

        vehiculeLabel.setText(
                vehicule.getMarque() + " " +
                        vehicule.getModele() + " - " +
                        vehicule.getImmatriculation()
        );

        chargerDocuments();
    }

    private void chargerDocuments() {
        List<DocumentVehicule> tousLesDocuments = documentVehiculeService.getAll();
        ObservableList<DocumentVehicule> documentsVehicule = FXCollections.observableArrayList();

        for (DocumentVehicule document : tousLesDocuments) {
            if (document.getIdVehicule() == vehiculeActuel.getIdVehicule()) {
                documentsVehicule.add(document);
            }
        }

        documentsTableView.setItems(documentsVehicule);
        messageLabel.setText(documentsVehicule.size() + " document(s) trouvé(s).");

    }

    @FXML
    private void ajouterDocumentAction() {
        if (vehiculeActuel == null) {
            messageLabel.setText("Aucun véhicule sélectionné.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterDocumentVehicule.fxml"));
            Parent root = loader.load();

            AjouterDocumentVehiculeController controller = loader.getController();
            controller.setVehicule(vehiculeActuel);

            messageLabel.getScene().setRoot(root);

        } catch (IOException e) {
            messageLabel.setText("Erreur lors de l'ouverture du formulaire d'ajout document.");
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void supprimerDocumentAction() {
        DocumentVehicule documentSelectionne = documentsTableView.getSelectionModel().getSelectedItem();

        if (documentSelectionne == null) {
            messageLabel.setText("Veuillez sélectionner un document à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer ce document ?");
        confirmation.setContentText("Le document sera supprimé définitivement.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                documentVehiculeService.delete(documentSelectionne);
                chargerDocuments();
                messageLabel.setText("Document supprimé avec succès.");
            }
        });
    }

    @FXML
    private void retourAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/GestionVehicule.fxml"));
            messageLabel.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}