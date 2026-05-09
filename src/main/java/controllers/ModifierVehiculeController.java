package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import models.Vehicule;
import services.VehiculeService;

import java.io.IOException;

public class ModifierVehiculeController {

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
    private Label messageLabel;

    private VehiculeService vehiculeService = new VehiculeService();

    private Vehicule vehiculeActuel;

    @FXML
    public void initialize() {
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
    }

    @FXML
    private void modifierVehiculeAction() {
        if (!champsValides()) {
            return;
        }

        try {
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
                    vehiculeActuel.getPhotoPath(),
                    vehiculeActuel.getStatut(),
                    vehiculeActuel.getStatutValidation()
            );

            vehiculeService.update(vehiculeModifie);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Véhicule modifié avec succès !");
            alert.show();

            messageLabel.setText("Véhicule modifié avec succès !");

        } catch (NumberFormatException e) {
            messageLabel.setText("Année et nombre de places doivent être des nombres.");
        }
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