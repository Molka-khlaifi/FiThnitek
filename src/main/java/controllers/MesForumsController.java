package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import models.publication;
import services.forumService;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import util.SessionManager;


import java.io.IOException;
import java.util.List;

public class MesForumsController {

    @FXML private Label statsLabel;
    @FXML private Label messageLabel;
    @FXML private VBox  feedContainer;

    private final forumService forumService = new forumService();
    private List<publication> mesPosts;

    @FXML
    public void initialize() {
        chargerMesPosts();
    }

    private void chargerMesPosts() {
        mesPosts = forumService.getByAuteur(SessionManager.getCurrentUser().getId());
        mesPosts.sort((p1, p2) -> Boolean.compare(p2.isEpingle(), p1.isEpingle()));
        afficherFeed(mesPosts);
        statsLabel.setText(mesPosts.size() + " posts");
    }

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

            // ── Header ──

            HBox header = new HBox(10);

            Label titre = new Label(post.getTitre());

            titre.setStyle(
                    "-fx-font-size: 16px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #085041;"
            );

            header.getChildren().add(titre);

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

                header.getChildren().addAll(
                        spacer,
                        epingle
                );
            }

            // ── Contenu ──

            Label contenu = new Label(post.getContenu());

            contenu.setWrapText(true);

            contenu.setStyle(
                    "-fx-text-fill: #444;" +
                            "-fx-font-size: 13px;"
            );

            // ── Image ──

            ImageView imageView = new ImageView();

            if (post.getImage() != null &&
                    !post.getImage().trim().isEmpty()) {

                try {

                    Image image = new Image(
                            "file:" + post.getImage()
                    );

                    imageView.setImage(image);

                    imageView.setFitWidth(500);

                    imageView.setPreserveRatio(true);

                    imageView.setSmooth(true);

                    imageView.setStyle(
                            "-fx-background-radius: 10;"
                    );

                } catch (Exception e) {

                    imageView.setVisible(false);
                    imageView.setManaged(false);
                }

            } else {

                imageView.setVisible(false);
                imageView.setManaged(false);
            }

            // ── Infos ──
            String categorie = post.getCategorie();

            if (categorie == null || categorie.isBlank()) {
                categorie = "NON DÉFINIE";
            } else {
                categorie = categorie.toUpperCase();
            }

            Label infos = new Label(
                    "📂 " + categorie +
                            "   👁 " + post.getNb_vues() + " vues"
            );

            infos.setStyle(
                    "-fx-text-fill: #777;" +
                            "-fx-font-size: 12px;"
            );

            // ── Boutons ──

            Button commentairesBtn =
                    new Button("💬 Commentaires");

            commentairesBtn.setStyle(
                    "-fx-background-color: #2389c8;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 5;" +
                            "-fx-cursor: hand;"
            );

            commentairesBtn.setOnAction(
                    e -> ouvrirCommentaires(post)
            );

            Button modifierBtn =
                    new Button("✏ Modifier");

            modifierBtn.setStyle(
                    "-fx-background-color: #3aa3e3;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 5;" +
                            "-fx-cursor: hand;"
            );

            modifierBtn.setOnAction(
                    e -> modifierPost(post)
            );

            Button supprimerBtn =
                    new Button("🗑 Supprimer");

            supprimerBtn.setStyle(
                    "-fx-background-color: #5bbcf5;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 5;" +
                            "-fx-cursor: hand;"
            );

            supprimerBtn.setOnAction(
                    e -> supprimerPost(post)
            );

            Button epinglerBtn =
                    new Button(
                            post.isEpingle()
                                    ? "📌 Désépingler"
                                    : "📌 Épingler"
                    );

            epinglerBtn.setStyle(
                    "-fx-background-color: #85d0ff;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 5;" +
                            "-fx-cursor: hand;"
            );

            epinglerBtn.setOnAction(e -> {

                forumService.toggleEpingle(post.getId());

                messageLabel.setText(
                        "📌 Post mis à jour"
                );

                chargerMesPosts();
            });

            HBox actions = new HBox(
                    8,
                    commentairesBtn,
                    modifierBtn,
                    supprimerBtn,
                    epinglerBtn
            );

            // ── Double clic ──

            card.setOnMouseClicked(event -> {

                if (event.getClickCount() == 2) {

                    afficherDetailsPost(post);
                }
            });

            // ── Empêcher propagation ──

            commentairesBtn.setOnMouseClicked(
                    event -> event.consume()
            );

            modifierBtn.setOnMouseClicked(
                    event -> event.consume()
            );

            supprimerBtn.setOnMouseClicked(
                    event -> event.consume()
            );

            epinglerBtn.setOnMouseClicked(
                    event -> event.consume()
            );

            // ── Ajouter composants ──

            card.getChildren().addAll(
                    (Node) header,
                    contenu,
                    imageView,
                    infos,
                    actions
            );

            feedContainer.getChildren().add(card);
        }
    }

    // ── Modifier ──
    private void modifierPost(publication post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierForum.fxml"));
            Parent root = loader.load();
            ModifierForumController controller = loader.getController();
            controller.initData(post);
            feedContainer.getScene().setRoot(root);
        } catch (IOException e) {
            showError("Erreur modification : " + e.getMessage());
        }
    }

    // ── Supprimer ──
    private void supprimerPost(publication post) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer ce post ?");
        alert.setContentText("Action irréversible");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                forumService.delete(post);
                messageLabel.setText("✅ Post supprimé");
                chargerMesPosts();
            }
        });
    }

    // ── Détails ──
    private void afficherDetailsPost(publication post) {
        try {
            forumService.incrementerVues(post.getId());
            publication updatedPost = forumService.getById(post.getId());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PostDetails.fxml"));
            Parent root = loader.load();
            PostDetailsController controller = loader.getController();
            controller.setPost(updatedPost);
            controller.setRetourFxml("/MesForums.fxml");
            feedContainer.getScene().setRoot(root);
        } catch (IOException e) {
            showError("Erreur affichage détails : " + e.getMessage());
        }
    }

    // ── Commentaires ──
    private void ouvrirCommentaires(publication post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentaireForum.fxml"));
            Parent root = loader.load();
            CommentaireForumController ctrl = loader.getController();
            ctrl.initData(post.getId(), post.getTitre());
            ctrl.setRetourFxml("/MesForums.fxml");
            feedContainer.getScene().setRoot(root);
        } catch (IOException e) {
            showError("Erreur commentaires : " + e.getMessage());
        }
    }

    @FXML void ajouterAction(ActionEvent event)      { loadScene("/AjouterForum.fxml"); }
    @FXML void retourListeAction(ActionEvent event)  { loadScene("/ListeForum.fxml"); }
    @FXML void openActiviteAction(ActionEvent event) { loadScene("/MonActivite.fxml"); }

    @FXML void modifierAction(ActionEvent event)         {}
    @FXML void supprimerAction(ActionEvent event)        {}
    @FXML void epinglerAction(ActionEvent event)         {}
    @FXML void voirCommentairesAction(ActionEvent event) {}

    private void loadScene(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Scene scene = feedContainer.getScene();

            if (scene == null) {
                System.out.println("Scene NULL !");
                return;
            }

            scene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur chargement : " + e.getMessage());
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(msg);
        a.show();
    }
}