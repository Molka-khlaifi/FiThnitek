
package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
        import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.ReponseReclamation;
import services.NavigationManager;
import services.ReponseReclamationService;

import java.time.LocalDateTime;

public class ReponseReclamationController {

    @FXML private TextArea                                       txtReponse;
    @FXML private Label                                          lblMessage;
    @FXML private Label                                          lblReclamation;
    @FXML private TableView<ReponseReclamation>                  tableReponses;
    @FXML private TableColumn<ReponseReclamation, Integer>       colIdRec;
    @FXML private TableColumn<ReponseReclamation, String>        colMessage;
    @FXML private TableColumn<ReponseReclamation, LocalDateTime> colDate;

    ReponseReclamationService service = new ReponseReclamationService();

    private int idReclamation;

    @FXML
    public void initialize() {
        colIdRec.setCellValueFactory(new PropertyValueFactory<>("idReclamation"));
        colMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    public void setIdReclamation(int id) {
        this.idReclamation = id;
        chargerReponses();
    }

    public void setLabelReclamation(String texte) {
        lblReclamation.setText(texte);
    }

    private void chargerReponses() {
        tableReponses.getItems().setAll(service.getByReclamation(idReclamation));
    }

    @FXML
    public void envoyerReponse() {
        if (txtReponse.getText().isEmpty()) {
            lblMessage.setText("RÃ©ponse vide !");
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        ReponseReclamation r = new ReponseReclamation(
                idReclamation,
                1,
                txtReponse.getText()
        );

        service.add(r);

        lblMessage.setText("RÃ©ponse envoyÃ©e avec succÃ¨s !");
        lblMessage.setStyle("-fx-text-fill: green;");

        txtReponse.clear();
        chargerReponses();
    }

    // âœ… Retour vers la liste admin (au lieu de l'ancien ListeReclamation.fxml)
    @FXML
    public void retourListe() {
        try {
            NavigationManager.navigateFrom(txtReponse, "/views/ListeReclamationAdmin.fxml");
        } catch (Exception e) {
            lblMessage.setText("Erreur navigation : " + e.getMessage());
            e.printStackTrace();
        }
    }
}




