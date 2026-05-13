
package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.ReponseReclamation;
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
            lblMessage.setText("Réponse vide !");
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        ReponseReclamation r = new ReponseReclamation(
                idReclamation,
                1, // id admin
                txtReponse.getText()
        );

        service.add(r);

        lblMessage.setText("Réponse envoyée avec succès !");
        lblMessage.setStyle("-fx-text-fill: green;");

        txtReponse.clear();
        chargerReponses();
    }

    // ✅ Retour vers la liste des réclamations
    @FXML
    public void retourListe() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/ListeReclamation.fxml")
            );
            Stage stage = (Stage) txtReponse.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Liste des Réclamations");
        } catch (Exception e) {
            lblMessage.setText("Erreur navigation : " + e.getMessage());
            e.printStackTrace();
        }
    }
}

