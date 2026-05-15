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

public class ModifierDocumentVehiculeController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label vehiculeLabel;

    @FXML
    private ComboBox<String> typeDocumentComboBox;

    @FXML
    private TextField nomFichierTextField;

    @FXML
    private Label statutLabel;

    @FXML
    private Label messageLabel;

    private final DocumentVehiculeService documentVehiculeService = new DocumentVehiculeService();

    private DocumentVehicule documentActuel;
    private Vehicule vehiculeActuel;

    private File nouveauFichierSelectionne;

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
    }

    public void setDocumentEtVehicule(DocumentVehicule document, Vehicule vehicule) {
        this.documentActuel = document;
        this.vehiculeActuel = vehicule;

        vehiculeLabel.setText(
                vehicule.getMarque() + " " +
                        vehicule.getModele() + " - " +
                        vehicule.getImmatriculation()
        );

        typeDocumentComboBox.setValue(document.getTypeDocument());
        nomFichierTextField.setText(document.getNomFichier());
        statutLabel.setText("Statut : " + document.getStatutDocument());
    }

    @FXML
    private void choisirNouveauFichierAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un nouveau document véhicule");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documents et images", "*.pdf", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File fichier = fileChooser.showOpenDialog(messageLabel.getScene().getWindow());

        if (fichier != null) {
            nouveauFichierSelectionne = fichier;
            nomFichierTextField.setText(fichier.getName());
            messageLabel.setText("Nouveau fichier sélectionné : " + fichier.getName());
        }
    }

    @FXML
    private void modifierDocumentAction() {
        if (documentActuel == null || vehiculeActuel == null) {
            messageLabel.setText("Aucun document sélectionné.");
            return;
        }

        if (typeDocumentComboBox.getValue() == null) {
            messageLabel.setText("Veuillez choisir le type du document.");
            return;
        }

        try {
            String nomFichierFinal = documentActuel.getNomFichier();
            String cheminFichierFinal = documentActuel.getCheminFichier();

            if (nouveauFichierSelectionne != null) {
                Path dossierUpload = Paths.get("uploads", "documents");

                if (!Files.exists(dossierUpload)) {
                    Files.createDirectories(dossierUpload);
                }

                String nomOriginal = nouveauFichierSelectionne.getName();
                String nomSecurise = nettoyerNomFichier(nomOriginal);
                String nomFinal = System.currentTimeMillis() + "_" + nomSecurise;

                Path destination = dossierUpload.resolve(nomFinal);

                Files.copy(
                        nouveauFichierSelectionne.toPath(),
                        destination,
                        StandardCopyOption.REPLACE_EXISTING
                );

                nomFichierFinal = nomFinal;
                cheminFichierFinal = "uploads/documents/" + nomFinal;
            }

            documentActuel.setTypeDocument(typeDocumentComboBox.getValue());
            documentActuel.setNomFichier(nomFichierFinal);
            documentActuel.setCheminFichier(cheminFichierFinal);

            documentVehiculeService.update(documentActuel);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Document modifié avec succès !");
            alert.show();

            messageLabel.setText("Document modifié avec succès.");
            nouveauFichierSelectionne = null;
            nomFichierTextField.setText(documentActuel.getNomFichier());

        } catch (IOException e) {
            messageLabel.setText("Erreur lors de la modification du fichier.");
            System.out.println("Erreur modification document : " + e.getMessage());
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
}
