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

public class ListeForumController {

    @FXML private ListView<String> publicationListView;
    @FXML private TextField searchTextField;
    @FXML private TextField idTextField;
    @FXML private ComboBox<String> categorieFilterComboBox;
    @FXML private Label statsLabel;
    @FXML private Label messageLabel;

    private forumService forumService = new forumService();
    private List<publication> publicationList;

    // ───────────────── INIT ─────────────────
    @FXML
    public void initialize() {

        categorieFilterComboBox.getItems().addAll("Tous", "annonce", "question", "discussion");
        categorieFilterComboBox.setValue("Tous");

        chargerpublications();

        publicationListView.setOnMouseClicked(event -> {
            int index = publicationListView.getSelectionModel().getSelectedIndex();
            if (index >= 0 && index < publicationList.size()) {
                idTextField.setText(String.valueOf(publicationList.get(index).getId()));
            }
        });
    }

    // ───────────────── CHARGER ─────────────────
    private void chargerpublications() {
        publicationList = forumService.getAll();
        afficher(publicationList);
        statsLabel.setText(publicationList.size() + " posts");
    }

    // ───────────────── AFFICHAGE ─────────────────
    private void afficher(List<publication> list) {

        publicationListView.getItems().clear();

        for (publication f : list) {

            String epingle = f.isEpingle() ? "📌 " : "";

            String ligne = epingle +
                    "[" + f.getId() + "] " +
                    f.getTitre() +
                    " | " + f.getCategorie().toUpperCase() +
                    " | " + f.getStatut() +
                    " | " + f.getNb_vues() + " vues";

            publicationListView.getItems().add(ligne);
        }
    }

    // ───────────────── RECHERCHE ─────────────────
    @FXML
    void rechercherAction(ActionEvent event) {

        String keyword = searchTextField.getText().trim();

        if (keyword.isEmpty()) {
            chargerpublications();
            return;
        }

        publicationList = forumService.rechercher(keyword);
        afficher(publicationList);
        statsLabel.setText(publicationList.size() + " résultats");
    }

    // ───────────────── FILTRE ─────────────────
    @FXML
    void filtrerCategorieAction(ActionEvent event) {

        String cat = categorieFilterComboBox.getValue();

        if (cat == null || cat.equals("Tous")) {
            chargerpublications();
        } else {
            publicationList = forumService.getByCategorie(cat);
            afficher(publicationList);
            statsLabel.setText(publicationList.size() + " posts");
        }
    }

    // ───────────────── TRI ─────────────────
    @FXML
    void trierParDateAction(ActionEvent event) {
        publicationList = forumService.trierParDate();
        afficher(publicationList);
        messageLabel.setText("Tri par date");
    }

    @FXML
    void trierParVuesAction(ActionEvent event) {
        publicationList = forumService.trierParVues();
        afficher(publicationList);
        messageLabel.setText("Tri par vues");
    }

    // ───────────────── AJOUT ─────────────────
    @FXML
    void ajouterAction(ActionEvent event) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Ajouterpublication.fxml"));
            publicationListView.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur ajout : " + e.getMessage());
        }
    }

    // ───────────────── MODIFIER ─────────────────
    @FXML
    void modifierAction(ActionEvent event) {

        int id = getId();
        if (id == -1) return;

        publication f = forumService.getById(id);

        if (f == null) {
            showError("Post introuvable !");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Modifierpublication.fxml"));
            Parent root = loader.load();

            ModifierForumController ctrl = loader.getController();
            ctrl.initData(f);

            publicationListView.getScene().setRoot(root);

        } catch (IOException e) {
            System.out.println("Erreur modifier : " + e.getMessage());
        }
    }

    // ───────────────── SUPPRIMER ─────────────────
    @FXML
    void supprimerAction(ActionEvent event) {

        int id = getId();
        if (id == -1) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer post #" + id + " ?",
                ButtonType.YES, ButtonType.NO);

        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                forumService.delete(forumService.getById(id));
                chargerpublications();
                messageLabel.setText("Supprimé !");
            }
        });
    }

    // ───────────────── EPINGLER ─────────────────
    @FXML
    void epinglerAction(ActionEvent event) {

        int id = getId();
        if (id == -1) return;

        forumService.toggleEpingle(id);

        chargerpublications();
        messageLabel.setText("État épingle modifié");
    }

    // ───────────────── commentaireS ─────────────────
    @FXML
    void voircommentairesAction(ActionEvent event) {

        int id = getId();
        if (id == -1) return;

        publication f = forumService.getById(id);

        if (f == null) {
            showError("Post introuvable !");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commentairepublication.fxml"));
            Parent root = loader.load();

            CommentaireForumController ctrl = loader.getController();
            ctrl.initData(f.getId(), f.getTitre());

            publicationListView.getScene().setRoot(root);

        } catch (IOException e) {
            System.out.println("Erreur commentaires : " + e.getMessage());
        }
    }

    // ───────────────── HELPERS ─────────────────
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