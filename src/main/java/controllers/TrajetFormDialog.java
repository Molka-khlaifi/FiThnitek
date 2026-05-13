package controllers;

import entities.Trajet;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TrajetFormDialog extends Dialog<Trajet> {

    // Accepte les deux formats
    private static final DateTimeFormatter FMT_FULL  = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_DATE  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final TextField        fDepart        = new TextField();
    private final TextField        fDestination   = new TextField();
    private final TextField        fDateDepart    = new TextField();
    private final TextField        fDateArrivee   = new TextField();
    private final TextField        fPrix          = new TextField();
    private final TextField        fPlacesTotal   = new TextField();
    private final TextField        fConducteurNom = new TextField();
    private final TextField        fConducteurTel = new TextField();
    private final TextField        fMatricule     = new TextField();
    private final TextArea         fDescription   = new TextArea();
    private final ComboBox<String> cbStatut       = new ComboBox<>();

    public TrajetFormDialog(Trajet trajet) {
        boolean isEdit = trajet != null;
        setTitle(isEdit ? "Modifier le trajet" : "Nouveau trajet");
        setHeaderText(isEdit ? "Modifier les informations" : "Renseigner les informations");

        ButtonType saveBtn = new ButtonType(
                isEdit ? "Mettre a jour" : "Creer", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        cbStatut.getItems().addAll("ACTIF", "COMPLET", "ANNULE", "TERMINE");
        cbStatut.setValue("ACTIF");
        fDescription.setPrefRowCount(2);
        fDescription.setWrapText(true);

        // Prompt text mis a jour : les deux formats acceptes
        fDateDepart.setPromptText("dd/MM/yyyy  ou  dd/MM/yyyy HH:mm");
        fDateArrivee.setPromptText("dd/MM/yyyy  ou  dd/MM/yyyy HH:mm");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 20));

        grid.add(new Label("Depart :"),        0,  0); grid.add(fDepart,        1,  0);
        grid.add(new Label("Destination :"),   0,  1); grid.add(fDestination,   1,  1);
        grid.add(new Label("Date depart :"),   0,  2); grid.add(fDateDepart,    1,  2);
        grid.add(new Label("Date arrivee :"),  0,  3); grid.add(fDateArrivee,   1,  3);
        grid.add(new Label("Prix (DT) :"),     0,  4); grid.add(fPrix,          1,  4);
        grid.add(new Label("Places total :"),  0,  5); grid.add(fPlacesTotal,   1,  5);
        grid.add(new Label("Conducteur :"),    0,  6); grid.add(fConducteurNom, 1,  6);
        grid.add(new Label("Telephone :"),     0,  7); grid.add(fConducteurTel, 1,  7);
        grid.add(new Label("Matricule :"),     0,  8); grid.add(fMatricule,     1,  8);
        grid.add(new Label("Statut :"),        0,  9); grid.add(cbStatut,       1,  9);
        grid.add(new Label("Description :"),   0, 10); grid.add(fDescription,   1, 10);

        if (isEdit) {
            fDepart.setText(trajet.getDepart());
            fDestination.setText(trajet.getDestination());
            fDateDepart.setText(trajet.getDateDepart().format(FMT_FULL));
            fDateArrivee.setText(trajet.getDateArrivee().format(FMT_FULL));
            fPrix.setText(String.valueOf(trajet.getPrix()));
            fPlacesTotal.setText(String.valueOf(trajet.getPlacesTotal()));
            fConducteurNom.setText(trajet.getConducteurNom());
            fConducteurTel.setText(trajet.getConducteurTel());
            fMatricule.setText(trajet.getMatriculeVehicule());
            cbStatut.setValue(trajet.getStatut());
            if (trajet.getDescription() != null) fDescription.setText(trajet.getDescription());
        }

        getDialogPane().setContent(grid);

        setResultConverter(btn -> {
            if (btn != saveBtn) return null;
            try {
                LocalDateTime dd = parseDateTime(fDateDepart.getText().trim(), true);
                LocalDateTime da = parseDateTime(fDateArrivee.getText().trim(), false);

                if (da.isBefore(dd)) {
                    new Alert(Alert.AlertType.ERROR,
                            "La date d'arrivee doit etre apres la date de depart.").showAndWait();
                    return null;
                }

                int    places = Integer.parseInt(fPlacesTotal.getText().trim());
                double prix   = Double.parseDouble(fPrix.getText().replace(",", ".").trim());

                Trajet t = isEdit ? trajet : new Trajet();
                t.setDepart(fDepart.getText().trim());
                t.setDestination(fDestination.getText().trim());
                t.setDateDepart(dd);
                t.setDateArrivee(da);
                t.setPrix(prix);
                t.setPlacesTotal(places);
                if (!isEdit) t.setPlacesDisponibles(places);
                t.setConducteurNom(fConducteurNom.getText().trim());
                t.setConducteurTel(fConducteurTel.getText().trim());
                t.setMatriculeVehicule(fMatricule.getText().trim());
                t.setStatut(cbStatut.getValue());
                String desc = fDescription.getText().trim();
                t.setDescription(desc.isEmpty() ? null : desc);
                return t;

            } catch (DateTimeParseException ex) {
                new Alert(Alert.AlertType.ERROR,
                        "Format de date invalide.\nUtilisez dd/MM/yyyy ou dd/MM/yyyy HH:mm\n"
                                + "Exemple : 10/10/2026 ou 10/10/2026 08:00").showAndWait();
                return null;
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR,
                        "Prix ou nombre de places invalide : " + ex.getMessage()).showAndWait();
                return null;
            }
        });
    }

    /**
     * Parse une date en acceptant deux formats :
     *   - "dd/MM/yyyy HH:mm"  (complet)
     *   - "dd/MM/yyyy"        (date seule → heure par defaut 00:00 ou 23:59)
     */
    private LocalDateTime parseDateTime(String text, boolean isDepart) {
        // Essai format complet
        try {
            return LocalDateTime.parse(text, FMT_FULL);
        } catch (DateTimeParseException ignored) {}

        // Essai format date seule
        LocalDate date = LocalDate.parse(text, FMT_DATE);
        // Depart → debut de journee, Arrivee → fin de journee
        return isDepart ? date.atStartOfDay() : date.atTime(23, 59);
    }
}