package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import models.publication;
import services.forumService;

import java.io.IOException;
import java.util.List;

public class MesForumsController {

    @FXML
    private TextField idTextField;

    @FXML
    private Label statsLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private VBox feedContainer;

    private final forumService forumService = new forumService();

    private List<publication> mesPosts;

    // ───────────────── INIT ─────────────────

    @FXML
    public void initialize() {
        chargerMesPosts();
    }

    // ───────────────── CHARGER POSTS ─────────────────

    private void chargerMesPosts() {

        mesPosts = forumService.getAll();

        // Trier les posts épinglés en premier
        mesPosts.sort((p1, p2) ->
                Boolean.compare(p2.isEpingle(), p1.isEpingle())
        );

        afficherFeed(mesPosts);

        statsLabel.setText(mesPosts.size() + " posts");
    }

    // ───────────────── FEED ─────────────────

    private void afficherFeed(List<publication> list) {

        feedContainer.getChildren().clear();

        for (publication post : list) {

            VBox card = new VBox(10);

            card.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-padding: 15;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-radius: 12;" +
                            "-fx-border-color: #DDE6ED;"
            );

            // ───── HEADER ─────

            HBox header = new HBox(10);

            Label titre = new Label(post.getTitre());

            titre.setStyle(
                    "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #085041;"
            );

            header.getChildren().add(titre);

            // Badge épinglé
            if (post.isEpingle()) {

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Label epingle = new Label("📌 Épinglé");

                epingle.setStyle(
                        "-fx-background-color: #FFE082;" +
                                "-fx-padding: 4 8;" +
                                "-fx-background-radius: 10;" +
                                "-fx-font-size: 11px;"
                );

                header.getChildren().addAll(spacer, epingle);
            }

            // ───── CONTENU ─────

            Label contenu = new Label(post.getContenu());

            contenu.setWrapText(true);

            contenu.setStyle(
                    "-fx-text-fill: #444;" +
                            "-fx-font-size: 13px;"
            );

            // ───── INFOS ─────

            Label infos = new Label(
                    "📂 " + post.getCategorie().toUpperCase()
                            + "   |   👁 " + post.getNb_vues() + " vues"
            );

            infos.setStyle(
                    "-fx-text-fill: #777;" +
                            "-fx-font-size: 12px;"
            );

            // ───── BOUTONS ─────

            Button detailsBtn = new Button("Voir détails");

            detailsBtn.setStyle(
                    "-fx-background-color: #0f87cc;" +
                            "-fx-text-fill: white;"
            );

            detailsBtn.setOnAction(e -> afficherDetailsPost(post));

            Button commentairesBtn = new Button("💬 Commentaires");

            commentairesBtn.setStyle(
                    "-fx-background-color: #4bcad6;" +
                            "-fx-text-fill: white;"
            );

            commentairesBtn.setOnAction(e -> ouvrirCommentaires(post));

            HBox actions = new HBox(10, detailsBtn, commentairesBtn);

            // ───── CLICK SUR CARD ─────

            card.setOnMouseClicked(event -> {

                // simple clic
                if (event.getClickCount() == 1) {
                    idTextField.setText(String.valueOf(post.getId()));
                }

                // double clic
                if (event.getClickCount() == 2) {
                    afficherDetailsPost(post);
                }
            });

            // ───── AJOUT CARD ─────

            card.getChildren().addAll(
                    header,
                    contenu,
                    infos,
                    actions
            );

            feedContainer.getChildren().add(card);
        }
    }

    // ───────────────── DETAILS ─────────────────

    private void afficherDetailsPost(publication post) {

        try {

            // incrémenter les vues
            forumService.incrementerVues(post.getId());

            // récupérer le post mis à jour depuis la BD
            publication updatedPost =
                    forumService.getById(post.getId());

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/PostDetails.fxml")
            );

            Parent root = loader.load();

            PostDetailsController controller =
                    loader.getController();

            // envoyer le post mis à jour
            controller.setPost(updatedPost);

            feedContainer.getScene().setRoot(root);

        } catch (IOException e) {

            showError(
                    "Erreur affichage détails : "
                            + e.getMessage()
            );
        }
    }

    // ───────────────── COMMENTAIRES ─────────────────

    private void ouvrirCommentaires(publication post) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/CommentaireForum.fxml")
            );

            Parent root = loader.load();

            CommentaireForumController ctrl =
                    loader.getController();

            ctrl.initData(
                    post.getId(),
                    post.getTitre()
            );

            feedContainer.getScene().setRoot(root);

        } catch (IOException e) {

            showError(
                    "Erreur commentaires : "
                            + e.getMessage()
            );
        }
    }

    // ───────────────── ACTIONS CRUD ─────────────────

    @FXML
    void ajouterAction(ActionEvent event) {
        loadScene("/AjouterForum.fxml");
    }

    @FXML
    void modifierAction(ActionEvent event) {

        int id = getId();

        if (id == -1) return;

        publication p = forumService.getById(id);

        if (p == null) {
            showError("Post introuvable !");
            return;
        }

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ModifierForum.fxml")
            );

            Parent root = loader.load();

            ModifierForumController controller =
                    loader.getController();

            controller.initData(p);

            feedContainer.getScene().setRoot(root);

        } catch (IOException e) {

            showError(
                    "Erreur modification : "
                            + e.getMessage()
            );
        }
    }

    @FXML
    void supprimerAction(ActionEvent event) {

        int id = getId();

        if (id == -1) return;

        publication p = forumService.getById(id);

        if (p == null) {
            showError("Post introuvable !");
            return;
        }

        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        alert.setTitle("Confirmation");

        alert.setHeaderText("Supprimer ce post ?");

        alert.setContentText("Action irréversible");

        alert.showAndWait().ifPresent(response -> {

            if (response == ButtonType.OK) {

                forumService.delete(p);

                messageLabel.setText(
                        "✅ Post supprimé"
                );

                chargerMesPosts();
            }
        });
    }

    @FXML
    void epinglerAction(ActionEvent event) {

        int id = getId();

        if (id == -1) return;

        forumService.toggleEpingle(id);

        messageLabel.setText(
                "📌 Post épinglé"
        );

        chargerMesPosts();
    }

    @FXML
    void retourListeAction(ActionEvent event) {
        loadScene("/ListeForum.fxml");
    }

    @FXML
    void openActiviteAction(ActionEvent event) {
        loadScene("/MonActivite.fxml");
    }

    // IMPORTANT POUR LE FXML
    @FXML
    void voirCommentairesAction(ActionEvent event) {

        int id = getId();

        if (id == -1) return;

        publication p = forumService.getById(id);

        if (p == null) {
            showError("Post introuvable !");
            return;
        }

        ouvrirCommentaires(p);
    }

    // ───────────────── UTILITAIRES ─────────────────

    private int getId() {

        try {

            return Integer.parseInt(
                    idTextField.getText().trim()
            );

        } catch (Exception e) {

            showError("ID invalide !");

            return -1;
        }
    }

    private void loadScene(String fxml) {

        try {

            Parent root = FXMLLoader.load(
                    getClass().getResource(fxml)
            );

            feedContainer.getScene().setRoot(root);

        } catch (IOException e) {

            showError(
                    "Erreur chargement : "
                            + e.getMessage()
            );
        }
    }

    private void showError(String msg) {

        Alert a = new Alert(
                Alert.AlertType.ERROR
        );

        a.setHeaderText(msg);

        a.show();
    }
}