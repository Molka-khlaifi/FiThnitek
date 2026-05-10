package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import models.DocumentVehicule;
import models.Vehicule;
import services.DocumentVehiculeService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DocumentsVehiculeController {

    @FXML
    private Label vehiculeLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private VBox documentsCardsPane;

    private final DocumentVehiculeService documentVehiculeService = new DocumentVehiculeService();

    private Vehicule vehiculeActuel;

    private DocumentVehicule documentSelectionne;

    @FXML
    public void initialize() {
        // No table columns anymore.
        // Documents are now displayed as vertical cards.
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
        if (vehiculeActuel == null) {
            messageLabel.setText("Aucun véhicule sélectionné.");
            return;
        }

        List<DocumentVehicule> tousLesDocuments = documentVehiculeService.getAll();
        List<DocumentVehicule> documentsVehicule = new ArrayList<>();

        for (DocumentVehicule document : tousLesDocuments) {
            if (document.getIdVehicule() == vehiculeActuel.getIdVehicule()) {
                documentsVehicule.add(document);
            }
        }

        afficherCartesDocuments(documentsVehicule);
        messageLabel.setText(documentsVehicule.size() + " document(s) trouvé(s).");
    }

    private void afficherCartesDocuments(List<DocumentVehicule> documents) {
        documentsCardsPane.getChildren().clear();

        for (DocumentVehicule document : documents) {
            VBox carte = creerCarteDocument(document);
            documentsCardsPane.getChildren().add(carte);
        }
    }

    private VBox creerCarteDocument(DocumentVehicule document) {
        VBox carte = new VBox();
        carte.setPrefWidth(620);
        carte.setSpacing(10);
        carte.setPadding(new Insets(14));
        carte.setCursor(Cursor.HAND);

        boolean estSelectionne = documentSelectionne != null
                && documentSelectionne.getIdDocument() == document.getIdDocument();

        if (estSelectionne) {
            carte.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-background-radius: 16;" +
                            "-fx-border-radius: 16;" +
                            "-fx-border-color: #3498db;" +
                            "-fx-border-width: 3;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(52,152,219,0.35), 12, 0, 0, 5);"
            );
        } else {
            carte.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-background-radius: 16;" +
                            "-fx-border-radius: 16;" +
                            "-fx-border-color: #eeeeee;" +
                            "-fx-border-width: 1;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 4);"
            );
        }

        HBox contenuCarte = new HBox(16);
        contenuCarte.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        iconBox.setPrefWidth(90);
        iconBox.setPrefHeight(90);
        iconBox.setMinWidth(90);
        iconBox.setStyle(
                "-fx-background-color: #ecf0f1;" +
                        "-fx-background-radius: 14;"
        );

        Label documentIcon = new Label("📄");
        documentIcon.setStyle("-fx-font-size: 38px;");
        iconBox.getChildren().add(documentIcon);

        VBox infosBox = new VBox(8);
        infosBox.setAlignment(Pos.CENTER_LEFT);
        infosBox.setPrefWidth(485);

        HBox titleLine = new HBox(10);
        titleLine.setAlignment(Pos.CENTER_LEFT);

        Label typeLabel = new Label(formaterTypeDocument(document.getTypeDocument()));
        typeLabel.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label statutBadge = creerBadge(
                texteAffichage(document.getStatutDocument()),
                couleurStatut(document.getStatutDocument())
        );

        titleLine.getChildren().addAll(typeLabel, statutBadge);

        Label nomFichierLabel = new Label("Nom fichier : " + texteAffichage(document.getNomFichier()));
        nomFichierLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

        Label dateUploadLabel = new Label("Date upload : " + texteAffichage(document.getDateUpload()));
        dateUploadLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

        HBox ocrLine = new HBox(8);
        ocrLine.setAlignment(Pos.CENTER_LEFT);

        Label ocrBadge = creerBadge("OCR : Non scanné", "#7f8c8d");
        Label ocrHint = new Label("Reconnaissance automatique à ajouter plus tard");
        ocrHint.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");

        ocrLine.getChildren().addAll(ocrBadge, ocrHint);

        infosBox.getChildren().addAll(
                titleLine,
                nomFichierLabel,
                dateUploadLabel,
                ocrLine
        );

        contenuCarte.getChildren().addAll(iconBox, infosBox);
        carte.getChildren().add(contenuCarte);

        carte.setOnMouseClicked(event -> {
            documentSelectionne = document;
            chargerDocuments();
            messageLabel.setText("Document sélectionné : " + document.getNomFichier());
        });

        return carte;
    }

    private Label creerBadge(String texte, String couleur) {
        Label badge = new Label(texte);
        badge.setStyle(
                "-fx-background-color: " + couleur + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 4 9 4 9;"
        );
        return badge;
    }

    private String couleurStatut(String statut) {
        if (statut == null) {
            return "#7f8c8d";
        }

        switch (statut) {
            case "VALIDE":
                return "#27ae60";
            case "REFUSE":
                return "#e74c3c";
            case "EN_ATTENTE":
                return "#f39c12";
            default:
                return "#7f8c8d";
        }
    }

    private String formaterTypeDocument(String type) {
        if (type == null || type.trim().isEmpty()) {
            return "Document véhicule";
        }

        switch (type) {
            case "CARTE_GRISE":
                return "Carte grise";
            case "ASSURANCE":
                return "Assurance";
            case "VISITE_TECHNIQUE":
                return "Visite technique";
            case "VIGNETTE":
                return "Vignette";
            case "AUTRE":
                return "Autre document";
            default:
                return type;
        }
    }

    private String texteAffichage(String valeur) {
        if (valeur == null || valeur.trim().isEmpty()) {
            return "N/A";
        }
        return valeur;
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
                documentSelectionne = null;
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