package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import models.publication;
import services.forumService;
import services.NavigationManager;
import services.SessionManager;

import java.io.IOException;
import java.util.List;

public class MesForumsController {

    @FXML private Label statsLabel;
    @FXML private Label messageLabel;
    @FXML private VBox  feedContainer;
    private String source = "CONDUCTEUR";

    public void setSource(String source) {
        this.source = source;
    }

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

            // â”€â”€ Header â”€â”€
            HBox header = new HBox(10);
            Label titre = new Label(post.getTitre());
            titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #085041;");
            header.getChildren().add(titre);

            if (post.isEpingle()) {
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                Label epingle = new Label("ðŸ“Œ Ã‰pinglÃ©");
                epingle.setStyle(
                        "-fx-background-color: #FFE082;" +
                                "-fx-padding: 4 8;" +
                                "-fx-background-radius: 10;" +
                                "-fx-font-size: 11px;"
                );
                header.getChildren().addAll(spacer, epingle);
            }

            // â”€â”€ Contenu â”€â”€
            Label contenu = new Label(post.getContenu());
            contenu.setWrapText(true);
            contenu.setStyle("-fx-text-fill: #444; -fx-font-size: 13px;");

            // â”€â”€ Image â”€â”€
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

            // â”€â”€ Infos â”€â”€
            String categorie = post.getCategorie();
            if (categorie == null || categorie.isBlank()) categorie = "NON DÃ‰FINIE";
            else categorie = categorie.toUpperCase();

            Label infos = new Label("ðŸ“‚ " + categorie + "   ðŸ‘ " + post.getNb_vues() + " vues");
            infos.setStyle("-fx-text-fill: #777; -fx-font-size: 12px;");

            // â”€â”€ Boutons â”€â”€
            Button commentairesBtn = new Button("ðŸ’¬ Commentaires");
            commentairesBtn.setStyle("-fx-background-color: #F5F6FA; -fx-text-fill: #444; -fx-background-radius: 5; -fx-cursor: hand;");
            commentairesBtn.setOnAction(e -> ouvrirCommentaires(post));

            Button modifierBtn = new Button("âœ Modifier");
            modifierBtn.setStyle("-fx-background-color: #F5F6FA; -fx-text-fill: #444; -fx-background-radius: 5; -fx-cursor: hand;");
            modifierBtn.setOnAction(e -> modifierPost(post));

            Button supprimerBtn = new Button("ðŸ—‘ Supprimer");
            supprimerBtn.setStyle("-fx-background-color: #F5F6FA; -fx-text-fill: #444; -fx-background-radius: 5; -fx-cursor: hand;");
            supprimerBtn.setOnAction(e -> supprimerPost(post));

            Button epinglerBtn = new Button(post.isEpingle() ? "ðŸ“Œ DÃ©sÃ©pingler" : "ðŸ“Œ Ã‰pingler");
            epinglerBtn.setStyle("-fx-background-color: #F5F6FA; -fx-text-fill: #444; -fx-background-radius: 5; -fx-cursor: hand;");
            epinglerBtn.setOnAction(e -> {
                forumService.toggleEpingle(post.getId());
                messageLabel.setText("ðŸ“Œ Post mis Ã  jour");
                chargerMesPosts();
            });

            HBox actions = new HBox(8, commentairesBtn, modifierBtn, supprimerBtn, epinglerBtn);

            // â”€â”€ Double clic â†’ dÃ©tails â”€â”€
            card.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) afficherDetailsPost(post);
            });

            // â”€â”€ EmpÃªcher propagation sur les boutons â”€â”€
            for (Button btn : new Button[]{commentairesBtn, modifierBtn, supprimerBtn, epinglerBtn}) {
                btn.setOnMouseClicked(event -> event.consume());
            }

            card.getChildren().addAll((Node) header, contenu, imageView, infos, actions);
            feedContainer.getChildren().add(card);
        }
    }

    // â”€â”€ Modifier â”€â”€
    private void modifierPost(publication post) {
        try {
            NavigationManager.navigateFrom(messageLabel, "/ModifierForum.fxml",
                    (ModifierForumController controller) -> controller.initData(post));
        } catch (IOException e) {
            showError("Erreur modification : " + e.getMessage());
        }
    }

    // Supprimer â”€â”€
    private void supprimerPost(publication post) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer ce post ?");
        alert.setContentText("Action irrÃ©versible");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                forumService.delete(post);
                messageLabel.setText("âœ… Post supprimÃ©");
                chargerMesPosts();
            }
        });
    }

    // â”€â”€ DÃ©tails â”€â”€
    private void afficherDetailsPost(publication post) {
        try {
            forumService.incrementerVues(post.getId());
            publication updatedPost = forumService.getById(post.getId());
            NavigationManager.navigateFrom(messageLabel, "/PostDetails.FXML", (PostDetailsController controller) -> {
                controller.setPost(updatedPost);
                controller.setRetourFxml("/MesForums.FXML");
            });
        } catch (IOException e) {
            showError("Erreur affichage details : " + e.getMessage());
        }
    }

    // Commentaires â”€â”€
    private void ouvrirCommentaires(publication post) {
        try {
            NavigationManager.navigateFrom(messageLabel, "/CommentaireForum.fxml", (CommentaireForumController ctrl) -> {
                ctrl.initData(post.getId(), post.getTitre());
                ctrl.setRetourFxml("/MesForums.FXML");
            });
        } catch (IOException e) {
            showError("Erreur commentaires : " + e.getMessage());
        }
    }

    @FXML void ajouterAction(ActionEvent event)      { try { NavigationManager.navigateFrom(messageLabel, "/AjouterForum.fxml"); } catch (IOException e) { showError(e.getMessage()); } }
    @FXML void retourListeAction(ActionEvent event)  { try { NavigationManager.navigateFrom(messageLabel, "/ListeForum.fxml"); } catch (IOException e) { showError(e.getMessage()); } }
    @FXML void openActiviteAction(ActionEvent event) { try { NavigationManager.navigateFrom(messageLabel, "/MonActivite.fxml"); } catch (IOException e) { showError(e.getMessage()); } }

    @FXML void modifierAction(ActionEvent event)         {}
    @FXML void supprimerAction(ActionEvent event)        {}
    @FXML void epinglerAction(ActionEvent event)         {}
    @FXML void voirCommentairesAction(ActionEvent event) {}

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(msg);
        a.show();
    }
}

