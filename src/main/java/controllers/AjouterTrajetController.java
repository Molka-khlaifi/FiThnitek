package controllers;

import entities.Trajet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import services.ServiceTrajet;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class AjouterTrajetController implements Initializable {

    @FXML private TextField txtDepart;
    @FXML private TextField txtDestination;
    @FXML private TextField txtDateDepart;
    @FXML private TextField txtDateArrivee;
    @FXML private TextField txtPrix;
    @FXML private TextField txtPlaces;
    @FXML private TextField txtConducteurNom;
    @FXML private TextField txtConducteurTel;
    @FXML private TextField txtMatricule;
    @FXML private TextArea  txtDescription;
    @FXML private Label     lblMessage;

    private final ServiceTrajet service = new ServiceTrajet();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // nothing needed at init
    }

    @FXML
    private void handleAjouter() {
        lblMessage.getStyleClass().removeAll("msg-success", "msg-error");
        lblMessage.setText("");

        if (isEmpty(txtDepart) || isEmpty(txtDestination) || isEmpty(txtDateDepart)
                || isEmpty(txtDateArrivee) || isEmpty(txtPrix) || isEmpty(txtPlaces)
                || isEmpty(txtConducteurNom) || isEmpty(txtConducteurTel) || isEmpty(txtMatricule)) {
            setMsg("Veuillez remplir tous les champs obligatoires.", false);
            return;
        }

        try {
            LocalDateTime dd = LocalDateTime.parse(txtDateDepart.getText().trim(), FMT);
            LocalDateTime da = LocalDateTime.parse(txtDateArrivee.getText().trim(), FMT);

            if (!da.isAfter(dd)) {
                setMsg("La date d arrivee doit etre apres le depart.", false);
                return;
            }

            int    places = Integer.parseInt(txtPlaces.getText().trim());
            double prix   = Double.parseDouble(txtPrix.getText().trim());

            if (places <= 0) { setMsg("Le nombre de places doit etre > 0.", false); return; }
            if (prix   <= 0) { setMsg("Le prix doit etre > 0.", false); return; }

            String desc = txtDescription.getText().trim();
            Trajet t = new Trajet(
                    txtDepart.getText().trim(),
                    txtDestination.getText().trim(),
                    dd, da, prix, places, places,
                    txtConducteurNom.getText().trim(),
                    txtConducteurTel.getText().trim(),
                    txtMatricule.getText().trim(),
                    "ACTIF",
                    desc.isEmpty() ? null : desc
            );

            service.ajouter(t);
            setMsg("Trajet cree avec succes ! ID: " + t.getId(), true);
            handleClear();

        } catch (DateTimeParseException e) {
            setMsg("Format de date invalide. Utilisez dd/MM/yyyy HH:mm", false);
        } catch (NumberFormatException e) {
            setMsg("Prix ou places invalides.", false);
        } catch (SQLException e) {
            setMsg("Erreur base de donnees: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleClear() {
        txtDepart.clear(); txtDestination.clear();
        txtDateDepart.clear(); txtDateArrivee.clear();
        txtPrix.clear(); txtPlaces.clear();
        txtConducteurNom.clear(); txtConducteurTel.clear();
        txtMatricule.clear(); txtDescription.clear();
        lblMessage.setText("");
        lblMessage.getStyleClass().removeAll("msg-success", "msg-error");
    }

    private boolean isEmpty(TextField f) {
        return f.getText() == null || f.getText().trim().isEmpty();
    }

    private void setMsg(String msg, boolean success) {
        lblMessage.setText(msg);
        if (success) {
            lblMessage.getStyleClass().add("msg-success");
        } else {
            lblMessage.getStyleClass().add("msg-error");
        }
    }
}