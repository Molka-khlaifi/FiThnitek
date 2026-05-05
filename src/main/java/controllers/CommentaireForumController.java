package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import models.commentaire;
import services.forumService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class CommentaireForumController {

    @FXML private Label            titreForum;
    @FXML private Label            infoForumLabel;
    @FXML private Label            nbCommentairesLabel;
    @FXML private ListView<String> commentairesListView;
    @FXML private TextField        idCommentaireTextField;
    @FXML private TextArea         nouveauCommentaireArea;
    @FXML private Label            messageLabel;

    private forumService  forumService = new forumService();
    private int           forumId;
    private List<commentaire> commentaireList;

    // ─── Recevoir le forumId et titre depuis ListeForumController ─────────
    public void initData(int forumId, String titre) {
        this.forumId = forumId;
        titreForum.setText(titre);
        infoForumLabel.setText("Post #" + forumId + " — " + titre);

        // Incrémenter vues
        forumService.incrementerVues(forumId);
        chargerCommentaires();
    }

    // ─── Charger les commentaires du forum ────────────────────────────────
    private void chargerCommentaires() {
        commentairesListView.getItems().clear();
        commentaireList = forumService.getcommentairesBypublication(forumId);
        nbCommentairesLabel.setText("Commentaires (" + commentaireList.size() + ")");

        for (commentaire c : commentaireList) {
            String ligne = "[#" + c.getId() + "]  "
                    + "User#" + c.getAuteurId()
                    + "  ·  " + c.getDateCommentaire().toLocalDate()
                    + "   →   " + c.getContenu()
                    + "      ❤ " + c.getLike() + " likes";
            commentairesListView.getItems().add(ligne);
        }

        // Clic auto-remplir id commentaire
        commentairesListView.setOnMouseClicked(event -> {
            int index = commentairesListView.getSelectionModel().getSelectedIndex();
            if (index >= 0 && index < commentaireList.size()) {
                idCommentaireTextField.setText(
                        String.valueOf(commentaireList.get(index).getId())
                );
            }
        });
    }

    // ─── Publier un commentaire ───────────────────────────────────────────
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

        commentaire c = new commentaire(
                texte, LocalDateTime.now(),
                0, forumId, 1   // auteur_id=1, remplacer par user connecté
        );
        forumService.addcommentaire(c);
        nouveauCommentaireArea.clear();
        messageLabel.setText("Commentaire publié avec succès !");
        chargerCommentaires();
    }

    // ─── Liker un commentaire ─────────────────────────────────────────────
    @FXML
    void likerAction(ActionEvent event) {
        int id = getIdCommentaire();
        if (id == -1) return;
        forumService.likercommentaire(id);
        messageLabel.setText("Like ajouté au commentaire #" + id);
        chargerCommentaires();
    }

    // ─── Supprimer un commentaire ─────────────────────────────────────────
    @FXML
    void supprimerCommentaireAction(ActionEvent event) {
        int id = getIdCommentaire();
        if (id == -1) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer le commentaire #" + id + " ?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                forumService.deletecommentaire(id);
                messageLabel.setText("Commentaire #" + id + " supprimé.");
                chargerCommentaires();
            }
        });
    }

    // ─── Retour liste ─────────────────────────────────────────────────────
    @FXML
    void retourListeAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ListeForum.fxml"));
            titreForum.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────
    private int getIdCommentaire() {
        try {
            return Integer.parseInt(idCommentaireTextField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Veuillez entrer un ID de commentaire valide !");
            return -1;
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(msg);
        alert.show();
    }
}
