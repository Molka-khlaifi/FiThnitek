package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import models.DocumentVehicule;
import models.Vehicule;
import services.DocumentVehiculeService;
import services.ResponsivePageUtil;
import services.NavigationManager;
import services.ResponsivePageUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class AjouterDocumentVehiculeController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label vehiculeLabel;

    @FXML
    private ComboBox<String> typeDocumentComboBox;

    @FXML
    private TextField nomFichierTextField;

    @FXML
    private Label messageLabel;

    private final DocumentVehiculeService documentVehiculeService = new DocumentVehiculeService();

    private Vehicule vehiculeActuel;

    private File fichierSelectionne;

    @FXML
    public void initialize() {
        ResponsivePageUtil.fitAnchorContent(rootPane, 800, 580);
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
    private void choisirFichierAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un document véhicule");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documents et images", "*.pdf", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File fichier = fileChooser.showOpenDialog(messageLabel.getScene().getWindow());

        if (fichier != null) {
            fichierSelectionne = fichier;
            nomFichierTextField.setText(fichier.getName());
            messageLabel.setText("Fichier sélectionné : " + fichier.getName());
        }
    }

    @FXML
    private void ajouterDocumentAction() {
        if (vehiculeActuel == null) {
            messageLabel.setText("Aucun véhicule sélectionné.");
            return;
        }

        if (typeDocumentComboBox.getValue() == null) {
            messageLabel.setText("Veuillez choisir le type du document.");
            return;
        }

        if (fichierSelectionne == null) {
            messageLabel.setText("Veuillez uploader un fichier.");
            return;
        }

        try {
            Path dossierUpload = Paths.get("uploads", "documents");

            if (!Files.exists(dossierUpload)) {
                Files.createDirectories(dossierUpload);
            }

            String nomOriginal = fichierSelectionne.getName();
            String nomSecurise = nettoyerNomFichier(nomOriginal);
            String nomFinal = System.currentTimeMillis() + "_" + nomSecurise;

            Path destination = dossierUpload.resolve(nomFinal);

            Files.copy(
                    fichierSelectionne.toPath(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );

            String cheminFichier = "uploads/documents/" + nomFinal;

            DocumentVehicule document = new DocumentVehicule(
                    vehiculeActuel.getIdVehicule(),
                    typeDocumentComboBox.getValue(),
                    nomFinal,
                    cheminFichier,
                    "EN_ATTENTE"
            );

            documentVehiculeService.add(document);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Document ajouté avec succès !");
            alert.show();

            messageLabel.setText("Document ajouté avec succès dans uploads/documents/.");
            viderChamps();

        } catch (IOException e) {
            messageLabel.setText("Erreur lors de l'upload du fichier.");
            System.out.println("Erreur upload document : " + e.getMessage());
        }
    }

    @FXML
    private void retourAction() {
        try {
            NavigationManager.navigateFrom(messageLabel, "/DocumentsVehicule.fxml",
                    (DocumentsVehiculeController controller) -> controller.setVehicule(vehiculeActuel));

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private String nettoyerNomFichier(String nomFichier) {
        return nomFichier
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private void viderChamps() {
        nomFichierTextField.clear();
        fichierSelectionne = null;
        typeDocumentComboBox.setValue("ASSURANCE");
    }
}
