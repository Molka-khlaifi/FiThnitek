package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import models.publication;
import services.forumService;

import java.io.IOException;
import java.util.List;

public class MesForumsController {

    @FXML private ListView<String> mesForumsListView;
    @FXML private TextField idTextField;
    @FXML private Label statsLabel;
    @FXML private Label messageLabel;

    private forumService forumService = new forumService();
    private List<publication> mesPosts;

    @FXML
    public void initialize() {
        chargerMesPosts();

        mesForumsListView.setOnMouseClicked(event -> {
            int index = mesForumsListView.getSelectionModel().getSelectedIndex();
            if (index >= 0 && index < mesPosts.size()) {
                idTextField.setText(String.valueOf(mesPosts.get(index).getId()));
            }
        });
    }

    private void chargerMesPosts() {
        mesPosts = forumService.getAll(); // remplace par getByUser() si tu as cette méthode
        afficher(mesPosts);
        statsLabel.setText(mesPosts.size() + " posts");
    }

    private void afficher(List<publication> list) {
        mesForumsListView.getItems().clear();
        for (publication f : list) {
            String epingle = f.isEpingle() ? "📌 " : "";
            mesForumsListView.getItems().add(
                    epingle + "[" + f.getId() + "] " + f.getTitre() +
                            " | " + f.getCategorie().toUpperCase() +
                            " | " + f.getStatut()
            );
        }
    }

    @FXML
    void ajouterAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterForum.fxml"));
            mesForumsListView.getScene().setRoot(root);
        } catch (IOException e) {
            showError("Erreur chargement : " + e.getMessage());
        }
    }

    @FXML
    void modifierAction(ActionEvent event) {
        int id = getId();
        if (id == -1) return;
        // TODO: ouvrir la vue de modification avec l'ID
        messageLabel.setText("Modifier post #" + id);
    }

    @FXML
    void supprimerAction(ActionEvent event) {
        int id = getId();
        if (id == -1) return;
        publication p = forumService.getById(id);
        if (p == null) { showError("Post introuvable !"); return; }
        forumService.delete(p);
        messageLabel.setText("Post #" + id + " supprimé.");
        chargerMesPosts();
    }

    @FXML
    void epinglerAction(ActionEvent event) {
        int id = getId();
        if (id == -1) return;
        // TODO: appeler forumService.epingler(id)
        messageLabel.setText("Post #" + id + " épinglé.");
        chargerMesPosts();
    }

    @FXML
    void voirCommentairesAction(ActionEvent event) {
        int id = getId();
        if (id == -1) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentaireForum.fxml"));
            Parent root = loader.load();
            CommentaireForumController ctrl = loader.getController();
            ctrl.initData(id, "");
            mesForumsListView.getScene().setRoot(root);
        } catch (IOException e) {
            showError("Erreur commentaires : " + e.getMessage());
        }
    }

    @FXML
    void retourListeAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ListeForum.fxml"));
            mesForumsListView.getScene().setRoot(root);
        } catch (IOException e) {
            showError("Erreur retour : " + e.getMessage());
        }
    }

    private int getId() {
        try {
            return Integer.parseInt(idTextField.getText().trim());
        } catch (Exception e) {
            showError("ID invalide !");
            return -1;
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(msg);
        a.show();
    }
}