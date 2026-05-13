package controllers;

import models.Reservation;
import models.Trajet;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import services.ServiceReservation;
import services.ServiceTrajet;

import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReservationController implements Initializable {

    @FXML private TableView<Reservation>            tableView;
    @FXML private TableColumn<Reservation, Integer> colId;
    @FXML private TableColumn<Reservation, String>  colTrajet;
    @FXML private TableColumn<Reservation, String>  colPassager;
    @FXML private TableColumn<Reservation, String>  colEmail;
    @FXML private TableColumn<Reservation, String>  colTel;
    @FXML private TableColumn<Reservation, Integer> colPlaces;
    @FXML private TableColumn<Reservation, String>  colDate;
    @FXML private TableColumn<Reservation, String>  colStatut;
    @FXML private TableColumn<Reservation, String>  colMontant;
    @FXML private TableColumn<Reservation, Void>    colActions;
    @FXML private TextField                         txtSearch;
    @FXML private ComboBox<String>                  cbStatutFilter;
    @FXML private Label                             lblTotal;

    private final ServiceReservation      service       = new ServiceReservation();
    private final ServiceTrajet           serviceTrajet = new ServiceTrajet();
    private final ObservableList<Reservation> data      = FXCollections.observableArrayList();
    private static final DateTimeFormatter FMT          = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumns();
        setupFilters();
        loadData();
    }

    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTrajet.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().getTrajetInfo()));
        colPassager.setCellValueFactory(new PropertyValueFactory<>("passagerNom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("passagerEmail"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("passagerTel"));
        colPlaces.setCellValueFactory(new PropertyValueFactory<>("nombrePlaces"));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDateReservation().format(FMT)));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colMontant.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("%.2f DT", c.getValue().getMontantTotal())));

        colStatut.setCellFactory(col -> new TableCell<Reservation, String>() {
            @Override
            protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setText(null); setStyle(""); return; }
                setText(s);
                String color;
                if ("CONFIRMEE".equals(s))       color = "#27ae60";
                else if ("EN_ATTENTE".equals(s)) color = "#f39c12";
                else if ("ANNULEE".equals(s))    color = "#e74c3c";
                else                             color = "#3498db";
                setStyle("-fx-text-fill:white;-fx-background-color:" + color +
                        ";-fx-background-radius:4;-fx-padding:2 8;-fx-font-weight:bold;");
            }
        });

        colActions.setCellFactory(col -> new TableCell<Reservation, Void>() {
            private final Button btnOk  = new Button("Confirmer");
            private final Button btnAnn = new Button("Annuler");
            private final Button btnDel = new Button("Supprimer");
            {
                btnOk.getStyleClass().add("btn-icon-edit");
                btnAnn.getStyleClass().add("btn-icon-warn");
                btnDel.getStyleClass().add("btn-icon-del");
                btnOk.setOnAction(e  -> changeStatut(getTableView().getItems().get(getIndex()), "CONFIRMEE"));
                btnAnn.setOnAction(e -> changeStatut(getTableView().getItems().get(getIndex()), "ANNULEE"));
                btnDel.setOnAction(e -> deleteReservation(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : new HBox(6, btnOk, btnAnn, btnDel));
            }
        });

        tableView.setItems(data);
    }

    private void setupFilters() {
        cbStatutFilter.setItems(FXCollections.observableArrayList("TOUS","EN_ATTENTE","CONFIRMEE","ANNULEE"));
        cbStatutFilter.setValue("TOUS");
        cbStatutFilter.setOnAction(e -> filterData());
        txtSearch.textProperty().addListener((obs, o, n) -> filterData());
    }

    private void loadData() {
        try {
            data.setAll(service.getAll());
            updateLabel();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void filterData() {
        String search = txtSearch.getText().toLowerCase().trim();
        String statut = cbStatutFilter.getValue();
        try {
            List<Reservation> result = new ArrayList<>();
            for (Reservation r : service.getAll()) {
                boolean sOk = "TOUS".equals(statut) || r.getStatut().equals(statut);
                boolean qOk = search.isEmpty()
                        || r.getPassagerNom().toLowerCase().contains(search)
                        || r.getPassagerEmail().toLowerCase().contains(search)
                        || (r.getTrajetInfo() != null && r.getTrajetInfo().toLowerCase().contains(search));
                if (sOk && qOk) result.add(r);
            }
            data.setAll(result);
            updateLabel();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    @FXML private void handleNouvelle() { showReservationForm(); }
    @FXML private void handleRefresh()  { loadData(); }

    private void showReservationForm() {
        Dialog<Reservation> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Reservation");
        dialog.setHeaderText("Informations du passager");

        ButtonType saveBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        ComboBox<Trajet> cbTrajet = new ComboBox<>();
        try {
            cbTrajet.setItems(FXCollections.observableArrayList(serviceTrajet.getAll()));
        } catch (SQLException e) {
            showError(e.getMessage());
        }
        cbTrajet.setConverter(new StringConverter<Trajet>() {
            public String toString(Trajet t) {
                return t == null ? "" : t.getDepart() + " -> " + t.getDestination()
                        + "  (" + t.getPlacesDisponibles() + " places, "
                        + String.format("%.0f", t.getPrix()) + " DT)";
            }
            public Trajet fromString(String s) { return null; }
        });

        TextField     fNom     = new TextField();
        TextField     fEmail   = new TextField();
        TextField     fTel     = new TextField();
        Spinner<Integer> spPlaces = new Spinner<>(1, 10, 1);
        TextArea      fComment = new TextArea();
        fComment.setPrefRowCount(2);
        ComboBox<String> cbSt  = new ComboBox<>();
        cbSt.setItems(FXCollections.observableArrayList("EN_ATTENTE","CONFIRMEE","ANNULEE"));
        cbSt.setValue("EN_ATTENTE");

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 20));
        grid.add(new Label("Trajet :"),      0, 0); grid.add(cbTrajet,  1, 0);
        grid.add(new Label("Nom :"),         0, 1); grid.add(fNom,      1, 1);
        grid.add(new Label("Email :"),       0, 2); grid.add(fEmail,    1, 2);
        grid.add(new Label("Telephone :"),   0, 3); grid.add(fTel,      1, 3);
        grid.add(new Label("Places :"),      0, 4); grid.add(spPlaces,  1, 4);
        grid.add(new Label("Statut :"),      0, 5); grid.add(cbSt,      1, 5);
        grid.add(new Label("Commentaire :"), 0, 6); grid.add(fComment,  1, 6);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn != saveBtn) return null;
            Trajet t = cbTrajet.getValue();
            if (t == null || fNom.getText().trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Champs obligatoires manquants.").showAndWait();
                return null;
            }
            int places    = spPlaces.getValue();
            double montant = t.getPrix() * places;
            Reservation r  = new Reservation();
            r.setTrajetId(t.getId());
            r.setPassagerNom(fNom.getText().trim());
            r.setPassagerEmail(fEmail.getText().trim());
            r.setPassagerTel(fTel.getText().trim());
            r.setNombrePlaces(places);
            r.setStatut(cbSt.getValue());
            r.setCommentaire(fComment.getText().trim().isEmpty() ? null : fComment.getText().trim());
            r.setMontantTotal(montant);
            return r;
        });

        Optional<Reservation> result = dialog.showAndWait();
        result.ifPresent(res -> {
            try {
                service.ajouter(res);
                loadData();
                new Alert(Alert.AlertType.INFORMATION, "Reservation creee.").showAndWait();
            } catch (SQLException ex) {
                showError(ex.getMessage());
            }
        });
    }

    private void changeStatut(Reservation r, String statut) {
        try { service.changerStatut(r.getId(), statut); loadData(); }
        catch (SQLException e) { showError(e.getMessage()); }
    }

    private void deleteReservation(Reservation r) {
        Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer la reservation de " + r.getPassagerNom() + " ?",
                ButtonType.YES, ButtonType.NO);
        c.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try { service.supprimer(r.getId()); loadData(); }
                catch (SQLException e) { showError(e.getMessage()); }
            }
        });
    }

    private void updateLabel() {
        if (lblTotal != null) lblTotal.setText(data.size() + " reservation(s)");
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}