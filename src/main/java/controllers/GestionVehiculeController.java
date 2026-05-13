package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import models.Vehicule;
import services.VehiculeService;
import javafx.geometry.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    private VBox vehiculeCardsPane;

    private final VehiculeService vehiculeService = new VehiculeService();

    private ObservableList<Vehicule> tousLesVehicules = FXCollections.observableArrayList();

    private Vehicule vehiculeSelectionne;

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

        // Vérifie et supprime définitivement les véhicules demandés en suppression depuis plus de 48h
        vehiculeService.supprimerVehiculesApres48h();
        chargerVehicules();

        searchTextField.textProperty().addListener((obs, oldVal, newVal) -> appliquerRechercheEtFiltre());

        searchTextField.setOnAction(event -> rechercherAction());
    }

    private void chargerVehicules() {
        List<Vehicule> vehicules = vehiculeService.getAll();

        tousLesVehicules = FXCollections.observableArrayList(vehicules);

        afficherCartes(tousLesVehicules);

        statsLabel.setText(vehicules.size() + " véhicule(s)");
        messageLabel.setText("Véhicules chargés avec succès.");
    }

    private void afficherCartes(List<Vehicule> vehicules) {
        vehiculeCardsPane.getChildren().clear();

        for (Vehicule vehicule : vehicules) {
            VBox carte = creerCarteVehicule(vehicule);
            vehiculeCardsPane.getChildren().add(carte);
        }
    }

    private VBox creerCarteVehicule(Vehicule vehicule) {
        VBox carte = new VBox();
        carte.setPrefWidth(500);
        carte.setMinHeight(165);
        carte.setPadding(new Insets(12));
        carte.setCursor(Cursor.HAND);

        boolean estSelectionne = vehiculeSelectionne != null
                && vehiculeSelectionne.getIdVehicule() == vehicule.getIdVehicule();

        if (estSelectionne) {
            carte.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-background-radius: 18;" +
                            "-fx-border-radius: 18;" +
                            "-fx-border-color: #3498db;" +
                            "-fx-border-width: 3;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(52,152,219,0.35), 14, 0, 0, 5);"
            );
        } else {
            carte.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-background-radius: 18;" +
                            "-fx-border-radius: 18;" +
                            "-fx-border-color: #eeeeee;" +
                            "-fx-border-width: 1;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 12, 0, 0, 4);"
            );
        }

        HBox contenuCarte = new HBox(18);
        contenuCarte.setAlignment(Pos.CENTER_LEFT);

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefWidth(170);
        imageContainer.setPrefHeight(125);
        imageContainer.setMinWidth(170);
        imageContainer.setMaxWidth(170);
        imageContainer.setStyle(
                "-fx-background-color: #ecf0f1;" +
                        "-fx-background-radius: 14;"
        );

        Image image = chargerImageVehicule(vehicule.getPhotoPath());

        if (image != null) {
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(170);
            imageView.setFitHeight(125);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            imageContainer.getChildren().add(imageView);
        } else {
            Label placeholder = new Label("🚗");
            placeholder.setStyle("-fx-font-size: 46px;");
            imageContainer.getChildren().add(placeholder);
        }

        VBox detailsBox = new VBox(8);
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        detailsBox.setPrefWidth(280);

        Label titreLabel = new Label(texteAffichage(vehicule.getMarque()) + " " + texteAffichage(vehicule.getModele()));
        titreLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        titreLabel.setWrapText(true);

        Label immatriculationLabel = new Label("Matricule : " + texteAffichage(vehicule.getImmatriculation()));
        immatriculationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

        HBox ligneBadges1 = new HBox(8);
        ligneBadges1.setAlignment(Pos.CENTER_LEFT);

        Label anneeBadge = creerBadge("Année " + vehicule.getAnnee(), "#3498db");
        Label placesBadge = creerBadge(vehicule.getNombrePlaces() + " places", "#1abc9c");
        Label typeBadge = creerBadge(texteAffichage(vehicule.getTypeVehicule()), "#9b59b6");

        ligneBadges1.getChildren().addAll(anneeBadge, placesBadge, typeBadge);

        HBox ligneBadges2 = new HBox(8);
        ligneBadges2.setAlignment(Pos.CENTER_LEFT);

        Label energieBadge = creerBadge(texteAffichage(vehicule.getEnergie()), "#f39c12");
        Label couleurBadge = creerBadge("Couleur " + texteAffichage(vehicule.getCouleur()), "#34495e");

        ligneBadges2.getChildren().addAll(energieBadge, couleurBadge);

        Label statutLabel = new Label("Statut : " + texteAffichage(vehicule.getStatut()));
        statutLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        detailsBox.getChildren().addAll(
                titreLabel,
                immatriculationLabel,
                ligneBadges1,
                ligneBadges2,
                statutLabel
        );

        contenuCarte.getChildren().addAll(imageContainer, detailsBox);
        carte.getChildren().add(contenuCarte);

        carte.setOnMouseClicked(event -> {
            vehiculeSelectionne = vehicule;
            afficherCartes(getVehiculesAffiches());
            messageLabel.setText("Véhicule sélectionné : " + vehicule.getMarque() + " " + vehicule.getModele());
        });

        return carte;
    }

    private Label creerBadge(String texte, String couleur) {
        Label badge = new Label(texte);
        badge.setStyle(
                "-fx-background-color: " + couleur + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 4 8 4 8;"
        );
        return badge;
    }

    private Image chargerImageVehicule(String photoPath) {
        if (photoPath == null || photoPath.trim().isEmpty()) {
            return null;
        }

        try {
            File file = new File(photoPath);

            if (!file.isAbsolute()) {
                file = new File(System.getProperty("user.dir"), photoPath);
            }

            if (file.exists()) {
                return new Image(file.toURI().toString());
            }

            String resourcePath = photoPath.startsWith("/") ? photoPath : "/" + photoPath;
            InputStream inputStream = getClass().getResourceAsStream(resourcePath);

            if (inputStream != null) {
                return new Image(inputStream);
            }

        } catch (Exception e) {
            System.out.println("Erreur chargement image véhicule : " + e.getMessage());
        }

        return null;
    }



    private void appliquerRechercheEtFiltre() {
        List<Vehicule> resultats = getVehiculesAffiches();

        afficherCartes(resultats);

        statsLabel.setText(resultats.size() + " véhicule(s)");

        String recherche = searchTextField.getText().toLowerCase().trim();
        String typeChoisi = typeFilterComboBox.getValue();

        if (recherche.isEmpty() && (typeChoisi == null || typeChoisi.equals("Tous"))) {
            messageLabel.setText("Tous les véhicules affichés.");
        } else {
            messageLabel.setText("Recherche / filtre appliqué.");
        }
    }

    private List<Vehicule> getVehiculesAffiches() {
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
                            || typeChoisi.equals(vehicule.getTypeVehicule());

            if (correspondRecherche && correspondType) {
                resultats.add(vehicule);
            }
        }

        return resultats;
    }

    private String texte(String valeur) {
        if (valeur == null) {
            return "";
        }
        return valeur.toLowerCase();
    }

    private String texteAffichage(String valeur) {
        if (valeur == null || valeur.trim().isEmpty()) {
            return "N/A";
        }
        return valeur;
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
                vehiculeSelectionne = null;
                chargerVehicules();
                appliquerRechercheEtFiltre();
                messageLabel.setText("Demande de suppression enregistrée.");
            }
        });
    }

    @FXML
    private void documentsAction() {
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
    }@FXML
    private void monEspaceVehiculeAction() {
        messageLabel.setText("Vous êtes déjà dans Mon Espace Véhicule.");
    }

    @FXML
    private void impactEnergetiqueAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ImpactEnergetique.fxml"));
            messageLabel.getScene().setRoot(root);
        } catch (IOException e) {
            messageLabel.setText("Erreur lors de l'ouverture de la page Impact énergétique.");
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void maintenanceAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Maintenance.fxml"));
            messageLabel.getScene().setRoot(root);
        } catch (IOException e) {
            messageLabel.setText("Erreur lors de l'ouverture de la page Maintenance.");
            System.out.println(e.getMessage());
        }
    }
}
