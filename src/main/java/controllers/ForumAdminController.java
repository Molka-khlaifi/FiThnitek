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
import util.ModerationContenu;
import util.NavigationManager;

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

    // ───────── STATS ─────────

    private void chargerStatistiques() {
        try {
            List<publication> allPosts = forumService.getAll();
            int totalPosts = allPosts.size();
            postsCountLabel.setText("📝 " + totalPosts + " posts");
            statsLabel.setText(totalPosts + " posts");
            usersCountLabel.setText("👥 " + getTotalUsers() + " utilisateurs");
            commentsCountLabel.setText("💬 " + getTotalComments() + " commentaires");
        } catch (Exception e) {
            System.err.println("Erreur chargement statistiques: " + e.getMessage());
        }
    }

    private int getTotalUsers()    { return 0; }
    private int getTotalComments() { return 0; }

    // ───────── CHARGEMENT ─────────

    private void loadPosts() {
        currentView = "normal";
        chargerpublications();
    }

    private void chargerpublications() {
        publicationList = forumService.getAll();
        publicationList.sort((p1, p2) -> Boolean.compare(p2.isEpingle(), p1.isEpingle()));
        afficherFeed(publicationList);
        statsLabel.setText(publicationList.size() + " posts");
        postsCountLabel.setText("📝 " + publicationList.size() + " posts");
    }

    private void afficherFeed(List<publication> list) {
        feedContainer.getChildren().clear();
        if (list == null || list.isEmpty()) {
            Label empty = new Label("Aucun post à afficher.");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 14px; -fx-padding: 20;");
            feedContainer.getChildren().add(empty);
            return;
        }
        for (publication post : list) {
            feedContainer.getChildren().add(createPostCard(post));
        }
    }

    // ───────── CARD ─────────

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

        // ── HEADER ──
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
            Label badge = new Label("🗑 Supprimé");
            badge.setStyle("-fx-background-color: #FFBBBB; -fx-text-fill: #8B0000; -fx-padding: 3 8; -fx-background-radius: 10; -fx-font-size: 11px; -fx-font-weight: bold;");
            header.getChildren().add(badge);
        } else if (isMasque) {
            Label badge = new Label("🚫 Masqué");
            badge.setStyle("-fx-background-color: #DDD0FF; -fx-text-fill: #4B0082; -fx-padding: 3 8; -fx-background-radius: 10; -fx-font-size: 11px; -fx-font-weight: bold;");
            header.getChildren().add(badge);
        } else if (post.isEpingle()) {
            Label badge = new Label("📌 Épinglé");
            badge.setStyle("-fx-background-color: #FFE082; -fx-text-fill: #7A5800; -fx-padding: 3 8; -fx-background-radius: 10; -fx-font-size: 11px; -fx-font-weight: bold;");
            header.getChildren().add(badge);
        }

        // ── AUTEUR ──
        String nomAuteur = forumService.getNomAuteur(post.getAuteurId());
        Label auteurLabel = new Label("👤 " + nomAuteur);
        auteurLabel.setStyle(
                "-fx-text-fill: #7B5EA7; -fx-font-size: 12px;" +
                        "-fx-cursor: hand; -fx-underline: true;"
        );
        auteurLabel.setOnMouseClicked(e -> ouvrirProfil(post, nomAuteur));

        // ── CONTENU ──
        Label contenu = new Label(post.getContenu());
        contenu.setWrapText(true);
        contenu.setStyle("-fx-text-fill: #555; -fx-font-size: 13px;");

        // ── IMAGE ──
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

        // ── INFOS ──
        String categorie = post.getCategorie() != null ? post.getCategorie().toUpperCase() : "NON DÉFINIE";
        Label infos = new Label("📂 " + categorie + "   👁 " + post.getNb_vues() + " vues");
        infos.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");

        // ── SEPARATEUR ──
        Region sep = new Region();
        sep.setStyle("-fx-background-color: #ECECEC;");
        sep.setPrefHeight(1);

        // ── BOUTONS ──
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_RIGHT);

        if (isSupprime) {
            // Dans la corbeille : Restaurer + Supprimer définitivement
            Button btnRestaurer = makeButton("♻ Restaurer", "#2BBF8A");
            btnRestaurer.setOnAction(e -> restaurerPost(post));

            Button btnSuppDef = makeButton("💣 Supprimer définitivement", "#C0392B");
            btnSuppDef.setOnAction(e -> supprimerDefinitivement(post));

            actions.getChildren().addAll(btnRestaurer, btnSuppDef);

        } else if (isMasque) {
            // Post masqué : Démasquer + Supprimer
            Button btnDemasquer = makeButton("✅ Démasquer", "#2BBF8A");
            btnDemasquer.setOnAction(e -> demasquerPost(post));

            Button btnSupprimer = makeButton("🗑 Mettre en corbeille", "#7B5EA7");
            btnSupprimer.setOnAction(e -> envoyerCorbeille(post));

            actions.getChildren().addAll(btnDemasquer, btnSupprimer);

        } else {
            // Post normal
            Button btnSupprimer = makeButton("🗑 Corbeille", "#F5F6FA");
            btnSupprimer.setOnAction(e -> envoyerCorbeille(post));

            Button btnMasquer = makeButton("🚫 Masquer", "#F5F6FA");
            btnMasquer.setOnAction(e -> masquerPost(post));

            Button btnEpingle = makeButton(post.isEpingle() ? "📌 Désépingler" : "📌 Épingler", "#F5F6FA");
            btnEpingle.setOnAction(e -> toggleEpingle(post, btnEpingle));

            Button btnDetails = makeButton("📖 Détails", "#F5F6FA");
            btnDetails.setOnAction(e -> afficherDetailsPost(post));

            actions.getChildren().addAll(btnSupprimer, btnMasquer, btnEpingle, btnDetails);
        }

        card.getChildren().addAll(header, auteurLabel, contenu, imageView, infos, sep, actions);
        return card;
    }

    /** Crée un bouton stylisé */
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

    // ───────── ACTIONS SUR POSTS ─────────

    /** Envoie le post dans la corbeille (statut = "supprime") */
    private void envoyerCorbeille(publication post) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Mettre en corbeille");
        alert.setContentText("Ce post sera déplacé dans la corbeille. Vous pourrez le restaurer.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            forumService.changerStatut(post.getId(), "supprime");
            afficherMessage("🗑 Post \"" + post.getTitre() + "\" déplacé dans la corbeille");
            rafraichirVue();
        }
    }

    /** Suppression définitive (depuis la corbeille) */
    private void supprimerDefinitivement(publication post) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression définitive");
        alert.setHeaderText("⚠ Cette action est irréversible");
        alert.setContentText("Le post sera définitivement supprimé de la base de données.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            forumService.delete(post);
            afficherMessage("💣 Post définitivement supprimé");
            rafraichirVue();
        }
    }

    /** Restaure un post depuis la corbeille */
    private void restaurerPost(publication post) {
        forumService.changerStatut(post.getId(), "actif");
        afficherMessage("♻ Post \"" + post.getTitre() + "\" restauré avec succès");
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
            afficherMessage("🚫 Post \"" + post.getTitre() + "\" masqué");
            rafraichirVue();
        }
    }

    /** Démasque un post */
    private void demasquerPost(publication post) {
        forumService.changerStatut(post.getId(), "actif");
        afficherMessage("✅ Post \"" + post.getTitre() + "\" démasqué et remis en ligne");
        rafraichirVue();
    }

    private void toggleEpingle(publication post, Button btn) {
        forumService.toggleEpingle(post.getId());
        boolean nouvelleValeur = !post.isEpingle();
        post.setEpingle(nouvelleValeur);
        btn.setText(nouvelleValeur ? "📌 Désépingler" : "📌 Épingler");
        afficherMessage(nouvelleValeur ? "📌 Post épinglé" : "📍 Post désépinglé");
        rafraichirVue();
    }

    private void afficherDetailsPost(publication post) {
        try {
            forumService.incrementerVues(post.getId());
            publication fresh = forumService.getById(post.getId());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PostDetails.fxml"));
            Parent root = loader.load();
            PostDetailsController controller = loader.getController();
            controller.setPost(fresh);
            controller.setSource("ADMIN");
            NavigationManager.loadIntoTab("FORUM", root);
            NavigationManager.selectTab("FORUM");
            afficherMessage("📖 Affichage des détails du post");
        } catch (IOException e) {
            e.printStackTrace();
            afficherMessage("❌ Erreur lors de l'ouverture des détails");
        }
    }

    /** Rafraîchit la vue courante (corbeille / masqués / normal) */
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

    // ───────── RECHERCHE / FILTRE ─────────

    private void rechercherPosts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            loadPosts();
            return;
        }
        publicationList = forumService.rechercher(keyword);
        afficherFeed(publicationList);
        statsLabel.setText(publicationList.size() + " résultats");
        afficherMessage("🔍 Recherche : " + keyword);
    }

    private void ouvrirProfil(publication post, String nomAuteur) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfilAuteur.fxml"));
            Parent root = loader.load();
            ProfilAuteurController ctrl = loader.getController();
            ctrl.initData(post.getAuteurId(), nomAuteur, "/ForumAdmin.fxml");
            ctrl.setSource("ADMIN");
            NavigationManager.loadIntoTab("FORUM", root);
        } catch (IOException e) {
            e.printStackTrace();
            afficherMessage("❌ Erreur lors de l'ouverture du profil");
        }
    }

    // ───────── ACTIONS FXML ─────────

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
            afficherMessage("📂 Filtre : " + cat);
        }
    }

    @FXML
    void trierParDateAction(ActionEvent event) {
        publicationList = forumService.trierParDate();
        afficherFeed(publicationList);
        afficherMessage("📅 Tri par date (du plus récent)");
    }

    @FXML
    void trierParVuesAction(ActionEvent event) {
        publicationList = forumService.trierParVues();
        afficherFeed(publicationList);
        afficherMessage("👁 Tri par nombre de vues");
    }

    @FXML
    void voirPostsMasques(ActionEvent event) {
        currentView = "masque";
        publicationList = forumService.getPostsByStatut("masque");
        if (publicationList != null && !publicationList.isEmpty()) {
            afficherFeed(publicationList);
            statsLabel.setText(publicationList.size() + " posts masqués");
            afficherMessage("🚫 Affichage des posts masqués — cliquez ✅ Démasquer pour les remettre en ligne");
        } else {
            feedContainer.getChildren().clear();
            Label empty = new Label("✅ Aucun post masqué.");
            empty.setStyle("-fx-text-fill: #2BBF8A; -fx-font-size: 14px; -fx-padding: 20;");
            feedContainer.getChildren().add(empty);
            statsLabel.setText("0 posts masqués");
            afficherMessage("📭 Aucun post masqué");
        }
    }

    @FXML
    void voirPostsEpingles(ActionEvent event) {
        currentView = "normal";
        publicationList = forumService.getEpingles();
        afficherFeed(publicationList);
        statsLabel.setText(publicationList.size() + " posts épinglés");
        afficherMessage("📌 Affichage des posts épinglés");
    }

    @FXML
    void voirCorbeille(ActionEvent event) {
        currentView = "supprime";
        publicationList = forumService.getPostsByStatut("supprime");
        if (publicationList != null && !publicationList.isEmpty()) {
            afficherFeed(publicationList);
            statsLabel.setText(publicationList.size() + " posts dans la corbeille");
            afficherMessage("🗑 Corbeille — ♻ Restaurer ou 💣 Supprimer définitivement");
        } else {
            feedContainer.getChildren().clear();
            Label empty = new Label("🗑 Corbeille vide.");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 14px; -fx-padding: 20;");
            feedContainer.getChildren().add(empty);
            statsLabel.setText("Corbeille vide");
            afficherMessage("🗑 Corbeille vide");
        }
    }

    @FXML
    void ajouterAnnonceAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterForum.fxml"));
            Parent root = loader.load();
            AjouterForumController controller = loader.getController();
            controller.setSource("ADMIN");
            NavigationManager.loadIntoTab("FORUM", root);
            afficherMessage("📝 Création d'une annonce admin");
        } catch (IOException e) {
            e.printStackTrace();
            afficherMessage("❌ Erreur lors de l'ouverture");
        }
    }

    @FXML
    void openModerationAI(ActionEvent event) {
        afficherMessage("🤖 Analyse IA en cours...");
        moderationAIActive();
    }

    // ───────── IA DE MODÉRATION ─────────

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
                            ? "✅ Analyse IA terminée : aucun contenu problématique"
                            : "⚠️ Analyse IA : " + finalCompteur + " posts masqués");
                    loadPosts();
                });
                return null;
            }
        };
        task.setOnFailed(e -> afficherMessage("❌ Erreur lors de l'analyse IA"));
        new Thread(task).start();
    }
}