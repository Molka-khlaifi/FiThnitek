package controllers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.Date;
import java.time.ZoneId;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import models.publication;
import services.forumService;
import util.SessionManager;

import java.io.IOException;

import java.time.LocalDateTime;

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

    @FXML
    public void initialize() {

        categorieComboBox.getItems().addAll("question", "discussion","autre");
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
        String titre    = titreTextField.getText().trim();
        String contenu = contenuTextArea.getText();

        String categorie = categorieComboBox.getValue();
        String statut   = statutComboBox.getValue();

        // ── Validation ────────────────────────────────────────────────────
        if (titre.isEmpty()) {
            erreurLabel.setText("Le titre est obligatoire !");
            titreTextField.requestFocus();
            return;
        }
        if (titre.length() < 5) {
            erreurLabel.setText("Le titre doit avoir au moins 5 caractères !");
            return;
        }
        if (contenu.isEmpty()) {
            erreurLabel.setText("Le contenu est obligatoire !");
            contenuTextArea.requestFocus();
            return;
        }
        if (contenu.length() > 500) {
            erreurLabel.setText("Le contenu ne doit pas dépasser 500 caractères !");
            return;
        }
        Date date = Date.from(LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toInstant());
        Integer trajetId = null;
        publication pub = new publication(
                titre, contenu, categorie,
                statut,date,
                0, SessionManager.getCurrentUser().getId(), trajetId,false,imageChoisie  // auteur_id=1, remplacer par user connecté
        );

        forumService forumService = new forumService();
        try {
            forumService.add(pub);

            // ── Naviguer vers DetailForum ──────────────────────────────────
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailForum.fxml"));
            Parent root = loader.load();
            DetailForumController ctrl = loader.getController();
            ctrl.setTitreTextField(titre);
            ctrl.setContenuTextArea(contenu);
            ctrl.setCategorieTextField(categorie);
            ctrl.setStatutTextField(statut);
            ctrl.setDateLabel(LocalDateTime.now().toString());
            titreTextField.getScene().setRoot(root);
        }
        catch (IOException e) {
            System.out.println("Erreur navigation : " + e.getMessage());
        }
    }

    @FXML
    void voirListeAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ListeForum.fxml"));
            titreTextField.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    @FXML
    void annulerAction(ActionEvent event) {
        titreTextField.clear();
        contenuTextArea.clear();
        categorieComboBox.setValue("discussion");
        statutComboBox.setValue("ouvert");
        erreurLabel.setText("");
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
}