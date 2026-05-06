package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import models.publication;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.awt.*;
import java.io.IOException;

public class PostDetailsController {

    @FXML private ImageView imageView;
    @FXML private Label titreLabel;
    @FXML private Label contenuLabel;
    @FXML private Label categorieLabel;
    @FXML private Label dateLabel;
    @FXML private Label vuesLabel;

    private publication currentPost;

    public void setPost(publication post) {
        this.currentPost = post;

        titreLabel.setText(post.getTitre());
        contenuLabel.setText(post.getContenu());
        categorieLabel.setText("Catégorie : " + post.getCategorie());
        dateLabel.setText("Date : " + post.getDate_creation());
        vuesLabel.setText("Vues : " + post.getNb_vues());
        if (post.getImage() != null && !post.getImage().isEmpty()) {
            try {
                Image img;

                // ✔ si chemin local (C:\...)
                if (post.getImage().startsWith("C:") || post.getImage().startsWith("/")) {
                    img = new Image("file:" + post.getImage());
                }
                // ✔ si image dans resources
                else {
                    img = new Image(getClass().getResourceAsStream("/" + post.getImage()));
                }

                imageView.setImage(img);

            } catch (Exception e) {
                System.out.println("Erreur chargement image : " + e.getMessage());
            }
        }
    }

    @FXML
    void retourAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ListeForum.fxml"));
            titreLabel.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur retour : " + e.getMessage());
        }
    }
}