package controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import models.publication;
import services.forumService;
import services.ModerationContenu;
import services.NavigationManager;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ForumAdminController {

    @FXML private VBox feedContainer;
    @FXML private TextField searchTextField;
    @FXML private ComboBox<String> categorieFilterComboBox;
    @FXML private Label statsLabel;
    @FXML private Label usersCountLabel;
    @FXML private Label postsCountLabel;
    @FXML private Label commentsCountLabel;
    @FXML private Label messageLabel;

    private forumService forumService = new forumService();
    private List<publication> publicationList;
    private String currentFilter = "Tous";

    // Mode actuel d'affichage : "normal", "masque", "supprime"
    private String currentView = "normal";

    @FXML
    public void initialize() {
        categorieFilterComboBox.getItems().addAll("Tous", "question", "discussion", "autre");
        categorieFilterComboBox.setValue("Tous");
        categorieFilterComboBox.setOnAction(this::filtrerCategorieAction);

        searchTextField.textProperty().addListener((obs, oldVal, newVal) -> rechercherPosts(newVal));

        chargerStatistiques();
        loadPosts();
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€ STATS â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private void chargerStatistiques() {
        try {
            List<publication> allPosts = forumService.getAll();
            int totalPosts = allPosts.size();
            postsCountLabel.setText("ðŸ“ " + totalPosts + " posts");
            statsLabel.setText(totalPosts + " posts");
            usersCountLabel.setText("ðŸ‘¥ " + getTotalUsers() + " utilisateurs");
            commentsCountLabel.setText("ðŸ’¬ " + getTotalComments() + " commentaires");
        } catch (Exception e) {
            System.err.println("Erreur chargement statistiques: " + e.getMessage());
        }
    }

    private int getTotalUsers()    { return 0; }
    private int getTotalComments() { return 0; }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€ CHARGEMENT â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private void loadPosts() {
        currentView = "normal";
        chargerpublications();
    }

    private void chargerpublications() {
        publicationList = forumService.getAll();
        publicationList.sort((p1, p2) -> Boolean.compare(p2.isEpingle(), p1.isEpingle()));
        afficherFeed(publicationList);
        statsLabel.setText(publicationList.size() + " posts");
        postsCountLabel.setText("ðŸ“ " + publicationList.size() + " posts");
    }

    private void afficherFeed(List<publication> list) {
        feedContainer.getChildren().clear();
        if (list == null || list.isEmpty()) {
            Label empty = new Label("Aucun post Ã  afficher.");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 14px; -fx-padding: 20;");
            feedContainer.getChildren().add(empty);
            return;
        }
        for (publication post : list) {
            feedContainer.getChildren().add(createPostCard(post));
        }
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€ CARD â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private VBox createPostCard(publication post) {
        boolean isMasque   = "masque".equalsIgnoreCase(post.getStatut());
        boolean isSupprime = "supprime".equalsIgnoreCase(post.getStatut());

        // Couleur de fond selon statut
        String bgColor = isSupprime ? "#FFF0F0"
                : isMasque   ? "#F5F0FF"
                : "white";
        String borderColor = isSupprime ? "#FFBBBB"
                : isMasque   ? "#C9BFFF"
                : "#DDE6ED";

        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-padding: 15;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: " + borderColor + ";" +
                        "-fx-border-width: 1.5;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 8, 0, 0, 2);"
        );

        // â”€â”€ HEADER â”€â”€
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label titre = new Label(post.getTitre());
        titre.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #1E1E5E;");
        header.getChildren().add(titre);

        Region spacerH = new Region();
        HBox.setHgrow(spacerH, Priority.ALWAYS);
        header.getChildren().add(spacerH);

        // Badge statut
        if (isSupprime) {
            Label badge = new Label("ðŸ—‘ SupprimÃ©");
            badge.setStyle("-fx-background-color: #FFBBBB; -fx-text-fill: #8B0000; -fx-padding: 3 8; -fx-background-radius: 10; -fx-font-size: 11px; -fx-font-weight: bold;");
            header.getChildren().add(badge);
        } else if (isMasque) {
            Label badge = new Label("ðŸš« MasquÃ©");
            badge.setStyle("-fx-background-color: #DDD0FF; -fx-text-fill: #4B0082; -fx-padding: 3 8; -fx-background-radius: 10; -fx-font-size: 11px; -fx-font-weight: bold;");
            header.getChildren().add(badge);
        } else if (post.isEpingle()) {
            Label badge = new Label("ðŸ“Œ Ã‰pinglÃ©");
            badge.setStyle("-fx-background-color: #FFE082; -fx-text-fill: #7A5800; -fx-padding: 3 8; -fx-background-radius: 10; -fx-font-size: 11px; -fx-font-weight: bold;");
            header.getChildren().add(badge);
        }

        // â”€â”€ AUTEUR â”€â”€
        String nomAuteur = forumService.getNomAuteur(post.getAuteurId());
        Label auteurLabel = new Label("ðŸ‘¤ " + nomAuteur);
        auteurLabel.setStyle(
                "-fx-text-fill: #7B5EA7; -fx-font-size: 12px;" +
                        "-fx-cursor: hand; -fx-underline: true;"
        );
        auteurLabel.setOnMouseClicked(e -> ouvrirProfil(post, nomAuteur));

        // â”€â”€ CONTENU â”€â”€
        Label contenu = new Label(post.getContenu());
        contenu.setWrapText(true);
        contenu.setStyle("-fx-text-fill: #555; -fx-font-size: 13px;");

        // â”€â”€ IMAGE â”€â”€
        ImageView imageView = new ImageView();
        if (post.getImage() != null && !post.getImage().trim().isEmpty()) {
            try {
                imageView.setImage(new Image("file:" + post.getImage()));
                imageView.setFitWidth(480);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setStyle("-fx-background-radius: 8;");
            } catch (Exception e) {
                imageView.setVisible(false);
                imageView.setManaged(false);
            }
        } else {
            imageView.setVisible(false);
            imageView.setManaged(false);
        }

        // â”€â”€ INFOS â”€â”€
        String categorie = post.getCategorie() != null ? post.getCategorie().toUpperCase() : "NON DÃ‰FINIE";
        Label infos = new Label("ðŸ“‚ " + categorie + "   ðŸ‘ " + post.getNb_vues() + " vues");
        infos.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");

        // â”€â”€ SEPARATEUR â”€â”€
        Region sep = new Region();
        sep.setStyle("-fx-background-color: #ECECEC;");
        sep.setPrefHeight(1);

        // â”€â”€ BOUTONS â”€â”€
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_RIGHT);

        if (isSupprime) {
            // Dans la corbeille : Restaurer + Supprimer dÃ©finitivement
            Button btnRestaurer = makeButton("â™» Restaurer", "#2BBF8A");
            btnRestaurer.setOnAction(e -> restaurerPost(post));

            Button btnSuppDef = makeButton("ðŸ’£ Supprimer dÃ©finitivement", "#C0392B");
            btnSuppDef.setOnAction(e -> supprimerDefinitivement(post));

            actions.getChildren().addAll(btnRestaurer, btnSuppDef);

        } else if (isMasque) {
            // Post masquÃ© : DÃ©masquer + Supprimer
            Button btnDemasquer = makeButton("âœ… DÃ©masquer", "#2BBF8A");
            btnDemasquer.setOnAction(e -> demasquerPost(post));

            Button btnSupprimer = makeButton("ðŸ—‘ Mettre en corbeille", "#7B5EA7");
            btnSupprimer.setOnAction(e -> envoyerCorbeille(post));

            actions.getChildren().addAll(btnDemasquer, btnSupprimer);

        } else {
            // Post normal
            Button btnSupprimer = makeButton("ðŸ—‘ Corbeille", "#F5F6FA");
            btnSupprimer.setOnAction(e -> envoyerCorbeille(post));

            Button btnMasquer = makeButton("ðŸš« Masquer", "#F5F6FA");
            btnMasquer.setOnAction(e -> masquerPost(post));

            Button btnEpingle = makeButton(post.isEpingle() ? "ðŸ“Œ DÃ©sÃ©pingler" : "ðŸ“Œ Ã‰pingler", "#F5F6FA");
            btnEpingle.setOnAction(e -> toggleEpingle(post, btnEpingle));

            Button btnDetails = makeButton("ðŸ“– DÃ©tails", "#F5F6FA");
            btnDetails.setOnAction(e -> afficherDetailsPost(post));

            actions.getChildren().addAll(btnSupprimer, btnMasquer, btnEpingle, btnDetails);
        }

        card.getChildren().addAll(header, auteurLabel, contenu, imageView, infos, sep, actions);
        return card;
    }

    /** CrÃ©e un bouton stylisÃ© */
    private Button makeButton(String text, String bgColor) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-text-fill: #444;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 12px;" +
                        "-fx-padding: 5 12;"
        );
        // Effet hover
        btn.setOnMouseEntered(e -> btn.setOpacity(0.85));
        btn.setOnMouseExited(e  -> btn.setOpacity(1.0));
        return btn;
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€ ACTIONS SUR POSTS â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /** Envoie le post dans la corbeille (statut = "supprime") */
    private void envoyerCorbeille(publication post) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Mettre en corbeille");
        alert.setContentText("Ce post sera dÃ©placÃ© dans la corbeille. Vous pourrez le restaurer.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            forumService.changerStatut(post.getId(), "supprime");
            afficherMessage("ðŸ—‘ Post \"" + post.getTitre() + "\" dÃ©placÃ© dans la corbeille");
            rafraichirVue();
        }
    }

    /** Suppression dÃ©finitive (depuis la corbeille) */
    private void supprimerDefinitivement(publication post) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression dÃ©finitive");
        alert.setHeaderText("âš  Cette action est irrÃ©versible");
        alert.setContentText("Le post sera dÃ©finitivement supprimÃ© de la base de donnÃ©es.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            forumService.delete(post);
            afficherMessage("ðŸ’£ Post dÃ©finitivement supprimÃ©");
            rafraichirVue();
        }
    }

    /** Restaure un post depuis la corbeille */
    private void restaurerPost(publication post) {
        forumService.changerStatut(post.getId(), "actif");
        afficherMessage("â™» Post \"" + post.getTitre() + "\" restaurÃ© avec succÃ¨s");
        rafraichirVue();
    }

    /** Masque un post */
    private void masquerPost(publication post) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Masquer le post");
        alert.setContentText("Ce post ne sera plus visible par les utilisateurs.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            forumService.masquerPost(post.getId());
            afficherMessage("ðŸš« Post \"" + post.getTitre() + "\" masquÃ©");
            rafraichirVue();
        }
    }

    /** DÃ©masque un post */
    private void demasquerPost(publication post) {
        forumService.changerStatut(post.getId(), "actif");
        afficherMessage("âœ… Post \"" + post.getTitre() + "\" dÃ©masquÃ© et remis en ligne");
        rafraichirVue();
    }

    private void toggleEpingle(publication post, Button btn) {
        forumService.toggleEpingle(post.getId());
        boolean nouvelleValeur = !post.isEpingle();
        post.setEpingle(nouvelleValeur);
        btn.setText(nouvelleValeur ? "ðŸ“Œ DÃ©sÃ©pingler" : "ðŸ“Œ Ã‰pingler");
        afficherMessage(nouvelleValeur ? "ðŸ“Œ Post Ã©pinglÃ©" : "ðŸ“ Post dÃ©sÃ©pinglÃ©");
        rafraichirVue();
    }

    private void afficherDetailsPost(publication post) {
        try {
            forumService.incrementerVues(post.getId());
            publication fresh = forumService.getById(post.getId());
            NavigationManager.navigateFrom(messageLabel, "/PostDetails.FXML", (PostDetailsController controller) -> {
                controller.setPost(fresh);
                controller.setSource("ADMIN");
            });
            afficherMessage("Affichage des details du post");
        } catch (IOException e) {
            e.printStackTrace();
            afficherMessage("Erreur lors de l ouverture des details");
        }
    }

    /** RafraÃ®chit la vue courante (corbeille / masquÃ©s / normal) */
    private void rafraichirVue() {
        switch (currentView) {
            case "masque"   -> voirPostsMasques(null);
            case "supprime" -> voirCorbeille(null);
            default         -> { loadPosts(); chargerStatistiques(); }
        }
    }

    private void afficherMessage(String msg) {
        messageLabel.setText(msg);
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€ RECHERCHE / FILTRE â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private void rechercherPosts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            loadPosts();
            return;
        }
        publicationList = forumService.rechercher(keyword);
        afficherFeed(publicationList);
        statsLabel.setText(publicationList.size() + " rÃ©sultats");
        afficherMessage("ðŸ” Recherche : " + keyword);
    }

    private void ouvrirProfil(publication post, String nomAuteur) {
        try {
            NavigationManager.navigateFrom(messageLabel, "/ProfilAuteur.fxml", (ProfilAuteurController ctrl) -> {
                ctrl.initData(post.getAuteurId(), nomAuteur, "/ForumAdmin.FXML");
                ctrl.setSource("ADMIN");
            });
        } catch (IOException e) {
            e.printStackTrace();
            afficherMessage("Erreur lors de l ouverture du profil");
        }
    }

    // ACTIONS FXML â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @FXML
    void rechercherAction(ActionEvent event) {
        rechercherPosts(searchTextField.getText());
    }

    @FXML
    void filtrerCategorieAction(ActionEvent event) {
        String cat = categorieFilterComboBox.getValue();
        currentFilter = cat;
        if (cat == null || cat.equals("Tous")) {
            loadPosts();
        } else {
            publicationList = forumService.getByCategorie(cat);
            afficherFeed(publicationList);
            statsLabel.setText(publicationList.size() + " posts");
            afficherMessage("ðŸ“‚ Filtre : " + cat);
        }
    }

    @FXML
    void trierParDateAction(ActionEvent event) {
        publicationList = forumService.trierParDate();
        afficherFeed(publicationList);
        afficherMessage("ðŸ“… Tri par date (du plus rÃ©cent)");
    }

    @FXML
    void trierParVuesAction(ActionEvent event) {
        publicationList = forumService.trierParVues();
        afficherFeed(publicationList);
        afficherMessage("ðŸ‘ Tri par nombre de vues");
    }

    @FXML
    void voirPostsMasques(ActionEvent event) {
        currentView = "masque";
        publicationList = forumService.getPostsByStatut("masque");
        if (publicationList != null && !publicationList.isEmpty()) {
            afficherFeed(publicationList);
            statsLabel.setText(publicationList.size() + " posts masquÃ©s");
            afficherMessage("ðŸš« Affichage des posts masquÃ©s â€” cliquez âœ… DÃ©masquer pour les remettre en ligne");
        } else {
            feedContainer.getChildren().clear();
            Label empty = new Label("âœ… Aucun post masquÃ©.");
            empty.setStyle("-fx-text-fill: #2BBF8A; -fx-font-size: 14px; -fx-padding: 20;");
            feedContainer.getChildren().add(empty);
            statsLabel.setText("0 posts masquÃ©s");
            afficherMessage("ðŸ“­ Aucun post masquÃ©");
        }
    }

    @FXML
    void voirPostsEpingles(ActionEvent event) {
        currentView = "normal";
        publicationList = forumService.getEpingles();
        afficherFeed(publicationList);
        statsLabel.setText(publicationList.size() + " posts Ã©pinglÃ©s");
        afficherMessage("ðŸ“Œ Affichage des posts Ã©pinglÃ©s");
    }

    @FXML
    void voirCorbeille(ActionEvent event) {
        currentView = "supprime";
        publicationList = forumService.getPostsByStatut("supprime");
        if (publicationList != null && !publicationList.isEmpty()) {
            afficherFeed(publicationList);
            statsLabel.setText(publicationList.size() + " posts dans la corbeille");
            afficherMessage("ðŸ—‘ Corbeille â€” â™» Restaurer ou ðŸ’£ Supprimer dÃ©finitivement");
        } else {
            feedContainer.getChildren().clear();
            Label empty = new Label("ðŸ—‘ Corbeille vide.");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 14px; -fx-padding: 20;");
            feedContainer.getChildren().add(empty);
            statsLabel.setText("Corbeille vide");
            afficherMessage("ðŸ—‘ Corbeille vide");
        }
    }

    @FXML
    void ajouterAnnonceAction(ActionEvent event) {
        try {
            NavigationManager.navigateFrom(messageLabel, "/AjouterForum.fxml", (AjouterForumController controller) ->
                    controller.setSource("ADMIN"));
            afficherMessage("Creation d une annonce admin");
        } catch (IOException e) {
            e.printStackTrace();
            afficherMessage("Erreur lors de l ouverture");
        }
    }

    @FXML
    void openModerationAI(ActionEvent event) {
        afficherMessage("ðŸ¤– Analyse IA en cours...");
        moderationAIActive();
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€ IA DE MODÃ‰RATION â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private void moderationAIActive() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<publication> allPosts = forumService.getAll();
                int compteur = 0;
                for (publication post : allPosts) {
                    String titreModere   = ModerationContenu.moderer(post.getTitre());
                    String contenuModere = ModerationContenu.moderer(post.getContenu());
                    if (titreModere == null || contenuModere == null) {
                        compteur++;
                        Platform.runLater(() -> forumService.masquerPost(post.getId()));
                    }
                    Thread.sleep(50);
                }
                final int finalCompteur = compteur;
                Platform.runLater(() -> {
                    afficherMessage(finalCompteur == 0
                            ? "âœ… Analyse IA terminÃ©e : aucun contenu problÃ©matique"
                            : "âš ï¸ Analyse IA : " + finalCompteur + " posts masquÃ©s");
                    loadPosts();
                });
                return null;
            }
        };
        task.setOnFailed(e -> afficherMessage("âŒ Erreur lors de l'analyse IA"));
        new Thread(task).start();
    }
}

