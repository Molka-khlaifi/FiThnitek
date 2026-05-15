
package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
        import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Reclamation;
import services.NavigationManager;
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

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // Bouton : Modifier le statut (ChoiceDialog comme l'original)
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @FXML
    public void modifierStatut() {
        Reclamation selected = tableReclamations.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherErreur("SÃ©lectionne une rÃ©clamation !");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(
                selected.getEtat(), "En attente", "En cours", "RÃ©solu", "RejetÃ©"
        );
        dialog.setTitle("Modifier le statut");
        dialog.setHeaderText("RÃ©clamation : " + selected.getObjet());
        dialog.setContentText("Nouveau statut :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newEtat -> {
            service.updateEtat(selected.getId(), newEtat);
            selected.setEtat(newEtat);
            tableReclamations.refresh();
            afficherSucces("Statut modifiÃ© en : " + newEtat);
        });
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // Bouton : Supprimer (avec vÃ©rification des rÃ©ponses liÃ©es)
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @FXML
    public void supprimerReclamation() {
        Reclamation selected = tableReclamations.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherErreur("SÃ©lectionne une rÃ©clamation !");
            return;
        }

        int nbReponses = serviceReponses.getByReclamation(selected.getId()).size();
        String message = nbReponses > 0
                ? "Cette rÃ©clamation a " + nbReponses + " rÃ©ponse(s) liÃ©e(s).\nElles seront aussi supprimÃ©es. Confirmer ?"
                : "Supprimer la rÃ©clamation #" + selected.getId() + " ?";

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmation de suppression");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            service.delete(selected);
            tableReclamations.getItems().remove(selected);
            afficherSucces("SupprimÃ©e avec succÃ¨s !");
        }
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // Bouton : Voir / RÃ©pondre (mÃªme logique que l'original)
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @FXML
    public void voirReponses() {
        Reclamation selected = tableReclamations.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherErreur("Selectionne une reclamation !");
            return;
        }

        try {
            NavigationManager.navigateFrom(tableReclamations, "/views/RepondreReclamation.fxml",
                    (ReponseReclamationController ctrl) -> {
                        ctrl.setIdReclamation(selected.getId());
                        ctrl.setLabelReclamation("Reclamation #" + selected.getId() + " - " + selected.getObjet());
                    });
        } catch (Exception e) {
            afficherErreur("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Bouton : Statistiques
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @FXML
    public void voirStatistiques() {
        try {
            NavigationManager.navigateFrom(tableReclamations, "/views/Statistiques.fxml");
        } catch (Exception e) {
            afficherErreur("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Bouton : Retour vers Ajouter RÃ©clamation
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @FXML
    public void retourAjouter() {
        try {
            NavigationManager.navigateFrom(tableReclamations, "/views/AjouterReclamation.fxml");
        } catch (Exception e) {
            afficherErreur("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helpers
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private void afficherErreur(String msg) {
        lblInfo.setText(msg);
        lblInfo.setStyle("-fx-text-fill: red;");
    }

    private void afficherSucces(String msg) {
        lblInfo.setText(msg);
        lblInfo.setStyle("-fx-text-fill: green;");
    }
}

