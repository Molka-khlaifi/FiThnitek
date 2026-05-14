
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

public class ListeReclamationUserController {

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
    // Bouton : Supprimer (avec vérification des réponses liées)
    // ────────────────────────────────────────────────────────────────────────
    @FXML
    public void supprimerReclamation() {
        Reclamation selected = tableReclamations.getSelectionModel().getSelectedItem();
        if (selected == null) {
            lblInfo.setText("Sélectionne une réclamation !");
            lblInfo.setStyle("-fx-text-fill: orange;");
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
            lblInfo.setText("Réclamation supprimée avec succès !");
            lblInfo.setStyle("-fx-text-fill: green;");
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Bouton : Retour vers Ajouter Réclamation
    // ────────────────────────────────────────────────────────────────────────
    @FXML
    public void allerVersAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/AjouterReclamation.fxml")
            );
            Stage stage = (Stage) tableReclamations.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Ajouter une Réclamation");
        } catch (Exception e) {
            lblInfo.setText("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}


