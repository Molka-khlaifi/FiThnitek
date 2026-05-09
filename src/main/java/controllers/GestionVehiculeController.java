package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Vehicule;
import services.VehiculeService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GestionVehiculeController {

    @FXML
    private Label statsLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField searchTextField;

    @FXML
    private ComboBox<String> typeFilterComboBox;

    @FXML
    private TableView<Vehicule> vehiculeTableView;

    @FXML
    private TableColumn<Vehicule, String> marqueColumn;

    @FXML
    private TableColumn<Vehicule, String> modeleColumn;

    @FXML
    private TableColumn<Vehicule, String> immatriculationColumn;

    @FXML
    private TableColumn<Vehicule, String> couleurColumn;

    @FXML
    private TableColumn<Vehicule, Integer> anneeColumn;

    @FXML
    private TableColumn<Vehicule, Integer> placesColumn;

    @FXML
    private TableColumn<Vehicule, String> statutColumn;

    private VehiculeService vehiculeService = new VehiculeService();

    private ObservableList<Vehicule> tousLesVehicules = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        typeFilterComboBox.getItems().addAll(
                "Tous",
                "VOITURE",
                "MOTO",
                "VAN",
                "AUTRE"
        );

        typeFilterComboBox.setValue("Tous");

        marqueColumn.setCellValueFactory(new PropertyValueFactory<>("marque"));
        modeleColumn.setCellValueFactory(new PropertyValueFactory<>("modele"));
        immatriculationColumn.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        couleurColumn.setCellValueFactory(new PropertyValueFactory<>("couleur"));
        anneeColumn.setCellValueFactory(new PropertyValueFactory<>("annee"));
        placesColumn.setCellValueFactory(new PropertyValueFactory<>("nombrePlaces"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        chargerVehicules();

        // Recherche automatique à chaque lettre tapée
        searchTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            appliquerRechercheEtFiltre();
        });

        // Recherche aussi quand on appuie sur Enter
        searchTextField.setOnAction(event -> rechercherAction());
    }

    private void chargerVehicules() {
        List<Vehicule> vehicules = vehiculeService.getAll();

        tousLesVehicules = FXCollections.observableArrayList(vehicules);

        vehiculeTableView.setItems(tousLesVehicules);

        statsLabel.setText(vehicules.size() + " véhicules");
        messageLabel.setText("Véhicules chargés avec succès.");
    }



    private void appliquerRechercheEtFiltre() {
        String recherche = searchTextField.getText().toLowerCase().trim();
        String typeChoisi = typeFilterComboBox.getValue();

        ObservableList<Vehicule> resultats = FXCollections.observableArrayList();

        for (Vehicule vehicule : tousLesVehicules) {
            boolean correspondRecherche =
                    recherche.isEmpty()
                            || texte(vehicule.getMarque()).startsWith(recherche)
                            || texte(vehicule.getModele()).startsWith(recherche)
                            || texte(vehicule.getImmatriculation()).startsWith(recherche);
            boolean correspondType =
                    typeChoisi == null
                            || typeChoisi.equals("Tous")
                            || vehicule.getTypeVehicule().equals(typeChoisi);

            if (correspondRecherche && correspondType) {
                resultats.add(vehicule);
            }
        }

        vehiculeTableView.setItems(resultats);
        statsLabel.setText(resultats.size() + " véhicule(s)");

        if (recherche.isEmpty() && (typeChoisi == null || typeChoisi.equals("Tous"))) {
            messageLabel.setText("Tous les véhicules affichés.");
        } else {
            messageLabel.setText("Recherche / filtre appliqué.");
        }
    }

    private String texte(String valeur) {
        if (valeur == null) {
            return "";
        }
        return valeur.toLowerCase();
    }

    @FXML
    private void rechercherAction() {
        appliquerRechercheEtFiltre();
    }

    @FXML
    private void filtrerTypeAction() {
        appliquerRechercheEtFiltre();
    }

    @FXML
    private void ajouterAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterVehicule.fxml"));
            messageLabel.getScene().setRoot(root);
        } catch (IOException e) {
            messageLabel.setText("Erreur lors de l'ouverture du formulaire d'ajout.");
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void modifierAction() {
        Vehicule vehiculeSelectionne = vehiculeTableView.getSelectionModel().getSelectedItem();

        if (vehiculeSelectionne == null) {
            messageLabel.setText("Veuillez sélectionner un véhicule à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierVehicule.fxml"));
            Parent root = loader.load();

            ModifierVehiculeController controller = loader.getController();
            controller.setVehicule(vehiculeSelectionne);

            messageLabel.getScene().setRoot(root);

        } catch (IOException e) {
            messageLabel.setText("Erreur lors de l'ouverture du formulaire de modification.");
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void supprimerAction() {
        Vehicule vehiculeSelectionne = vehiculeTableView.getSelectionModel().getSelectedItem();

        if (vehiculeSelectionne == null) {
            messageLabel.setText("Veuillez sélectionner un véhicule à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer ce véhicule ?");
        confirmation.setContentText("Le véhicule sera marqué comme SUPPRESSION_DEMANDEE.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                vehiculeService.delete(vehiculeSelectionne);
                chargerVehicules();
                appliquerRechercheEtFiltre();
                messageLabel.setText("Demande de suppression enregistrée.");
            }
        });
    }

    @FXML
    private void documentsAction() {
        Vehicule vehiculeSelectionne = vehiculeTableView.getSelectionModel().getSelectedItem();

        if (vehiculeSelectionne == null) {
            messageLabel.setText("Veuillez sélectionner un véhicule pour voir ses documents.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DocumentsVehicule.fxml"));
            Parent root = loader.load();

            DocumentsVehiculeController controller = loader.getController();
            controller.setVehicule(vehiculeSelectionne);

            messageLabel.getScene().setRoot(root);

        } catch (IOException e) {
            messageLabel.setText("Erreur lors de l'ouverture des documents.");
            System.out.println(e.getMessage());
        }
    }
}