package controllers;

import models.Role;
import models.UserEntry;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditUserController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private TextField cinField;
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private Label messageLabel;

    private UserEntry currentUser;
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
    }

    public void setUser(UserEntry user) {
        this.currentUser = user;
        nomField.setText(user.getNom());
        prenomField.setText(user.getPrenom());
        emailField.setText(user.getEmail());
        telephoneField.setText(user.getNumeroTelephone());
        cinField.setText(user.getCin());
        roleComboBox.setValue(user.getRole());
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (!validateFields()) return;

        String query = "UPDATE utilisateur SET nom = ?, prenom = ?, email = ?, telephone = ?, cin = ?, role = ? WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConn();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, nomField.getText().trim());
            pstmt.setString(2, prenomField.getText().trim());
            pstmt.setString(3, emailField.getText().trim());
            pstmt.setString(4, telephoneField.getText().trim());
            pstmt.setString(5, cinField.getText().trim());
            pstmt.setString(6, roleComboBox.getValue().toString());
            pstmt.setInt(7, currentUser.getId());

            pstmt.executeUpdate();
            saveClicked = true;
            closeStage();

        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("Duplicate entry")) {
                messageLabel.setText("Email ou CIN déjà utilisé.");
            } else {
                messageLabel.setText("Erreur lors de la mise à jour.");
            }
            messageLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private boolean validateFields() {
        if (nomField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty() || 
            cinField.getText().trim().isEmpty() || roleComboBox.getValue() == null) {
            messageLabel.setText("Veuillez remplir les champs obligatoires.");
            messageLabel.setStyle("-fx-text-fill: #e74c3c;");
            return false;
        }
        return true;
    }
}
