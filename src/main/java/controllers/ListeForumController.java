package controllers;

import javafx.concurrent.Task;
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
import util.NavigationManager;
import util.SessionManager;

import java.io.IOException;
import java.util.List;


public class ListeForumController {

    @FXML private TextField searchTextField;
    @FXML private ComboBox<String> categorieFilterComboBox;
    @FXML private Label statsLabel;
    @FXML private Label messageLabel;
    @FXML private Button btnChatbot;
    @FXML private VBox feedContainer;
    private String source = "CONDUCTEUR";

    public void setSource(String source) {
        this.source = source;
    }

    private forumService forumService = new forumService();
    private List<publication> publicationList;

    // ───────── INIT ─────────

    @FXML
    public void initialize() {
        categorieFilterComboBox.getItems().addAll(
                "Tous", "question", "discussion", "autre"
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
        publicationList.sort((p1, p2) -> Boolean.compare(p2.isEpingle(), p1.isEpingle()));
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
            header.setStyle("-fx-alignment: CENTER_LEFT;");

            Label titre = new Label(post.getTitre());
            titre.setStyle(
                    "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #085041;"
            );
            header.getChildren().add(titre);

            if (post.isEpingle()) {
                Region spacerEpingle = new Region();
                HBox.setHgrow(spacerEpingle, Priority.ALWAYS);
                Label epingle = new Label("📌 Épinglé");
                epingle.setStyle(
                        "-fx-background-color: #FFE082;" +
                                "-fx-padding: 4 8;" +
                                "-fx-background-radius: 10;" +
                                "-fx-font-size: 11px;"
                );
                header.getChildren().addAll(spacerEpingle, epingle);
            }

            // ───────── AUTEUR CLIQUABLE ─────────
            String nomAuteur = forumService.getNomAuteur(post.getAuteurId());
            Label auteurLabel = new Label("👤 " + nomAuteur);
            auteurLabel.setStyle(
                    "-fx-text-fill: #9B8FD4;" +
                            "-fx-font-size: 12px;" +
                            "-fx-cursor: hand;" +
                            "-fx-underline: true;"
            );
            auteurLabel.setOnMouseClicked(e -> ouvrirProfil(post, nomAuteur));

            // ───────── CONTENU ORIGINAL ─────────
            Label contenu = new Label(post.getContenu());
            contenu.setWrapText(true);
            contenu.setStyle("-fx-text-fill: #444;");

            // ───────── CONTENU TRADUIT (caché) ─────────
            Label contenuTraduit = new Label("");
            contenuTraduit.setWrapText(true);
            contenuTraduit.setVisible(false);
            contenuTraduit.setManaged(false);
            contenuTraduit.setStyle(
                    "-fx-text-fill: #1A6B9A;" +
                            "-fx-font-style: italic;" +
                            "-fx-background-color: #EBF5FB;" +
                            "-fx-background-radius: 6;" +
                            "-fx-padding: 8 10;"
            );

            Label langueLabel = new Label("🌐 Traduit automatiquement · LibreTranslate");
            langueLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 10px;");
            langueLabel.setVisible(false);
            langueLabel.setManaged(false);

            // ───────── IMAGE ─────────
            ImageView imageView = new ImageView();
            if (post.getImage() != null && !post.getImage().trim().isEmpty()) {
                try {
                    Image image = new Image("file:" + post.getImage());
                    imageView.setImage(image);
                    imageView.setFitWidth(500);
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);
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
            if (categorie == null) categorie = "NON DÉFINIE";
            else categorie = categorie.toUpperCase();

            Label infos = new Label(
                    "📂 " + categorie + "   👁 " + post.getNb_vues() + " vues"
            );
            infos.setStyle("-fx-text-fill: #777; -fx-font-size: 12px;");

            // ───────── BOUTON TRADUCTION ─────────
            Button traduireBtn = new Button("🌐 Traduire ");
            traduireBtn.setStyle(
                    "-fx-background-color: #EBF5FB;" +
                            "-fx-text-fill: #1A6B9A;" +
                            "-fx-background-radius: 5;" +
                            "-fx-cursor: hand;" +
                            "-fx-font-size: 12px;"
            );

            final boolean[] traduit = {false};
            final String[] texteCache = {""};

            traduireBtn.setOnAction(e -> {
                if (!traduit[0]) {
                    traduireBtn.setText("⏳ Traduction...");
                    traduireBtn.setDisable(true);

                    Task<String> task = new Task<>() {
                        @Override
                        protected String call() {
                            return forumService.traduireEnAnglais(post.getContenu());
                        }
                    };

                    task.setOnSucceeded(ev -> {
                        texteCache[0] = task.getValue();
                        contenuTraduit.setText("🇬🇧 " + texteCache[0]);
                        contenuTraduit.setVisible(true);
                        contenuTraduit.setManaged(true);
                        langueLabel.setVisible(true);
                        langueLabel.setManaged(true);
                        traduireBtn.setText("🙈 Masquer la traduction");
                        traduireBtn.setDisable(false);
                        traduit[0] = true;
                    });

                    task.setOnFailed(ev -> {
                        traduireBtn.setText("❌ Erreur — réessayer");
                        traduireBtn.setDisable(false);
                    });

                    new Thread(task).start();

                } else {
                    boolean visible = contenuTraduit.isVisible();
                    contenuTraduit.setVisible(!visible);
                    contenuTraduit.setManaged(!visible);
                    langueLabel.setVisible(!visible);
                    langueLabel.setManaged(!visible);
                    traduireBtn.setText(visible
                            ? "🌐 Afficher la traduction"
                            : "🙈 Masquer la traduction");
                }
            });

            // ───────── BOUTON COMMENTAIRES ─────────
            Button commentairesBtn = new Button("💬 Commentaires");
            commentairesBtn.setStyle(
                    "-fx-background-color: #F5F6FA;" +
                            "-fx-text-fill: #085041;" +
                            "-fx-background-radius: 5;" +
                            "-fx-cursor: hand;"
            );
            commentairesBtn.setOnAction(e -> ouvrirCommentaires(post));

            HBox actions = new HBox(10, traduireBtn, commentairesBtn);

            // ───────── DOUBLE CLIC → DETAILS ─────────
            card.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    afficherDetailsPost(post);
                }
            });

