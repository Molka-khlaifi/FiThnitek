package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import services.NavigationManager;

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
        try {
            NavigationManager.navigateFrom(titreTextField, "ADMIN".equals(source) ? "/ForumAdmin.FXML" : "/ListeForum.fxml");
        } catch (IOException e) {
            System.out.println("Erreur retour forum : " + e.getMessage());
        }
    }

    @FXML
    void retourAjouterAction(ActionEvent event) {
        try {
            NavigationManager.navigateFrom(titreTextField, "ADMIN".equals(source) ? "/ForumAdmin.FXML" : "/ListeForum.fxml");
        } catch (IOException e) {
            System.out.println("Erreur retour forum : " + e.getMessage());
        }
    }

    @FXML
    void voirCommentairesAction(ActionEvent event) {
        try {
            NavigationManager.navigateFrom(titreTextField, "/CommentaireForum.fxml",
                    (CommentaireForumController ctrl) -> ctrl.initData(forumId, titreTextField.getText()));
        } catch (IOException e) {
            System.out.println("Erreur navigation commentaires : " + e.getMessage());
        }
    }
}

