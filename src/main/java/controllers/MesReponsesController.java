package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
        import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Reclamation;
import models.ReponseReclamation;
import services.NavigationManager;
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

    // âœ… ID de l'utilisateur connectÃ© (Ã  remplacer par la session rÃ©elle)
    private final int ID_USER_CONNECTE = 1;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colObjet.setCellValueFactory(new PropertyValueFactory<>("objet"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colUrgence.setCellValueFactory(new PropertyValueFactory<>("urgence"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));

        // âœ… Charger uniquement les rÃ©clamations de cet utilisateur
        List<Reclamation> toutes = serviceRec.getAll();
        for (Reclamation r : toutes) {
            if (r.getIdUser() == ID_USER_CONNECTE) {
                tableReclamations.getItems().add(r);
            }
        }

        // Message si aucune rÃ©clamation
        if (tableReclamations.getItems().isEmpty()) {
            lblInfo.setText("Vous n'avez aucune rÃ©clamation pour l'instant.");
            lblInfo.setStyle("-fx-text-fill: #888;");
        }
    }

    @FXML
    public void voirReponse() {
        Reclamation selected = tableReclamations.getSelectionModel().getSelectedItem();

        if (selected == null) {
            lblInfo.setText("SÃ©lectionne une rÃ©clamation !");
            lblInfo.setStyle("-fx-text-fill: red;");
            return;
        }

        List<ReponseReclamation> reponses = serviceReponses.getByReclamation(selected.getId());

        if (reponses.isEmpty()) {
            txtReponseAdmin.setText("");
            lblInfo.setText("â³ Pas encore de rÃ©ponse de l'admin pour cette rÃ©clamation.");
            lblInfo.setStyle("-fx-text-fill: #e67e22;");
        } else {
            // âœ… Afficher toutes les rÃ©ponses de l'admin
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < reponses.size(); i++) {
                ReponseReclamation rep = reponses.get(i);
                if (i > 0) sb.append("\nâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€\n");
                sb.append("RÃ©ponse #").append(i + 1).append(" :\n");
                sb.append(rep.getMessage());
                if (rep.getDate() != null) {
                    sb.append("\nðŸ“… ").append(rep.getDate().toLocalDate());
                }
            }
            txtReponseAdmin.setText(sb.toString());
            lblInfo.setText("âœ” " + reponses.size() + " rÃ©ponse(s) trouvÃ©e(s).");
            lblInfo.setStyle("-fx-text-fill: green;");
        }
    }

    @FXML
    public void retourAjouter() {
        try {
            NavigationManager.navigateFrom(tableReclamations, "/views/AjouterReclamation.fxml");
        } catch (Exception e) {
            lblInfo.setText("Erreur navigation : " + e.getMessage());
            e.printStackTrace();
        }
    }
}

