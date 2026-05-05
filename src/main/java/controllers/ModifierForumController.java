package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import models.publication;
import services.forumService;

import java.io.IOException;
import java.sql.SQLException;

public class ModifierForumController {

    @FXML private TextField        idTextField;
    @FXML private TextField        titreTextField;
    @FXML private TextArea         contenuTextArea;
    @FXML private ComboBox<String> categorieComboBox;
    @FXML private ComboBox<String> statutComboBox;
    @FXML private Label            erreurLabel;

    private publication       forumAModifier;
    private forumService forumService = new forumService();

    @FXML
    public void initialize() {
        categorieComboBox.getItems().addAll("annonce", "question", "discussion");
        statutComboBox.getItems().addAll("ouvert", "ferme");
    }

    // ─── Recevoir le forum depuis ListeForumController ────────────────────
    public void initData(publication forum) {
        this.forumAModifier = forum;
        idTextField.setText(String.valueOf(forum.getId()));
        titreTextField.setText(forum.getTitre());
        contenuTextArea.setText(forum.getContenu());
        categorieComboBox.setValue(forum.getCategorie());
        statutComboBox.setValue(forum.getStatut());
    }

    @FXML
    void modifierForumAction(ActionEvent event) {
        erreurLabel.setText("");
        String titre    = titreTextField.getText().trim();
        String contenu  = contenuTextArea.getText().trim();
        String categorie = categorieComboBox.getValue();
        String statut   = statutComboBox.getValue();

        // Validation
        if (titre.isEmpty()) {
            erreurLabel.setText("Le titre est obligatoire !");
            return;
        }
        if (titre.length() < 5) {
            erreurLabel.setText("Le titre doit avoir au moins 5 caractères !");
            return;
        }
        if (contenu.isEmpty()) {
            erreurLabel.setText("Le contenu est obligatoire !");
            return;
        }

        forumAModifier.setTitre(titre);
        forumAModifier.setContenu(contenu);
        forumAModifier.setCategorie(categorie);
        forumAModifier.setStatut(statut);

        try {
            forumService.update(forumAModifier);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Post modifié avec succès !");
            alert.show();
            Parent root = FXMLLoader.load(getClass().getResource("/ListeForum.fxml"));
            titreTextField.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur navigation : " + e.getMessage());
        }
    }

    @FXML
    void annulerAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ListeForum.fxml"));
            titreTextField.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}