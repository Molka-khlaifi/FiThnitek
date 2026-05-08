package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

public class DetailForumController {

    @FXML private TextField titreTextField;
    @FXML private TextArea  contenuTextArea;
    @FXML private TextField categorieTextField;
    @FXML private TextField statutTextField;
    @FXML private Label     dateLabel;

    private int forumId; // pour naviguer vers commentaires

    // ─── Setters appelés depuis AjouterForumController ───────────────────
    public void setTitreTextField(String titre) {
        titreTextField.setText(titre);
    }
    public void setContenuTextArea(String contenu) {
        contenuTextArea.setText(contenu);
    }
    public void setCategorieTextField(String categorie) {
        categorieTextField.setText(categorie);
    }
    public void setStatutTextField(String statut) {
        statutTextField.setText(statut);
    }
    public void setDateLabel(String date) {
        dateLabel.setText(date);
    }
    public void setForumId(int id) {
        this.forumId = id;
    }

    // ─── Retour à la liste ────────────────────────────────────────────────
    @FXML
    void retourListeAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ListeForum.fxml"));
            titreTextField.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // ─── Ajouter un autre post ────────────────────────────────────────────
    @FXML
    void retourAjouterAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterForum.fxml"));
            titreTextField.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // ─── Voir commentaires de ce post ────────────────────────────────────
    @FXML
    void voirCommentairesAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentaireForum.fxml"));
            Parent root = loader.load();
            CommentaireForumController ctrl = loader.getController();
            ctrl.initData(forumId, titreTextField.getText());
            titreTextField.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur navigation commentaires : " + e.getMessage());
        }
    }
}

