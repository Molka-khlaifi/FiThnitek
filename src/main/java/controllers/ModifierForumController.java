package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.publication;
import services.forumService;
import services.NavigationManager;

public class ModifierForumController {

    @FXML private TextField        idTextField;
    @FXML private TextField        titreTextField;
    @FXML private TextArea         contenuTextArea;
    @FXML private ComboBox<String> categorieComboBox;
    @FXML private ComboBox<String> statutComboBox;
    @FXML private Label            erreurLabel;

    private publication  forumAModifier;
    private forumService forumService = new forumService();

    @FXML
    public void initialize() {
        categorieComboBox.getItems().addAll("question", "discussion", "autre");
        statutComboBox.getItems().addAll("ouvert", "ferme");
    }

    public void initData(publication forum) {
        this.forumAModifier = forum;
        idTextField.setText(String.valueOf(forum.getId()));
        titreTextField.setText(forum.getTitre());
        contenuTextArea.setText(forum.getContenu());
        categorieComboBox.setValue(forum.getCategorie());
        statutComboBox.setValue(forum.getStatut());
    }

    @FXML
    void modifierForumAction(ActionEvent event) {
        erreurLabel.setText("");
        String titre    = titreTextField.getText().trim();
        String contenu  = contenuTextArea.getText().trim();
        String categorie = categorieComboBox.getValue();
        String statut   = statutComboBox.getValue();

        if (titre.isEmpty()) { erreurLabel.setText("Le titre est obligatoire !"); return; }
        if (titre.length() < 5) { erreurLabel.setText("Le titre doit avoir au moins 5 caractÃ¨res !"); return; }
        if (contenu.isEmpty()) { erreurLabel.setText("Le contenu est obligatoire !"); return; }

        forumAModifier.setTitre(titre);
        forumAModifier.setContenu(contenu);
        forumAModifier.setCategorie(categorie);
        forumAModifier.setStatut(statut);

        forumService.update(forumAModifier);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("SuccÃ¨s");
        alert.setHeaderText("Post modifiÃ© avec succÃ¨s !");
        alert.show();

        // âœ… Retour dans le conteneur FORUM, pas de setRoot()
        try {
            NavigationManager.navigateFrom(erreurLabel, "/MesForums.FXML");
        } catch (Exception e) {
            erreurLabel.setText("Erreur retour forum : " + e.getMessage());
        }
    }

    @FXML
    void annulerAction(ActionEvent event) {
        // âœ… Retour dans le conteneur FORUM
        try {
            NavigationManager.navigateFrom(erreurLabel, "/MesForums.FXML");
        } catch (Exception e) {
            erreurLabel.setText("Erreur retour forum : " + e.getMessage());
        }
    }
}

