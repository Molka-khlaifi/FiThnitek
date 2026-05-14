package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.publication;
import util.NavigationManager;

import java.io.IOException;

public class PostDetailsController {

    @FXML private ImageView imageView;
    @FXML private Label titreLabel;
    @FXML private Label contenuLabel;
    @FXML private Label categorieLabel;
    @FXML private Label dateLabel;
    @FXML private Label vuesLabel;
    @FXML private Label statutLabel;
    @FXML private Label messageLabel;
    private String source = "CONDUCTEUR";

    public void setSource(String source) {
        this.source = source;
    }

    private publication currentPost;
    private String retourFxml = "/ListeForum.fxml";

    public void setRetourFxml(String fxml) { this.retourFxml = fxml; }

    public void setPost(publication post) {
        this.currentPost = post;
        titreLabel.setText(post.getTitre());
        contenuLabel.setText(post.getContenu());
        categorieLabel.setText(post.getCategorie().toUpperCase());
        dateLabel.setText("📅 " + post.getDate_creation());
        vuesLabel.setText("👁 " + post.getNb_vues() + " vues");
        statutLabel.setText(post.getStatut().toUpperCase());

        if (post.getImage() != null && !post.getImage().isEmpty()) {
            try {
                Image img;
                if (post.getImage().startsWith("C:") || post.getImage().startsWith("/")) {
                    img = new Image("file:" + post.getImage());
                } else {
                    img = new Image(getClass().getResourceAsStream("/" + post.getImage()));
                }
                imageView.setImage(img);
                imageView.setVisible(true);
                imageView.setManaged(true);
            } catch (Exception e) {
                imageView.setVisible(false);
                imageView.setManaged(false);
            }
        } else {
            imageView.setVisible(false);
            imageView.setManaged(false);
        }
    }

    @FXML
    void retourAction() {
        if ("ADMIN".equals(source)) {
            NavigationManager.navigateInTab("FORUM", "/ForumAdmin.fxml");
        } else {
            NavigationManager.navigateInTab("FORUM", "/listeForum.fxml");
        }
    }

    @FXML
    void voirCommentairesAction(ActionEvent event) {
        if (currentPost == null) {
            messageLabel.setText("Aucun post sélectionné !");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentaireForum.fxml"));
            Parent root = loader.load();
            CommentaireForumController ctrl = loader.getController();
            ctrl.initData(currentPost.getId(), currentPost.getTitre());
            ctrl.setRetourFxml(retourFxml);
            // ✅ Rester dans le conteneur FORUM
            NavigationManager.loadIntoTab("FORUM", root);
        } catch (IOException e) {
            System.out.println("Erreur commentaires : " + e.getMessage());
            messageLabel.setText("Erreur ouverture commentaires !");
        }
    }
}