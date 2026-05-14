package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.concurrent.Task;
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
import services.OcrService;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocumentsVehiculeController {

    @FXML
    private Label vehiculeLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private VBox documentsCardsPane;

    private final DocumentVehiculeService documentVehiculeService = new DocumentVehiculeService();

    private final OcrService ocrService = new OcrService();

    private Vehicule vehiculeActuel;

    private DocumentVehicule documentSelectionne;

    private final Set<Integer> documentsScannesOcr = new HashSet<>();

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

        StackPane iconBox = creerPreviewDocument(document);

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

        boolean ocrScanne = documentsScannesOcr.contains(document.getIdDocument());
        Label ocrBadge = creerBadge(
                ocrScanne ? "OCR : Scanné" : "OCR : Non scanné",
                ocrScanne ? "#27ae60" : "#7f8c8d"
        );

        Button scannerOcrButton = new Button("Scanner OCR");
        scannerOcrButton.setStyle(
                "-fx-background-color: #1f2d3d;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 5 10 5 10;" +
                        "-fx-cursor: hand;"
        );
        scannerOcrButton.setOnAction(event -> {
            event.consume();
            scannerOcrAction(document);
        });

        ocrLine.getChildren().addAll(ocrBadge, scannerOcrButton);

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

    private StackPane creerPreviewDocument(DocumentVehicule document) {
        StackPane previewBox = new StackPane();
        previewBox.setPrefWidth(115);
        previewBox.setPrefHeight(90);
        previewBox.setMinWidth(115);
        previewBox.setMaxWidth(115);
        previewBox.setStyle(
                "-fx-background-color: #ecf0f1;" +
                        "-fx-background-radius: 14;"
        );

        String cheminFichier = document.getCheminFichier();

        if (estImage(cheminFichier)) {
            Image image = chargerImageDocument(cheminFichier);

            if (image != null) {
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(105);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                previewBox.setCursor(Cursor.HAND);
                Tooltip.install(previewBox, new Tooltip("Cliquer pour prévisualiser"));

                previewBox.setOnMouseClicked(event -> ouvrirApercuImage(document));

                previewBox.getChildren().add(imageView);
                return previewBox;
            }
        }

        Label documentIcon = new Label("📄");
        documentIcon.setStyle("-fx-font-size: 38px;");
        previewBox.getChildren().add(documentIcon);

        Tooltip.install(previewBox, new Tooltip("Aperçu disponible pour les images"));

        return previewBox;
    }

    private boolean estImage(String cheminFichier) {
        if (cheminFichier == null) {
            return false;
        }

        String chemin = cheminFichier.toLowerCase();

        return chemin.endsWith(".png")
                || chemin.endsWith(".jpg")
                || chemin.endsWith(".jpeg");
    }

    private Image chargerImageDocument(String cheminFichier) {
        if (cheminFichier == null || cheminFichier.trim().isEmpty()) {
            return null;
        }

        try {
            File file = new File(cheminFichier);

            if (!file.isAbsolute()) {
                file = new File(System.getProperty("user.dir"), cheminFichier);
            }

            if (file.exists()) {
                // IMPORTANT: no width/height here
                // This loads the original image quality
                return new Image(file.toURI().toString());
            }

            String resourcePath = cheminFichier.startsWith("/") ? cheminFichier : "/" + cheminFichier;
            InputStream inputStream = getClass().getResourceAsStream(resourcePath);

            if (inputStream != null) {
                // IMPORTANT: no width/height here either
                return new Image(inputStream);
            }

        } catch (Exception e) {
            System.out.println("Erreur chargement image document : " + e.getMessage());
        }

        return null;
    }

    private void ouvrirApercuImage(DocumentVehicule document) {
        Image image = chargerImageDocument(document.getCheminFichier());

        if (image == null) {
            messageLabel.setText("Impossible d'ouvrir l'aperçu de cette image.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Aperçu du document");
        dialog.setHeaderText(document.getNomFichier());

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(650);
        imageView.setFitHeight(420);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(false);

        ScrollPane scrollPane = new ScrollPane(imageView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefWidth(680);
        scrollPane.setPrefHeight(460);
        scrollPane.setStyle("-fx-background-color: white;");

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }

    private void scannerOcrAction(DocumentVehicule document) {
        File fichier = obtenirFichierDocument(document.getCheminFichier());

        if (fichier == null || !fichier.exists() || !fichier.isFile()) {
            afficherAlerte(
                    Alert.AlertType.ERROR,
                    "Scanner OCR",
                    "Fichier introuvable",
                    "Impossible de trouver le fichier du document.\nFichier : " + texteAffichage(document.getCheminFichier())
            );
            return;
        }

        if (estPdf(document.getCheminFichier())) {
            afficherAlerte(
                    Alert.AlertType.INFORMATION,
                    "Scanner OCR",
                    "OCR PDF",
                    "L'OCR des fichiers PDF sera ajout\u00e9 plus tard.\nFichier : " + fichier.getPath()
            );
            return;
        }

        if (!estImage(document.getCheminFichier())) {
            afficherAlerte(
                    Alert.AlertType.ERROR,
                    "Scanner OCR",
                    "Format non support\u00e9",
                    "Phase 2 supporte uniquement les images .jpg, .jpeg et .png.\nFichier : " + fichier.getPath()
            );
            return;
        }

        messageLabel.setText("Analyse OCR en cours...");

        Task<OcrService.OcrResult> ocrTask = new Task<>() {
            @Override
            protected OcrService.OcrResult call() throws Exception {
                return ocrService.scannerImage(fichier, document.getTypeDocument());
            }
        };

        ocrTask.setOnSucceeded(event -> {
            documentsScannesOcr.add(document.getIdDocument());
            messageLabel.setText("OCR termin\u00e9 pour " + texteAffichage(document.getNomFichier()) + ".");
            afficherAlerte(
                    Alert.AlertType.INFORMATION,
                    "Scanner OCR",
                    "R\u00e9sultat OCR",
                    ocrTask.getValue().toDisplayText()
            );
            chargerDocuments();
        });

        ocrTask.setOnFailed(event -> {
            Throwable exception = ocrTask.getException();

            if (exception instanceof OcrService.MissingApiKeyException) {
                messageLabel.setText("Cl\u00e9 API Gemini manquante.");
                afficherAlerte(
                        Alert.AlertType.WARNING,
                        "Scanner OCR",
                        "Configuration Gemini manquante",
                        exception.getMessage()
                );
            } else {
                String message = exception == null ? "Erreur OCR inconnue." : exception.getMessage();
                messageLabel.setText("Erreur pendant l'analyse OCR.");
                afficherAlerte(
                        Alert.AlertType.ERROR,
                        "Scanner OCR",
                        "Erreur OCR",
                        message
                );
            }
        });

        Thread thread = new Thread(ocrTask);
        thread.setDaemon(true);
        thread.start();
    }

    private File obtenirFichierDocument(String cheminFichier) {
        if (cheminFichier == null || cheminFichier.trim().isEmpty()) {
            return null;
        }

        File fichier = new File(cheminFichier);

        if (!fichier.isAbsolute()) {
            fichier = new File(System.getProperty("user.dir"), cheminFichier);
        }

        return fichier;
    }

    private boolean estPdf(String cheminFichier) {
        return cheminFichier != null && cheminFichier.toLowerCase().endsWith(".pdf");
    }

    private void afficherAlerte(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
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
    private void modifierDocumentAction() {
        if (documentSelectionne == null) {
            messageLabel.setText("Veuillez sélectionner un document à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierDocumentVehicule.fxml"));
            Parent root = loader.load();

            ModifierDocumentVehiculeController controller = loader.getController();
            controller.setDocumentEtVehicule(documentSelectionne, vehiculeActuel);

            messageLabel.getScene().setRoot(root);

        } catch (IOException e) {
            messageLabel.setText("Erreur lors de l'ouverture du formulaire de modification.");
            System.out.println(e.getMessage());
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
}
