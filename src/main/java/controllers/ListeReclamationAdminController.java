
package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
        import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Reclamation;
import services.ReclamationService;
import services.ReponseReclamationService;

import java.util.Optional;

public class ListeReclamationAdminController {

    @FXML
    private TableView<Reclamation> tableReclamations;
    @FXML private TableColumn<Reclamation, Integer> colId;
    @FXML private TableColumn<Reclamation, String>  colObjet;
    @FXML private TableColumn<Reclamation, String>  colType;
    @FXML private TableColumn<Reclamation, String>  colUrgence;
    @FXML private TableColumn<Reclamation, String>  colEtat;
    @FXML private Label lblInfo;

    ReclamationService service         = new ReclamationService();
    ReponseReclamationService serviceReponses = new ReponseReclamationService();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colObjet.setCellValueFactory(new PropertyValueFactory<>("objet"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colUrgence.setCellValueFactory(new PropertyValueFactory<>("urgence"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));
        tableReclamations.getItems().addAll(service.getAll());
    }

    // ────────────────────────────────────────────────────────────────────────
    // Bouton : Modifier le statut (ChoiceDialog comme l'original)
    // ────────────────────────────────────────────────────────────────────────
    @FXML
    public void modifierStatut() {
        Reclamation selected = tableReclamations.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherErreur("Sélectionne une réclamation !");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(
                selected.getEtat(), "En attente", "En cours", "Résolu", "Rejeté"
        );
        dialog.setTitle("Modifier le statut");
        dialog.setHeaderText("Réclamation : " + selected.getObjet());
        dialog.setContentText("Nouveau statut :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newEtat -> {
            service.updateEtat(selected.getId(), newEtat);
            selected.setEtat(newEtat);
            tableReclamations.refresh();
            afficherSucces("Statut modifié en : " + newEtat);
        });
    }

    // ────────────────────────────────────────────────────────────────────────
    // Bouton : Supprimer (avec vérification des réponses liées)
    // ────────────────────────────────────────────────────────────────────────
    @FXML
    public void supprimerReclamation() {
        Reclamation selected = tableReclamations.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherErreur("Sélectionne une réclamation !");
            return;
        }

        int nbReponses = serviceReponses.getByReclamation(selected.getId()).size();
        String message = nbReponses > 0
                ? "Cette réclamation a " + nbReponses + " réponse(s) liée(s).\nElles seront aussi supprimées. Confirmer ?"
                : "Supprimer la réclamation #" + selected.getId() + " ?";

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmation de suppression");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            service.delete(selected);
            tableReclamations.getItems().remove(selected);
            afficherSucces("Supprimée avec succès !");
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Bouton : Voir / Répondre (même logique que l'original)
    // ────────────────────────────────────────────────────────────────────────
    @FXML
    public void voirReponses() {
        Reclamation selected = tableReclamations.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherErreur("Sélectionne une réclamation !");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/RepondreReclamation.fxml")
            );
            Stage stage = (Stage) tableReclamations.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            ReponseReclamationController ctrl = loader.getController();
            ctrl.setIdReclamation(selected.getId());
            ctrl.setLabelReclamation("Réclamation #" + selected.getId() + " — " + selected.getObjet());

            stage.setTitle("Répondre — Réclamation #" + selected.getId());
        } catch (Exception e) {
            afficherErreur("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Bouton : Statistiques
    // ────────────────────────────────────────────────────────────────────────
    @FXML
    public void voirStatistiques() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/Statistiques.fxml")
            );
            Stage stage = (Stage) tableReclamations.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Statistiques des Réclamations");
        } catch (Exception e) {
            afficherErreur("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Bouton : Retour vers Ajouter Réclamation
    // ────────────────────────────────────────────────────────────────────────
    @FXML
    public void retourAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/AjouterReclamation.fxml")
            );
            Stage stage = (Stage) tableReclamations.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Ajouter une Réclamation");
        } catch (Exception e) {
            afficherErreur("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Helpers
    // ────────────────────────────────────────────────────────────────────────
    private void afficherErreur(String msg) {
        lblInfo.setText(msg);
        lblInfo.setStyle("-fx-text-fill: red;");
    }

    private void afficherSucces(String msg) {
        lblInfo.setText(msg);
        lblInfo.setStyle("-fx-text-fill: green;");
    }
}
