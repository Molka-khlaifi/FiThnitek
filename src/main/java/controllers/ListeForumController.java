package controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.publication;
import services.forumService;
import services.NavigationManager;
import services.SessionManager;

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

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€ INIT â”€â”€â”€â”€â”€â”€â”€â”€â”€

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

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€ CHARGER POSTS â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private void chargerpublications() {
        publicationList = forumService.getAll();
        publicationList.sort((p1, p2) -> Boolean.compare(p2.isEpingle(), p1.isEpingle()));
        afficherFeed(publicationList);
        statsLabel.setText(publicationList.size() + " posts");
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€ AFFICHAGE FEED â”€â”€â”€â”€â”€â”€â”€â”€â”€

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

            // â”€â”€â”€â”€â”€â”€â”€â”€â”€ HEADER â”€â”€â”€â”€â”€â”€â”€â”€â”€
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
                Label epingle = new Label("ðŸ“Œ Ã‰pinglÃ©");
                epingle.setStyle(
                        "-fx-background-color: #FFE082;" +
                                "-fx-padding: 4 8;" +
                                "-fx-background-radius: 10;" +
                                "-fx-font-size: 11px;"
                );
                header.getChildren().addAll(spacerEpingle, epingle);
            }

            // â”€â”€â”€â”€â”€â”€â”€â”€â”€ AUTEUR CLIQUABLE â”€â”€â”€â”€â”€â”€â”€â”€â”€
            String nomAuteur = forumService.getNomAuteur(post.getAuteurId());
            Label auteurLabel = new Label("ðŸ‘¤ " + nomAuteur);
            auteurLabel.setStyle(
                    "-fx-text-fill: #9B8FD4;" +
                            "-fx-font-size: 12px;" +
                            "-fx-cursor: hand;" +
                            "-fx-underline: true;"
            );
            auteurLabel.setOnMouseClicked(e -> ouvrirProfil(post, nomAuteur));

            // â”€â”€â”€â”€â”€â”€â”€â”€â”€ CONTENU ORIGINAL â”€â”€â”€â”€â”€â”€â”€â”€â”€
            Label contenu = new Label(post.getContenu());
            contenu.setWrapText(true);
            contenu.setStyle("-fx-text-fill: #444;");

            // â”€â”€â”€â”€â”€â”€â”€â”€â”€ CONTENU TRADUIT (cachÃ©) â”€â”€â”€â”€â”€â”€â”€â”€â”€
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

            Label langueLabel = new Label("ðŸŒ Traduit automatiquement Â· LibreTranslate");
            langueLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 10px;");
            langueLabel.setVisible(false);
            langueLabel.setManaged(false);

            // â”€â”€â”€â”€â”€â”€â”€â”€â”€ IMAGE â”€â”€â”€â”€â”€â”€â”€â”€â”€
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

            // â”€â”€â”€â”€â”€â”€â”€â”€â”€ INFOS â”€â”€â”€â”€â”€â”€â”€â”€â”€
            String categorie = post.getCategorie();
            if (categorie == null) categorie = "NON DÃ‰FINIE";
            else categorie = categorie.toUpperCase();

            Label infos = new Label(
                    "ðŸ“‚ " + categorie + "   ðŸ‘ " + post.getNb_vues() + " vues"
            );
            infos.setStyle("-fx-text-fill: #777; -fx-font-size: 12px;");

            // â”€â”€â”€â”€â”€â”€â”€â”€â”€ BOUTON TRADUCTION â”€â”€â”€â”€â”€â”€â”€â”€â”€
            Button traduireBtn = new Button("ðŸŒ Traduire ");
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
                    traduireBtn.setText("â³ Traduction...");
                    traduireBtn.setDisable(true);

                    Task<String> task = new Task<>() {
                        @Override
                        protected String call() {
                            return forumService.traduireEnAnglais(post.getContenu());
                        }
                    };

                    task.setOnSucceeded(ev -> {
                        texteCache[0] = task.getValue();
                        contenuTraduit.setText("ðŸ‡¬ðŸ‡§ " + texteCache[0]);
                        contenuTraduit.setVisible(true);
                        contenuTraduit.setManaged(true);
                        langueLabel.setVisible(true);
                        langueLabel.setManaged(true);
                        traduireBtn.setText("ðŸ™ˆ Masquer la traduction");
                        traduireBtn.setDisable(false);
                        traduit[0] = true;
                    });

                    task.setOnFailed(ev -> {
                        traduireBtn.setText("âŒ Erreur â€” rÃ©essayer");
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
                            ? "ðŸŒ Afficher la traduction"
                            : "ðŸ™ˆ Masquer la traduction");
                }
            });

            // â”€â”€â”€â”€â”€â”€â”€â”€â”€ BOUTON COMMENTAIRES â”€â”€â”€â”€â”€â”€â”€â”€â”€
            Button commentairesBtn = new Button("ðŸ’¬ Commentaires");
            commentairesBtn.setStyle(
                    "-fx-background-color: #F5F6FA;" +
                            "-fx-text-fill: #085041;" +
                            "-fx-background-radius: 5;" +
                            "-fx-cursor: hand;"
            );
            commentairesBtn.setOnAction(e -> ouvrirCommentaires(post));

            HBox actions = new HBox(10, traduireBtn, commentairesBtn);

            // â”€â”€â”€â”€â”€â”€â”€â”€â”€ DOUBLE CLIC â†’ DETAILS â”€â”€â”€â”€â”€â”€â”€â”€â”€
            card.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    afficherDetailsPost(post);
                }
            });

            // â”€â”€â”€â”€â”€â”€â”€â”€â”€ AJOUT DANS CARD â”€â”€â”€â”€â”€â”€â”€â”€â”€
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

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€ RECHERCHE â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @FXML
    void rechercherAction(ActionEvent event) {
        String keyword = searchTextField.getText().trim();
        if (keyword.isEmpty()) {
            chargerpublications();
            return;
        }
        publicationList = forumService.rechercher(keyword);
        afficherFeed(publicationList);
        statsLabel.setText(publicationList.size() + " rÃ©sultats");
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

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€ TRI â”€â”€â”€â”€â”€â”€â”€â”€â”€

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

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€ NAVIGATION (MODIFIÃ‰E AVEC NAVIGATIONMANAGER) â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @FXML
    void ajouterAction(ActionEvent event) {
        try {
            NavigationManager.navigateFrom(messageLabel, "/AjouterForum.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void mesPostsAction(ActionEvent event) {
        try {
            NavigationManager.navigateFrom(messageLabel, "/MesForums.FXML", (MesForumsController controller) -> {
                if (SessionManager.getCurrentUser() != null &&
                        "ADMIN".equals(SessionManager.getCurrentUser().getRole())) {
                    controller.setSource("ADMIN");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // DETAILS POST (MODIFIÃ‰) â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private void afficherDetailsPost(publication post) {
        try {
            forumService.incrementerVues(post.getId());
            publication fresh = forumService.getById(post.getId());
            NavigationManager.navigateFrom(messageLabel, "/PostDetails.FXML",
                    (PostDetailsController controller) -> controller.setPost(fresh));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // COMMENTAIRES (MODIFIÃ‰) â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private void ouvrirCommentaires(publication post) {
        try {
            NavigationManager.navigateFrom(messageLabel, "/CommentaireForum.fxml",
                    (CommentaireForumController ctrl) -> ctrl.initData(post.getId(), post.getTitre()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // PROFIL AUTEUR â”€â”€â”€â”€â”€â”€â”€â”€

    private void ouvrirProfil(publication post, String nomAuteur) {
        try {
            NavigationManager.navigateFrom(messageLabel, "/ProfilAuteur.fxml", (ProfilAuteurController ctrl) -> {
                ctrl.initData(post.getAuteurId(), nomAuteur, "/ListeForum.fxml");
                if (SessionManager.getCurrentUser() != null &&
                        "ADMIN".equals(SessionManager.getCurrentUser().getRole())) {
                    ctrl.setSource("ADMIN");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // CHATBOT  â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @FXML
    void openChatbot() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatBotAide.fxml"));
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

