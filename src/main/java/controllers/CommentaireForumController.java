package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import models.commentaire;
import services.forumService;

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

    private forumService forumService = new forumService();
    private int forumId;
    private List<commentaire> commentaireList;
    private String retourFxml = "/ListeForum.fxml"; // par défaut

    // ── Appelé depuis PostDetailsController ou MesForumsController ──
    public void setRetourFxml(String fxml) {
        this.retourFxml = fxml;
    }

    public void initData(int forumId, String titre) {
        this.forumId = forumId;
        titreForum.setText(titre);
        infoForumLabel.setText( " — " + titre);
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
        nbCommentairesLabel.setText("💬 Commentaires (" + commentaireList.size() + ")");
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
                Label initiales = new Label(getInitiales(c.getauteurNom()));
                initiales.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #0c447c;");
                StackPane avatar = new StackPane(cercle, initiales);
                avatar.setMinSize(36, 36);
                avatar.setMaxSize(36, 36);

                Label nom = new Label(c.getauteurNom() != null ? c.getauteurNom() : "Utilisateur");
                nom.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                Label date = new Label("🕐 " + formatDate(c.getDateCommentaire()));
                date.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa;");
                VBox headerBox = new VBox(2, nom, date);

                HBox topRow = new HBox(10, avatar, headerBox);
                topRow.setAlignment(Pos.CENTER_LEFT);

                Label texte = new Label(c.getContenu());
                texte.setWrapText(true);
                texte.setMaxWidth(560);
                texte.setStyle("-fx-font-size: 13px; -fx-text-fill: #333; -fx-padding: 4 0 0 0;");

                Button btnLike = new Button("👍 J'aime · " + c.getLike());
                btnLike.setStyle("-fx-background-color: transparent; -fx-font-size: 12px; -fx-text-fill: #185fa5; -fx-cursor: hand; -fx-padding: 4 8;");
                btnLike.setOnAction(e -> {
                    forumService.likercommentaire(c.getId());
                    messageLabel.setText("Like ajouté !");
                    chargerCommentaires();
                });

                Button btnSuppr = new Button("🗑 Supprimer");
                btnSuppr.setStyle("-fx-background-color: transparent; -fx-font-size: 12px; -fx-text-fill: #e74c3c; -fx-cursor: hand; -fx-padding: 4 8;");
                btnSuppr.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Supprimer ce commentaire ?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.YES) {
                            forumService.deletecommentaire(c.getId());
                            messageLabel.setText("Commentaire supprimé.");
                            chargerCommentaires();
                        }
                    });
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
                if (minutes < 1)  return "à l'instant";
                if (minutes < 60) return "il y a " + minutes + " min";
                long heures = ChronoUnit.HOURS.between(dt, LocalDateTime.now());
                if (heures < 24)  return "il y a " + heures + " h";
                long jours = ChronoUnit.DAYS.between(dt, LocalDateTime.now());
                return "il y a " + jours + " j";
            }
        });
    }

    @FXML
    void publierCommentaireAction(ActionEvent event) {
        String texte = nouveauCommentaireArea.getText().trim();
        if (texte.isEmpty()) {
            showError("Le commentaire ne peut pas être vide !");
            return;
        }
        if (texte.length() > 300) {
            showError("Le commentaire ne doit pas dépasser 300 caractères !");
            return;
        }
        commentaire c = new commentaire(texte, LocalDateTime.now(), 0, forumId, 1);
        forumService.addcommentaire(c);
        nouveauCommentaireArea.clear();
        messageLabel.setText("✅ Commentaire publié !");
        chargerCommentaires();
    }

    @FXML
    void retourListeAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(retourFxml));
            titreForum.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(msg);
        alert.show();
    }
}