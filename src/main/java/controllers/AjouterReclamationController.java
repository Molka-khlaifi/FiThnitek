
package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Reclamation;
import services.ReclamationService;
import utils.BadWordsFilter;

public class AjouterReclamationController {

    @FXML private TextField        txtObjet;
    @FXML private TextArea         txtDescription;
    @FXML private ComboBox<String> cbType;
    @FXML private ComboBox<String> cbUrgence;
    @FXML private Label            lblMessage;

    ReclamationService service = new ReclamationService();

    @FXML
    public void initialize() {
        cbType.getItems().addAll("Conducteur", "Passager", "Paiement", "Autre");
        cbUrgence.getItems().addAll("Faible", "Moyenne", "Élevée");
    }

    @FXML
    public void ajouterReclamation() {

        // 1. Vérification champs vides
        if (txtObjet.getText().isEmpty() ||
                txtDescription.getText().isEmpty() ||
                cbType.getValue() == null ||
                cbUrgence.getValue() == null) {
            lblMessage.setText("Tous les champs sont obligatoires !");
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        String objet       = txtObjet.getText();
        String description = txtDescription.getText();

        // ✅ 2. Filtre Bad Words
        if (BadWordsFilter.containsBadWordInAny(objet, description)) {
            String motTrouve = BadWordsFilter.getFoundBadWord(objet);
            if (motTrouve.isEmpty()) motTrouve = BadWordsFilter.getFoundBadWord(description);

            lblMessage.setText("Langage inapproprié détecté (\"" + motTrouve + "\"). Veuillez reformuler.");
            lblMessage.setStyle("-fx-text-fill: red;");

            if (BadWordsFilter.containsBadWord(objet))
                txtObjet.setStyle("-fx-border-color: red; -fx-border-width: 1.5px;");
            if (BadWordsFilter.containsBadWord(description))
                txtDescription.setStyle("-fx-border-color: red; -fx-border-width: 1.5px;");
            return;
        }

        // Remettre styles normaux
        txtObjet.setStyle("");
        txtDescription.setStyle("");

        // 3. Ajout en base
        Reclamation r = new Reclamation(
                1,
                objet,
                description,
                cbType.getValue(),
                cbUrgence.getValue()
        );

        service.add(r);

        lblMessage.setText("Réclamation ajoutée avec succès !");
        lblMessage.setStyle("-fx-text-fill: green;");

        txtObjet.clear();
        txtDescription.clear();
        cbType.setValue(null);
        cbUrgence.setValue(null);
    }

    @FXML
    public void voirMesReponses() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MesReponses.fxml"));
            Stage stage = (Stage) txtObjet.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Mes Réclamations et Réponses");
        } catch (Exception e) {
            lblMessage.setText("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void allerVersListe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ListeReclamation.fxml"));
            Stage stage = (Stage) txtObjet.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Liste des Réclamations");
        } catch (Exception e) {
            lblMessage.setText("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
