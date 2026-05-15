package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import models.publication;
import services.forumService;
import services.ModerationContenu;
import services.NavigationManager;
import services.SessionManager;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


public class AjouterForumController {

    @FXML private TextField imagePathTextField;
    @FXML private ImageView imagePreview;
    private String imageChoisie = null;
    @FXML private TextField        titreTextField;
    @FXML private TextArea         contenuTextArea;
    @FXML private ComboBox<String> categorieComboBox;
    @FXML private ComboBox<String> statutComboBox;
    @FXML private Label            erreurLabel;
    @FXML private Label            compteurLabel;
    private String source = "CONDUCTEUR";
    public void setSource(String source) {
        this.source = source;
    }


    @FXML
    public void initialize() {
        categorieComboBox.getItems().addAll("question", "discussion", "autre");
        categorieComboBox.setValue("discussion");
        statutComboBox.getItems().addAll("ouvert", "ferme");
        statutComboBox.setValue("ouvert");

        // Compteur caractères
        contenuTextArea.textProperty().addListener((obs, oldVal, newVal) -> {
            int count = newVal.length();
            compteurLabel.setText(count + " / 500");
            compteurLabel.setStyle(count > 500
                    ? "-fx-text-fill: #e74c3c; -fx-font-size: 11px;"
                    : "-fx-text-fill: #aaa; -fx-font-size: 11px;");
        });
    }

    @FXML
    void ajouterForumAction(ActionEvent event) {
        erreurLabel.setText("");
        String titre = titreTextField.getText().trim();
        String contenu = contenuTextArea.getText();

        String categorie = categorieComboBox.getValue();
        String statut = statutComboBox.getValue();

        // ── Validation ────────────────────────────────────────────────────
        if (titre.isEmpty()) {
            erreurLabel.setText("Le titre est obligatoire !");
            titreTextField.requestFocus();
            return;
        }
        if (titre.length() < 5) {
            erreurLabel.setText("Le titre doit avoir au moins 5 caractères !");
            titreTextField.requestFocus();
            return;
        }

        // ✅ MODÉRATION DU TITRE
        String titreModere = ModerationContenu.moderer(titre);
        if (titreModere == null) {
            erreurLabel.setText(ModerationContenu.getMessageErreur());
            titreTextField.requestFocus();
            titreTextField.clear();
            return;
        }

        if (contenu.isEmpty()) {
            erreurLabel.setText("Le contenu est obligatoire !");
            contenuTextArea.requestFocus();
            return;
        }
        if (contenu.length() > 500) {
            erreurLabel.setText("Le contenu ne doit pas dépasser 500 caractères !");
            contenuTextArea.requestFocus();
            return;
        }

        // ✅ MODÉRATION DU CONTENU
        String contenuModere = ModerationContenu.moderer(contenu);
        if (contenuModere == null) {
            erreurLabel.setText(ModerationContenu.getMessageErreur());
            contenuTextArea.requestFocus();
            contenuTextArea.clear();
            return;
        }

        Date date = Date.from(LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toInstant());
        Integer trajetId = null;

        publication pub = new publication(
                titreModere, contenuModere, categorie,
                statut, date,
                0, SessionManager.getCurrentUser().getId(), trajetId, false, imageChoisie
        );

        forumService forumService = new forumService();
        try {
            forumService.add(pub);

            retournerListeForum();
            // Afficher un message de succès
            Alert successAlert = new Alert(AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText(null);
            successAlert.setContentText("✅ Votre publication a été ajoutée avec succès !");
            successAlert.showAndWait();

        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
            erreurLabel.setText("Erreur lors de la création du forum");
        }
    }

    @FXML
    void voirListeAction(ActionEvent event) {
        retournerListeForum();
    }

    @FXML
    void annulerAction(ActionEvent event) {
        titreTextField.clear();
        contenuTextArea.clear();
        categorieComboBox.setValue("discussion");
        statutComboBox.setValue("ouvert");
        imageChoisie = null;
        imagePathTextField.clear();
        imagePreview.setImage(null);
        erreurLabel.setText("");

        retournerListeForum();
    }

    @FXML
    void choisirImageAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(titreTextField.getScene().getWindow());
        if (file != null) {
            imageChoisie = file.getAbsolutePath();
            imagePathTextField.setText(file.getName());
            imagePreview.setImage(new Image(file.toURI().toString()));
        }
    }

    private void retournerListeForum() {
        try {
            NavigationManager.navigateFrom(erreurLabel, "ADMIN".equals(source) ? "/ForumAdmin.FXML" : "/ListeForum.fxml");
        } catch (IOException e) {
            erreurLabel.setText("Erreur retour forum : " + e.getMessage());
        }
    }
}
