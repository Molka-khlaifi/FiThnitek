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
import models.Vehicule;
import services.NavigationManager;
import services.ResponsivePageUtil;
import services.VehiculeService;
import services.ResponsivePageUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ModifierVehiculeController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField idVehiculeTextField;

    @FXML
    private TextField marqueTextField;

    @FXML
    private TextField modeleTextField;

    @FXML
    private TextField immatriculationTextField;

    @FXML
    private TextField couleurTextField;

    @FXML
    private TextField anneeTextField;

    @FXML
    private TextField placesTextField;

    @FXML
    private ComboBox<String> typeVehiculeComboBox;

    @FXML
    private ComboBox<String> energieComboBox;

    @FXML
    private TextField photoPathTextField;

    @FXML
    private Label messageLabel;

    private final VehiculeService vehiculeService = new VehiculeService();

    private Vehicule vehiculeActuel;

    private File nouvellePhotoSelectionnee;

    @FXML
    public void initialize() {
        ResponsivePageUtil.fitAnchorContent(rootPane, 800, 580);
        typeVehiculeComboBox.getItems().addAll(
                "VOITURE",
                "MOTO",
                "VAN",
                "AUTRE"
        );

        energieComboBox.getItems().addAll(
                "ESSENCE",
                "DIESEL",
                "HYBRIDE",
                "ELECTRIQUE",
                "GPL",
                "AUTRE"
        );
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehiculeActuel = vehicule;

        idVehiculeTextField.setText(String.valueOf(vehicule.getIdVehicule()));
        marqueTextField.setText(vehicule.getMarque());
        modeleTextField.setText(vehicule.getModele());
        immatriculationTextField.setText(vehicule.getImmatriculation());
        couleurTextField.setText(vehicule.getCouleur());
        anneeTextField.setText(String.valueOf(vehicule.getAnnee()));
        placesTextField.setText(String.valueOf(vehicule.getNombrePlaces()));
        typeVehiculeComboBox.setValue(vehicule.getTypeVehicule());
        energieComboBox.setValue(vehicule.getEnergie());
        photoPathTextField.setText(vehicule.getPhotoPath());
    }

    @FXML
    private void choisirPhotoAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une nouvelle photo du véhicule");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg")
        );

        File fichier = fileChooser.showOpenDialog(messageLabel.getScene().getWindow());

        if (fichier != null) {
            nouvellePhotoSelectionnee = fichier;
            photoPathTextField.setText(fichier.getName());
            messageLabel.setText("Nouvelle photo sélectionnée : " + fichier.getName());
        }
    }

    @FXML
    private void modifierVehiculeAction() {
        if (!champsValides()) {
            return;
        }

        try {
            String photoPathFinal = vehiculeActuel.getPhotoPath();

            if (nouvellePhotoSelectionnee != null) {
                Path dossierUpload = Paths.get("uploads", "vehicules");

                if (!Files.exists(dossierUpload)) {
                    Files.createDirectories(dossierUpload);
                }

                String nomOriginal = nouvellePhotoSelectionnee.getName();
                String nomSecurise = nettoyerNomFichier(nomOriginal);
                String nomFinal = System.currentTimeMillis() + "_" + nomSecurise;

                Path destination = dossierUpload.resolve(nomFinal);

                Files.copy(
                        nouvellePhotoSelectionnee.toPath(),
                        destination,
                        StandardCopyOption.REPLACE_EXISTING
                );

                photoPathFinal = "uploads/vehicules/" + nomFinal;
            }

            Vehicule vehiculeModifie = new Vehicule(
                    vehiculeActuel.getIdVehicule(),
                    vehiculeActuel.getIdUtilisateur(),
                    marqueTextField.getText(),
                    modeleTextField.getText(),
                    immatriculationTextField.getText(),
                    couleurTextField.getText(),
                    Integer.parseInt(anneeTextField.getText()),
                    Integer.parseInt(placesTextField.getText()),
                    typeVehiculeComboBox.getValue(),
                    energieComboBox.getValue(),
                    photoPathFinal,
                    vehiculeActuel.getStatut(),
                    vehiculeActuel.getStatutValidation()
            );

            vehiculeService.update(vehiculeModifie);

            vehiculeActuel = vehiculeModifie;
            nouvellePhotoSelectionnee = null;
            photoPathTextField.setText(photoPathFinal);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Véhicule modifié avec succès !");
            alert.show();

            messageLabel.setText("Véhicule modifié avec succès !");

        } catch (IOException e) {
            messageLabel.setText("Erreur lors de l'upload de la photo.");
            System.out.println("Erreur upload photo véhicule : " + e.getMessage());
        } catch (NumberFormatException e) {
            messageLabel.setText("Année et nombre de places doivent être des nombres.");
        }
    }

    @FXML
    private void retourAction() {
        try {
            NavigationManager.navigateFrom(messageLabel, "/GestionVehicule.fxml");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private String nettoyerNomFichier(String nomFichier) {
        return nomFichier
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private boolean champsValides() {
        if (marqueTextField.getText().isEmpty()
                || modeleTextField.getText().isEmpty()
                || immatriculationTextField.getText().isEmpty()
                || couleurTextField.getText().isEmpty()
                || anneeTextField.getText().isEmpty()
                || placesTextField.getText().isEmpty()
                || typeVehiculeComboBox.getValue() == null
                || energieComboBox.getValue() == null) {

            messageLabel.setText("Veuillez remplir tous les champs.");
            return false;
        }

        try {
            int annee = Integer.parseInt(anneeTextField.getText());
            int places = Integer.parseInt(placesTextField.getText());

            if (annee < 1990 || annee > 2026) {
                messageLabel.setText("L'année doit être entre 1990 et 2026.");
                return false;
            }

            if (places < 1 || places > 9) {
                messageLabel.setText("Le nombre de places doit être entre 1 et 9.");
                return false;
            }

        } catch (NumberFormatException e) {
            messageLabel.setText("Année et nombre de places doivent être des nombres.");
            return false;
        }

        return true;
    }
}
