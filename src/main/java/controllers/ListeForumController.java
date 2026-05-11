package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.publication;
import services.forumService;

import java.io.IOException;
import java.util.List;

public class ListeForumController {

    @FXML private TextField searchTextField;
    @FXML private ComboBox<String> categorieFilterComboBox;
    @FXML private Label statsLabel;
    @FXML private Label messageLabel;
    @FXML private Button btnChatbot;
    @FXML private VBox feedContainer;

    private forumService forumService = new forumService();
    private List<publication> publicationList;

    // ───────── INIT ─────────

    @FXML
    public void initialize() {

        categorieFilterComboBox.getItems().addAll(
                "Tous",
                "question",
                "discussion",
                "autre"
        );

        categorieFilterComboBox.setValue("Tous");

        chargerpublications();

        searchTextField.textProperty().addListener((obs, oldVal, newVal) -> {

            publicationList = forumService.rechercher(newVal);
            afficherFeed(publicationList);

        });

        searchTextField.setOnAction(this::rechercherAction);
    }

    // ───────── CHARGER POSTS ─────────

    private void chargerpublications() {

        publicationList = forumService.getAll();

        publicationList.sort(
                (p1, p2) -> Boolean.compare(
                        p2.isEpingle(),
                        p1.isEpingle()
                )
        );

        afficherFeed(publicationList);

        statsLabel.setText(publicationList.size() + " posts");
    }

    // ───────── AFFICHAGE FEED ─────────

    private void afficherFeed(List<publication> list) {

        feedContainer.getChildren().clear();

        for (publication post : list) {

            VBox card = new VBox(10);

            card.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-padding: 15;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-radius: 12;" +
                            "-fx-border-color: #DDE6ED;" +
                            "-fx-cursor: hand;"
            );

            // ───────── HEADER ─────────

            HBox header = new HBox(10);

            Label titre = new Label(post.getTitre());

            titre.setStyle(
                    "-fx-font-size: 18px;" +
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

            // ───────── CONTENU ─────────

            Label contenu = new Label(post.getContenu());

            contenu.setWrapText(true);

            contenu.setStyle(
                    "-fx-text-fill: #444;"
            );

            // ───────── IMAGE ─────────

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

            // ───────── INFOS ─────────
            String categorie = post.getCategorie();

            if (categorie == null) {
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

            // ───────── BOUTONS ─────────

            Button commentairesBtn =
                    new Button("💬 Commentaires");

            commentairesBtn.setStyle(
                    "-fx-background-color: #4bcad6;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 5;" +
                            "-fx-cursor: hand;"
            );

            commentairesBtn.setOnAction(
                    e -> ouvrirCommentaires(post)
            );

            HBox actions = new HBox(
                    10,
                    commentairesBtn
            );

            // ───────── DOUBLE CLIC ─────────

            card.setOnMouseClicked(event -> {

                if (event.getClickCount() == 2) {

                    afficherDetailsPost(post);
                }
            });

            // ───────── AJOUT DANS CARD ─────────

            card.getChildren().addAll(
                    header,
                    contenu,
                    imageView,
                    infos,
                    actions
            );

            // ───────── AJOUT DANS FEED ─────────

            feedContainer.getChildren().add(card);
        }
    }

    // ───────── RECHERCHE ─────────

    @FXML
    void rechercherAction(ActionEvent event) {

        String keyword =
                searchTextField.getText().trim();

        if (keyword.isEmpty()) {

            chargerpublications();

            return;
        }

        publicationList =
                forumService.rechercher(keyword);

        afficherFeed(publicationList);

        statsLabel.setText(
                publicationList.size() + " résultats"
        );
    }

    @FXML
    void filtrerCategorieAction(ActionEvent event) {

        String cat =
                categorieFilterComboBox.getValue();

        if (cat == null || cat.equals("Tous")) {

            chargerpublications();

        } else {

            publicationList =
                    forumService.getByCategorie(cat);

            afficherFeed(publicationList);
        }
    }

    // ───────── TRI ─────────

    @FXML
    void trierParDateAction(ActionEvent event) {

        publicationList =
                forumService.trierParDate();

        afficherFeed(publicationList);

        messageLabel.setText("Tri par date");
    }

    @FXML
    void trierParVuesAction(ActionEvent event) {

        publicationList =
                forumService.trierParVues();

        afficherFeed(publicationList);

        messageLabel.setText("Tri par vues");
    }

    // ───────── NAVIGATION ─────────

    @FXML
    void ajouterAction(ActionEvent event)
            throws IOException {

        Parent root =
                FXMLLoader.load(
                        getClass().getResource(
                                "/AjouterForum.fxml"
                        )
                );

        feedContainer.getScene().setRoot(root);
    }

    @FXML
    void mesPostsAction(ActionEvent event)
            throws IOException {

        Parent root =
                FXMLLoader.load(
                        getClass().getResource(
                                "/MesForums.fxml"
                        )
                );

        feedContainer.getScene().setRoot(root);
    }

    // ───────── DETAILS POST ─────────

    private void afficherDetailsPost(publication post) {

        try {

            forumService.incrementerVues(
                    post.getId()
            );

            publication fresh =
                    forumService.getById(
                            post.getId()
                    );

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/PostDetails.fxml"
                            )
                    );

            Parent root = loader.load();

            PostDetailsController controller =
                    loader.getController();

            controller.setPost(fresh);

            feedContainer.getScene().setRoot(root);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    // ───────── COMMENTAIRES ─────────

    private void ouvrirCommentaires(publication post) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/CommentaireForum.fxml"
                            )
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

            e.printStackTrace();
        }
    }

    // ───────── CHATBOT ─────────

    @FXML
    void openChatbot() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/ChatbotAide.fxml"
                            )
                    );

            Parent root = loader.load();

            Stage stage = new Stage();

            stage.setTitle("Assistant Forum");

            stage.setScene(new Scene(root));

            stage.setResizable(false);

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}