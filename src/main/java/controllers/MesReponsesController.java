package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
        import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Reclamation;
import models.ReponseReclamation;
import services.ReclamationService;
import services.ReponseReclamationService;

import java.util.List;

public class MesReponsesController {

    @FXML private TableView<Reclamation>            tableReclamations;
    @FXML private TableColumn<Reclamation, Integer> colId;
    @FXML private TableColumn<Reclamation, String>  colObjet;
    @FXML private TableColumn<Reclamation, String>  colType;
    @FXML private TableColumn<Reclamation, String>  colUrgence;
    @FXML private TableColumn<Reclamation, String>  colEtat;
    @FXML private TextArea                          txtReponseAdmin;
    @FXML private Label                             lblInfo;

    ReclamationService        serviceRec      = new ReclamationService();
    ReponseReclamationService serviceReponses = new ReponseReclamationService();

    // ✅ ID de l'utilisateur connecté (à remplacer par la session réelle)
    private final int ID_USER_CONNECTE = 1;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colObjet.setCellValueFactory(new PropertyValueFactory<>("objet"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colUrgence.setCellValueFactory(new PropertyValueFactory<>("urgence"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));

        // ✅ Charger uniquement les réclamations de cet utilisateur
        List<Reclamation> toutes = serviceRec.getAll();
        for (Reclamation r : toutes) {
            if (r.getIdUser() == ID_USER_CONNECTE) {
                tableReclamations.getItems().add(r);
            }
        }

        // Message si aucune réclamation
        if (tableReclamations.getItems().isEmpty()) {
            lblInfo.setText("Vous n'avez aucune réclamation pour l'instant.");
            lblInfo.setStyle("-fx-text-fill: #888;");
        }
    }

    @FXML
    public void voirReponse() {
        Reclamation selected = tableReclamations.getSelectionModel().getSelectedItem();

        if (selected == null) {
            lblInfo.setText("Sélectionne une réclamation !");
            lblInfo.setStyle("-fx-text-fill: red;");
            return;
        }

        List<ReponseReclamation> reponses = serviceReponses.getByReclamation(selected.getId());

        if (reponses.isEmpty()) {
            txtReponseAdmin.setText("");
            lblInfo.setText("⏳ Pas encore de réponse de l'admin pour cette réclamation.");
            lblInfo.setStyle("-fx-text-fill: #e67e22;");
        } else {
            // ✅ Afficher toutes les réponses de l'admin
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < reponses.size(); i++) {
                ReponseReclamation rep = reponses.get(i);
                if (i > 0) sb.append("\n─────────────────────\n");
                sb.append("Réponse #").append(i + 1).append(" :\n");
                sb.append(rep.getMessage());
                if (rep.getDate() != null) {
                    sb.append("\n📅 ").append(rep.getDate().toLocalDate());
                }
            }
            txtReponseAdmin.setText(sb.toString());
            lblInfo.setText("✔ " + reponses.size() + " réponse(s) trouvée(s).");
            lblInfo.setStyle("-fx-text-fill: green;");
        }
    }

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
            lblInfo.setText("Erreur navigation : " + e.getMessage());
            e.printStackTrace();
        }
    }
}