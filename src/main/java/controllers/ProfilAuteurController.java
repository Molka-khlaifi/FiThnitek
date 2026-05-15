package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import models.publication;
import services.forumService;
import services.NavigationManager;

import java.util.List;

public class ProfilAuteurController {

    @FXML private Label nomAuteurLabel;
    @FXML private Label initialesLabel;
    @FXML private Label emailLabel;
    @FXML private Label nbPostsLabel;
    @FXML private VBox postsContainer;

    private forumService forumService = new forumService();
    private String retourFxml = "/ListeForum.fxml";

    // âœ… AJOUT : Variable pour la source
    private String source = "CONDUCTEUR";

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€ INIT â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public void initData(int auteurId, String nomAuteur, String retourFxml) {
        this.retourFxml = retourFxml;
        initData(auteurId, nomAuteur);
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void initData(int auteurId, String nomAuteur) {

        // Nom
        nomAuteurLabel.setText(nomAuteur);

        // Initiales avatar
        String[] parts = nomAuteur.trim().split(" ");
        String initiales;
        if (parts.length >= 2) {
            initiales = String.valueOf(parts[0].charAt(0)) +
                    String.valueOf(parts[1].charAt(0));
        } else {
            initiales = nomAuteur.substring(0, Math.min(2, nomAuteur.length()));
        }
        initialesLabel.setText(initiales.toUpperCase());

        // Posts de l'auteur
        List<publication> posts = forumService.getByAuteur(auteurId);
        nbPostsLabel.setText(posts.size() + " publication(s)");

        // Afficher les posts
        afficherPosts(posts);
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€ AFFICHAGE POSTS â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private void afficherPosts(List<publication> posts) {
        postsContainer.getChildren().clear();

        if (posts.isEmpty()) {
            Label vide = new Label("Cet auteur n'a pas encore publiÃ© de post.");
            vide.setStyle(
                    "-fx-text-fill: #888;" +
                            "-fx-font-size: 13px;" +
                            "-fx-padding: 20;"
            );
            postsContainer.getChildren().add(vide);
            return;
        }

        for (publication post : posts) {

            VBox card = new VBox(8);
            card.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-padding: 14 16;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-radius: 10;" +
                            "-fx-border-color: #DDE2F0;" +
                            "-fx-border-width: 1;"
            );

            // â”€â”€ Titre + badge catÃ©gorie â”€â”€
            HBox headerCard = new HBox(10);
            headerCard.setStyle("-fx-alignment: CENTER_LEFT;");

            Label titre = new Label(post.getTitre());
            titre.setStyle(
                    "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #2D2D6B;"
            );
            titre.setWrapText(true);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            String cat = post.getCategorie() != null
                    ? post.getCategorie().toUpperCase()
                    : "N/A";
            Label categorie = new Label(cat);
            categorie.setStyle(
                    "-fx-background-color: #2BBF8A;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 4;" +
                            "-fx-padding: 2 8;" +
                            "-fx-font-size: 10px;" +
                            "-fx-font-weight: bold;"
            );

            headerCard.getChildren().addAll(titre, spacer, categorie);

            // â”€â”€ AperÃ§u contenu â”€â”€
            String apercu = post.getContenu() != null && post.getContenu().length() > 120
                    ? post.getContenu().substring(0, 120) + "..."
                    : post.getContenu();
            Label contenu = new Label(apercu);
            contenu.setWrapText(true);
            contenu.setStyle("-fx-text-fill: #555; -fx-font-size: 12px;");

            // â”€â”€ Vues + date â”€â”€
            HBox footer = new HBox(16);
            footer.setStyle("-fx-alignment: CENTER_LEFT;");

            Label vues = new Label("ðŸ‘ " + post.getNb_vues() + " vues");
            vues.setStyle("-fx-text-fill: #aaa; -fx-font-size: 11px;");

            Label date = new Label("ðŸ“… " + (post.getDate_creation() != null
                    ? post.getDate_creation().toString()
                    : ""));
            date.setStyle("-fx-text-fill: #aaa; -fx-font-size: 11px;");

            footer.getChildren().addAll(vues, date);

            card.getChildren().addAll(headerCard, contenu, footer);
            postsContainer.getChildren().add(card);
        }
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€ RETOUR  â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @FXML
    void retourAction() {
        try {
            NavigationManager.navigateFrom(nomAuteurLabel, "ADMIN".equals(source) ? "/ForumAdmin.FXML" : retourFxml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

