
        package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
        import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import models.Reclamation;
import services.NavigationManager;
import services.ReclamationService;
import utils.BadWordsFilter;

public class AjouterReclamationController {

    @FXML private TextField        txtObjet;
    @FXML private TextArea         txtDescription;
    @FXML private ComboBox<String> cbType;
    @FXML private ComboBox<String> cbUrgence;
    @FXML private Label            lblMessage;

    ReclamationService service = new ReclamationService();

    // ─── Mot de passe dédié à l'admin ───────────────────────────────────────
    private static final String ADMIN_PASSWORD = "admin1234";

    @FXML
    public void initialize() {
        cbType.getItems().addAll("Conducteur", "Passager", "Paiement", "Autre");
        cbUrgence.getItems().addAll("Faible", "Moyenne", "Élevée");
    }

    // ────────────────────────────────────────────────────────────────────────
    // Soumettre une réclamation
    // ────────────────────────────────────────────────────────────────────────
    @FXML
    public void ajouterReclamation() {

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

        txtObjet.setStyle("");
        txtDescription.setStyle("");

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

    // ────────────────────────────────────────────────────────────────────────
    // Bouton : Voir les réponses de l'admin (côté utilisateur)
    // ────────────────────────────────────────────────────────────────────────
    @FXML
    public void voirMesReponses() {
        try {
            NavigationManager.navigateFrom(txtObjet, "/views/MesReponses.fxml");
        } catch (Exception e) {
            lblMessage.setText("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Bouton : Voir la liste des réclamations (utilisateur)
    // ────────────────────────────────────────────────────────────────────────
    @FXML
    public void allerVersListeUser() {
        try {
            NavigationManager.navigateFrom(txtObjet, "/views/ListeReclamationUser.fxml");
        } catch (Exception e) {
            lblMessage.setText("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Bouton : Voir la liste des réclamations (admin) — protégée par mdp
    // ────────────────────────────────────────────────────────────────────────
    @FXML
    public void allerVersListeAdmin() {

        // ── Dialogue de saisie du mot de passe ──────────────────────────────
        PasswordField pwField = new PasswordField();
        pwField.setPromptText("Mot de passe");
        pwField.setPrefWidth(250);

        Label lblErreur = new Label();
        lblErreur.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Accès administrateur");
        dialog.setHeaderText("Entrez le mot de passe administrateur");

        ButtonType btnConnexion = new ButtonType("Connexion", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnConnexion, ButtonType.CANCEL);

        // ── Contenu du dialogue sans VBox ────────────────────────────────────
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 15px;");
        grid.add(new Label("Mot de passe :"), 0, 0);
        grid.add(pwField, 0, 1);
        grid.add(lblErreur, 0, 2);
        dialog.getDialogPane().setContent(grid);

        dialog.setOnShown(e -> pwField.requestFocus());

        dialog.getDialogPane()
                .lookupButton(btnConnexion)
                .disableProperty()
                .bind(pwField.textProperty().isEmpty());

        // ── Vérification du mot de passe ────────────────────────────────────
        var result = dialog.showAndWait();

        if (result.isEmpty() || result.get() == ButtonType.CANCEL) return;

        if (!pwField.getText().equals(ADMIN_PASSWORD)) {
            lblMessage.setText("Mot de passe incorrect !");
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        // ── Accès accordé : ouvrir la liste admin ───────────────────────────
        try {
            NavigationManager.navigateFrom(txtObjet, "/views/ListeReclamationAdmin.fxml");
        } catch (Exception e) {
            lblMessage.setText("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}