            // ───────── AJOUT DANS CARD ─────────
            card.getChildren().addAll(
                    header,
                    auteurLabel,
                    contenu,
                    contenuTraduit,
                    langueLabel,
                    imageView,
                    infos,
                    actions
            );

            feedContainer.getChildren().add(card);
        }
    }

    // ───────── RECHERCHE ─────────

    @FXML
    void rechercherAction(ActionEvent event) {
        String keyword = searchTextField.getText().trim();
        if (keyword.isEmpty()) {
            chargerpublications();
            return;
        }
        publicationList = forumService.rechercher(keyword);
        afficherFeed(publicationList);
        statsLabel.setText(publicationList.size() + " résultats");
    }

    @FXML
    void filtrerCategorieAction(ActionEvent event) {
        String cat = categorieFilterComboBox.getValue();
        if (cat == null || cat.equals("Tous")) {
            chargerpublications();
        } else {
            publicationList = forumService.getByCategorie(cat);
            afficherFeed(publicationList);
        }
    }

    // ───────── TRI ─────────

    @FXML
    void trierParDateAction(ActionEvent event) {
        publicationList = forumService.trierParDate();
        afficherFeed(publicationList);
        messageLabel.setText("Tri par date");
    }

    @FXML
    void trierParVuesAction(ActionEvent event) {
        publicationList = forumService.trierParVues();
        afficherFeed(publicationList);
        messageLabel.setText("Tri par vues");
    }

    // ───────── NAVIGATION (MODIFIÉE AVEC NAVIGATIONMANAGER) ─────────

    @FXML
    void ajouterAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterForum.fxml"));
            Parent root = loader.load();

            NavigationManager.loadIntoTab("FORUM", root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void mesPostsAction(ActionEvent event) {
        try {
            System.out.println("🔍 Chargement de MesForums.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MesForums.fxml"));
            Parent root = loader.load();
            System.out.println("✅ MesForums.fxml chargé avec succès");

            MesForumsController controller = loader.getController();

            if (SessionManager.getCurrentUser() != null &&
                    "ADMIN".equals(SessionManager.getCurrentUser().getRole())) {
                controller.setSource("ADMIN");
            }

            NavigationManager.loadIntoTab("FORUM", root);

        } catch (IOException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ───────── DETAILS POST (MODIFIÉ) ─────────

    private void afficherDetailsPost(publication post) {
        try {
            forumService.incrementerVues(post.getId());
            publication fresh = forumService.getById(post.getId());

            // ✅ Reste dans l'onglet FORUM du Dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PostDetails.fxml"));
            Parent root = loader.load();
            PostDetailsController controller = loader.getController();
            controller.setPost(fresh);

            // Remplacer le contenu du conteneur FORUM
            NavigationManager.loadIntoTab("FORUM", root);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ───────── COMMENTAIRES (MODIFIÉ) ─────────

    private void ouvrirCommentaires(publication post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentaireForum.fxml"));
            Parent root = loader.load();
            CommentaireForumController ctrl = loader.getController();
            ctrl.initData(post.getId(), post.getTitre());

            // ✅ Reste dans l'onglet FORUM du Dashboard
            NavigationManager.loadIntoTab("FORUM", root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ───────── PROFIL AUTEUR ────────

    private void ouvrirProfil(publication post, String nomAuteur) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfilAuteur.fxml"));
            Parent root = loader.load();
            ProfilAuteurController ctrl = loader.getController();

            ctrl.initData(post.getAuteurId(), nomAuteur, "/ListeForum.fxml");

            // ✅ Vérifier si l'utilisateur est Admin
            if (SessionManager.getCurrentUser() != null &&
                    "ADMIN".equals(SessionManager.getCurrentUser().getRole())) {
                ctrl.setSource("ADMIN");
                System.out.println("🔍 Profil ouvert en mode ADMIN");
            }

            NavigationManager.loadIntoTab("FORUM", root);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    // ───────── CHATBOT  ─────────

    @FXML
    void openChatbot() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatbotAide.fxml"));
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