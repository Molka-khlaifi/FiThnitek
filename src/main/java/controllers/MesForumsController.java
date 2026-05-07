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
        // double click
        mesForumsListView.setOnMouseClicked(event -> {
            int index = mesForumsListView.getSelectionModel().getSelectedIndex();

            if (index >= 0 && index < mesPosts.size()) {
                publication selected = mesPosts.get(index);
                idTextField.setText(String.valueOf(selected.getId()));

                if (event.getClickCount() == 2) {
                    afficherDetailsPost(selected);
                }
            }
        });
    }

    private void chargerMesPosts() {
        mesPosts = forumService.getAll();
        mesPosts.sort((p1, p2) -> Boolean.compare(p2.isEpingle(), p1.isEpingle()));
        afficher(mesPosts);
        statsLabel.setText(mesPosts.size() + " posts");
    }

    @FXML
    private void afficherDetailsPost(publication post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PostDetails.fxml"));
            Parent root = loader.load();

            PostDetailsController controller = loader.getController();
            controller.setPost(post);

            mesForumsListView.getScene().setRoot(root);

            forumService.incrementerVues(post.getId());

        } catch (IOException e) {
            System.out.println("Erreur affichage détails : " + e.getMessage());
        }
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

    @FXML void modifierAction(ActionEvent event) {
        int id = getId();
        if (id == -1) return;

        publication p = forumService.getById(id);

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierForum.fxml"));
            Parent root = loader.load();
            ModifierForumController controller = loader.getController();
            controller.initData(p);
            mesForumsListView.getScene().setRoot(root);

        } catch (IOException e) {

            showError("Erreur modification : " + e.getMessage());
        }
        // TODO: ouvrir la vue de modification avec l'ID
        messageLabel.setText("Modifier post #" + id); }

    @FXML
    void supprimerAction(ActionEvent event) {
        int id = getId();
        if (id == -1) return;
        publication p = forumService.getById(id);
        if (p == null) { showError("Post introuvable !"); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Suppression du post");
        alert.setContentText("Voulez-vous vraiment supprimer ce post ?");
        ButtonType oui = new ButtonType("Oui");
        ButtonType non = new ButtonType("Annuler");
        alert.getButtonTypes().setAll(oui, non);
        alert.showAndWait().ifPresent(response -> {
                    if (response == oui) {

                        forumService.delete(p);

                        messageLabel.setText("✅ Post #" + id + " supprimé.");

                        chargerMesPosts();

                    } else {

                        messageLabel.setText("❌ Suppression annulée.");
                    }
                });
        messageLabel.setText("Post #" + id + " supprimé.");
        chargerMesPosts();
    }

    @FXML
    void epinglerAction(ActionEvent event) {
        int id = getId();
        if (id == -1) return;
        forumService.toggleEpingle(id);
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