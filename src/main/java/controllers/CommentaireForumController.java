package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import models.commentaire;
import services.forumService;
import services.ModerationContenu;
import services.NavigationManager;
import services.SessionManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CommentaireForumController {

    @FXML private Label titreForum;
    @FXML private Label infoForumLabel;
    @FXML private Label nbCommentairesLabel;
    @FXML private ListView<commentaire> commentairesListView;
    @FXML private TextArea nouveauCommentaireArea;
    @FXML private Label messageLabel;
    private String source = "CONDUCTEUR";

    public void setSource(String source) {
        this.source = source;
    }

    private forumService forumService = new forumService();
    private int forumId;
    private List<commentaire> commentaireList;
    private String retourFxml = "/ListeForum.fxml";

    public void setRetourFxml(String fxml) {
        this.retourFxml = fxml;
    }

    public void initData(int forumId, String titre) {
        this.forumId = forumId;
        titreForum.setText(titre);
        infoForumLabel.setText(" â€” " + titre);
        forumService.incrementerVues(forumId);
        chargerCommentaires();

        nouveauCommentaireArea.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                publierCommentaireAction(null);
                event.consume();
            }
        });
    }

    private void chargerCommentaires() {
        commentairesListView.getItems().clear();
        commentaireList = forumService.getcommentairesBypublication(forumId);
        nbCommentairesLabel.setText("ðŸ’¬ Commentaires (" + commentaireList.size() + ")");
        commentairesListView.getItems().addAll(commentaireList);

        commentairesListView.setCellFactory(lv -> new ListCell<commentaire>() {
            @Override
            protected void updateItem(commentaire c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                    return;
                }

                Circle cercle = new Circle(18);
                cercle.setStyle("-fx-fill: #b5d4f4;");
                String auteurName = forumService.getNomAuteur(c.getAuteurId());
                Label initiales = new Label(getInitiales(auteurName));
                initiales.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #0c447c;");
                StackPane avatar = new StackPane(cercle, initiales);
                avatar.setMinSize(36, 36);
                avatar.setMaxSize(36, 36);
                Label nom = new Label();
                nom.setText(auteurName != null ? auteurName : "Utilisateur inconnu");
                nom.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                Label date = new Label("ðŸ• " + formatDate(c.getDateCommentaire()));
                date.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa;");
                VBox headerBox = new VBox(2, nom, date);

                HBox topRow = new HBox(10, avatar, headerBox);
                topRow.setAlignment(Pos.CENTER_LEFT);

                Label texte = new Label(c.getContenu());
                texte.setWrapText(true);
                texte.setMaxWidth(560);
                texte.setStyle("-fx-font-size: 13px; -fx-text-fill: #333; -fx-padding: 4 0 0 0;");

                Button btnLike = new Button("ðŸ‘ J'aime Â· " + c.getLike());
                btnLike.setStyle("-fx-background-color: transparent; -fx-font-size: 12px; -fx-text-fill: #185fa5; -fx-cursor: hand; -fx-padding: 4 8;");
                btnLike.setOnAction(e -> {
                    forumService.likercommentaire(c.getId());
                    messageLabel.setText("Like ajoutÃ© !");
                    chargerCommentaires();
                });

                Button btnSuppr = new Button("ðŸ—‘ Supprimer");
                btnSuppr.setStyle("-fx-background-color: transparent; -fx-font-size: 12px; -fx-text-fill: #e74c3c; -fx-cursor: hand; -fx-padding: 4 8;");
                btnSuppr.setOnAction(e -> {
                    // VÃ©rifier si l'utilisateur est l'auteur ou admin
                    if (c.getAuteurId() == SessionManager.getCurrentUser().getId() ||
                            "admin".equals(SessionManager.getCurrentUser().getRole())) {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                                "Supprimer ce commentaire ?", ButtonType.YES, ButtonType.NO);
                        confirm.showAndWait().ifPresent(btn -> {
                            if (btn == ButtonType.YES) {
                                forumService.deletecommentaire(c.getId());
                                messageLabel.setText("Commentaire supprimÃ©.");
                                chargerCommentaires();
                            }
                        });
                    } else {
                        showError("Vous ne pouvez supprimer que vos propres commentaires !");
                    }
                });

                HBox actions = new HBox(8, btnLike, btnSuppr);
                actions.setStyle("-fx-border-color: #eeeeee transparent transparent transparent; -fx-border-width: 1 0 0 0; -fx-padding: 8 0 0 0;");

                VBox carte = new VBox(8, topRow, texte, actions);
                carte.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 10; -fx-padding: 12;");
                carte.setMaxWidth(620);

                HBox wrapper = new HBox(carte);
                wrapper.setStyle("-fx-padding: 4; -fx-background-color: transparent;");
                HBox.setHgrow(carte, Priority.ALWAYS);

                setGraphic(wrapper);
                setStyle("-fx-background-color: transparent;");
            }

            private String getInitiales(String nom) {
                if (nom == null || nom.isEmpty()) return "?";
                String[] parts = nom.trim().split(" ");
                if (parts.length >= 2)
                    return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
                return String.valueOf(parts[0].charAt(0)).toUpperCase();
            }

            private String formatDate(LocalDateTime dt) {
                if (dt == null) return "";
                long minutes = ChronoUnit.MINUTES.between(dt, LocalDateTime.now());
                if (minutes < 1) return "Ã  l'instant";
                if (minutes < 60) return "il y a " + minutes + " min";
                long heures = ChronoUnit.HOURS.between(dt, LocalDateTime.now());
                if (heures < 24) return "il y a " + heures + " h";
                long jours = ChronoUnit.DAYS.between(dt, LocalDateTime.now());
                return "il y a " + jours + " j";
            }
        });
    }

    @FXML
    void publierCommentaireAction(ActionEvent event) {
        String texte = nouveauCommentaireArea.getText().trim();

        if (texte.isEmpty()) {
            showError("Le commentaire ne peut pas Ãªtre vide !");
            return;
        }
        if (texte.length() > 300) {
            showError("Le commentaire ne doit pas dÃ©passer 300 caractÃ¨res !");
            return;
        }

        // âœ… MODÃ‰RATION DU COMMENTAIRE
        String texteModere = ModerationContenu.moderer(texte);
        if (texteModere == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Contenu inappropriÃ©");
            alert.setHeaderText("Commentaire non autorisÃ©");
            alert.setContentText(ModerationContenu.getMessageErreur());
            alert.showAndWait();
            nouveauCommentaireArea.clear();
            return;
        }

        commentaire c = new commentaire(texteModere, LocalDateTime.now(), 0, forumId, SessionManager.getCurrentUser().getId());
        forumService.addcommentaire(c);
        nouveauCommentaireArea.clear();
        messageLabel.setText("âœ… Commentaire publiÃ© !");
        chargerCommentaires();
    }

    @FXML
    void retourListeAction(ActionEvent event) {
        try {
            NavigationManager.navigateFrom(messageLabel, "ADMIN".equals(source) ? "/ForumAdmin.FXML" : retourFxml);
        } catch (IOException e) {
            showError("Erreur retour forum : " + e.getMessage());
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(msg);
        alert.show();
    }
}

