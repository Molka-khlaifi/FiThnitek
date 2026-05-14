package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import util.NavigationManager;

import java.io.IOException;

public class DetailForumController {

    @FXML private TextField titreTextField;
    @FXML private TextArea  contenuTextArea;
    @FXML private TextField categorieTextField;
    @FXML private TextField statutTextField;
    @FXML private Label     dateLabel;
    private String source = "CONDUCTEUR";

    public void setSource(String source) {
        this.source = source;
    }

    private int forumId;

    public void setTitreTextField(String titre)       { titreTextField.setText(titre);       }
    public void setContenuTextArea(String contenu)    { contenuTextArea.setText(contenu);     }
    public void setCategorieTextField(String cat)     { categorieTextField.setText(cat);      }
    public void setStatutTextField(String statut)     { statutTextField.setText(statut);      }
    public void setDateLabel(String date)             { dateLabel.setText(date);              }
    public void setForumId(int id)                    { this.forumId = id;                    }

    @FXML
    void retourListeAction(ActionEvent event) {
        if ("ADMIN".equals(source)) {
            NavigationManager.navigateInTab("FORUM", "/ForumAdmin.fxml");
        } else {
            NavigationManager.navigateInTab("FORUM", "/ListeForum.fxml");
        }    }

    @FXML
    void retourAjouterAction(ActionEvent event) {
        if ("ADMIN".equals(source)) {
            NavigationManager.navigateInTab("FORUM", "/ForumAdmin.fxml");
        } else {
            NavigationManager.navigateInTab("FORUM", "/ListeForum.fxml");
        }
    }

    @FXML
    void voirCommentairesAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentaireForum.fxml"));
            Parent root = loader.load();
            CommentaireForumController ctrl = loader.getController();
            ctrl.initData(forumId, titreTextField.getText());
            // ✅ Rester dans le conteneur FORUM
            NavigationManager.loadIntoTab("FORUM", root);
        } catch (IOException e) {
            System.out.println("Erreur navigation commentaires : " + e.getMessage());
        }
    }
}