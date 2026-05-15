package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import models.Trajet;
import services.ServiceTrajet;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AjouterTrajetController {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private TextField txtDepart;
    @FXML private TextField txtDestination;
    @FXML private TextField txtDateDepart;
    @FXML private TextField txtDateArrivee;
    @FXML private TextField txtPrix;
    @FXML private TextField txtPlaces;
    @FXML private TextField txtConducteurNom;
    @FXML private TextField txtConducteurTel;
    @FXML private TextField txtMatricule;
    @FXML private TextArea txtDescription;
    @FXML private Label lblMessage;

    private final ServiceTrajet serviceTrajet = new ServiceTrajet();

    @FXML
    private void handleAjouter() {
        try {
            Trajet trajet = buildTrajetFromForm();
            serviceTrajet.ajouter(trajet);
            clearForm();
            showSuccess("Trajet créé avec succès.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (SQLException e) {
            showError("Erreur base de données : " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
        if (lblMessage != null) {
            lblMessage.setText("");
            lblMessage.getStyleClass().removeAll("msg-success", "msg-error", "msg-warn");
        }
    }

    private Trajet buildTrajetFromForm() {
        String depart = required(txtDepart, "Ville de départ");
        String destination = required(txtDestination, "Destination");
        LocalDateTime dateDepart = parseDateTime(required(txtDateDepart, "Date de départ"), true);
        LocalDateTime dateArrivee = parseDateTime(required(txtDateArrivee, "Date d'arrivée"), false);

        if (dateArrivee.isBefore(dateDepart)) {
            throw new IllegalArgumentException("La date d'arrivée doit être après la date de départ.");
        }

        double prix = parseDouble(required(txtPrix, "Prix"));
        int places = parseInt(required(txtPlaces, "Nombre de places"));
        if (places <= 0) {
            throw new IllegalArgumentException("Le nombre de places doit être supérieur à 0.");
        }

        Trajet trajet = new Trajet();
        trajet.setDepart(depart);
        trajet.setDestination(destination);
        trajet.setDateDepart(dateDepart);
        trajet.setDateArrivee(dateArrivee);
        trajet.setPrix(prix);
        trajet.setPlacesTotal(places);
        trajet.setPlacesDisponibles(places);
        trajet.setConducteurNom(required(txtConducteurNom, "Nom conducteur"));
        trajet.setConducteurTel(required(txtConducteurTel, "Téléphone conducteur"));
        trajet.setMatriculeVehicule(required(txtMatricule, "Matricule"));
        trajet.setDescription(trim(txtDescription));
        trajet.setStatut("ACTIF");
        return trajet;
    }

    private String required(TextField field, String label) {
        String value = trim(field);
        if (value.isEmpty()) {
            throw new IllegalArgumentException(label + " est obligatoire.");
        }
        return value;
    }

    private String trim(TextField field) {
        return field == null || field.getText() == null ? "" : field.getText().trim();
    }

    private String trim(TextArea area) {
        return area == null || area.getText() == null ? "" : area.getText().trim();
    }

    private LocalDateTime parseDateTime(String value, boolean startOfDay) {
        try {
            return LocalDateTime.parse(value, DATE_TIME_FORMAT);
        } catch (DateTimeParseException ignored) {
            try {
                LocalDate date = LocalDate.parse(value, DATE_FORMAT);
                return startOfDay ? date.atStartOfDay() : date.atTime(23, 59);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Format de date invalide. Utilisez dd/MM/yyyy ou dd/MM/yyyy HH:mm.");
            }
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Prix invalide.");
        }
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Nombre de places invalide.");
        }
    }

    private void clearForm() {
        txtDepart.clear();
        txtDestination.clear();
        txtDateDepart.clear();
        txtDateArrivee.clear();
        txtPrix.clear();
        txtPlaces.clear();
        txtConducteurNom.clear();
        txtConducteurTel.clear();
        txtMatricule.clear();
        txtDescription.clear();
    }

    private void showSuccess(String message) {
        showMessage(message, "msg-success");
    }

    private void showError(String message) {
        showMessage(message, "msg-error");
    }

    private void showMessage(String message, String styleClass) {
        if (lblMessage == null) {
            return;
        }
        lblMessage.setText(message);
        lblMessage.getStyleClass().removeAll("msg-success", "msg-error", "msg-warn");
        lblMessage.getStyleClass().add(styleClass);
    }
}
