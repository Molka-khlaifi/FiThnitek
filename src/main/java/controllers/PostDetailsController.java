package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.publication;

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

    private publication currentPost;
    private String retourFxml = "/ListeForum.fxml"; // par défaut

    // ── Appelé depuis MesForumsController pour savoir où revenir ──
    public void setRetourFxml(String fxml) {
        this.retourFxml = fxml;
    }

    public void setPost(publication post) {
        this.currentPost = post;

        titreLabel.setText(post.getTitre());
        contenuLabel.setText(post.getContenu());
        categorieLabel.setText(post.getCategorie().toUpperCase());
        dateLabel.setText("📅 " + post.getDate_creation());
        vuesLabel.setText("👁 " + post.getNb_vues() + " vues");
        statutLabel.setText(post.getStatut().toUpperCase());

        if (post.getImage() != null && !post.getImage().isEmpty()) {
            // ── Avec image : image à gauche + contenu à droite ──
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
                System.out.println("Erreur chargement image : " + e.getMessage());
                imageView.setVisible(false);
                imageView.setManaged(false);
            }
        } else {
            // ── Sans image : cacher l'ImageView, le contenu prend toute la largeur ──
            imageView.setVisible(false);
            imageView.setManaged(false);
        }
    }

    @FXML
    void retourAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(retourFxml));
            titreLabel.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur retour : " + e.getMessage());
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
            titreLabel.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur commentaires : " + e.getMessage());
            messageLabel.setText("Erreur ouverture commentaires !");
        }
    }
}