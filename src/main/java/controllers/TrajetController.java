package controllers;

import entities.Trajet;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import services.ServiceTrajet;

import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class TrajetController implements Initializable {

    @FXML private TableView<Trajet>            tableView;
    @FXML private TableColumn<Trajet, Integer> colId;
    @FXML private TableColumn<Trajet, String>  colDepart;
    @FXML private TableColumn<Trajet, String>  colDestination;
    @FXML private TableColumn<Trajet, String>  colDate;
    @FXML private TableColumn<Trajet, String>  colPrix;
    @FXML private TableColumn<Trajet, String>  colPlaces;
    @FXML private TableColumn<Trajet, String>  colConducteur;
    @FXML private TableColumn<Trajet, String>  colStatut;
    @FXML private TableColumn<Trajet, Void>    colActions;
    @FXML private TextField                    txtSearch;
    @FXML private ComboBox<String>             cbStatut;
    @FXML private Label                        lblTotal;

    private final ServiceTrajet service = new ServiceTrajet();
    private final ObservableList<Trajet> data = FXCollections.observableArrayList();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumns();
        setupFilters();
        loadData();
    }

    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDepart.setCellValueFactory(new PropertyValueFactory<>("depart"));
        colDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));

        // Diamond operator fix: <>
        colDate.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDateDepart().format(FMT)));
        colPrix.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("%.2f DT", c.getValue().getPrix())));
        colPlaces.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPlacesDisponibles() + "/" + c.getValue().getPlacesTotal()));

        colConducteur.setCellValueFactory(new PropertyValueFactory<>("conducteurNom"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Statut cell : if → switch (Java 14+)
        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setText(null); setStyle(""); return; }
                setText(s);
                String color = switch (s) {
                    case "ACTIF"   -> "#27ae60";
                    case "COMPLET" -> "#e67e22";
                    case "ANNULE"  -> "#e74c3c";
                    case "TERMINE" -> "#95a5a6";
                    default        -> "#3498db";
                };
                setStyle("-fx-text-fill:white;-fx-background-color:" + color
                        + ";-fx-background-radius:4;-fx-padding:2 8;-fx-font-weight:bold;");
            }
        });

        // Actions cell : diamond operator fix <>
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Modifier");
            private final Button btnAnn  = new Button("Annuler");
            private final Button btnDel  = new Button("Supprimer");
            {
                btnEdit.getStyleClass().add("btn-icon-edit");
                btnAnn.getStyleClass().add("btn-icon-warn");
                btnDel.getStyleClass().add("btn-icon-del");
                btnEdit.setOnAction(e -> editTrajet(getTableView().getItems().get(getIndex())));
                btnAnn.setOnAction(e  -> annulerTrajet(getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e  -> deleteTrajet(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : new HBox(4, btnEdit, btnAnn, btnDel));
            }
        });

        tableView.setItems(data);
    }

    private void setupFilters() {
        cbStatut.setItems(FXCollections.observableArrayList(
                "TOUS", "ACTIF", "COMPLET", "ANNULE", "TERMINE"));
        cbStatut.setValue("TOUS");
        cbStatut.setOnAction(e -> filterData());
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
        String statut = cbStatut.getValue();
        try {
            List<Trajet> result = new ArrayList<>();
            for (Trajet t : service.getAll()) {
                boolean sOk = "TOUS".equals(statut) || t.getStatut().equals(statut);
                boolean qOk = search.isEmpty()
                        || t.getDepart().toLowerCase().contains(search)
                        || t.getDestination().toLowerCase().contains(search)
                        || t.getConducteurNom().toLowerCase().contains(search);
                if (sOk && qOk) result.add(t);
            }
            data.setAll(result);
            updateLabel();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() { loadData(); }

    private void editTrajet(Trajet t) {
        TrajetFormDialog dialog = new TrajetFormDialog(t);
        Optional<Trajet> result = dialog.showAndWait();
        result.ifPresent(updated -> {
            try {
                service.modifier(updated);
                loadData();
                new Alert(Alert.AlertType.INFORMATION, "Trajet mis a jour.").showAndWait();
            } catch (SQLException e) { showError(e.getMessage()); }
        });
    }

    private void deleteTrajet(Trajet t) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer le trajet " + t.getDepart() + " -> " + t.getDestination() + " ?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try { service.supprimer(t.getId()); loadData(); }
                catch (SQLException e) { showError(e.getMessage()); }
            }
        });
    }

    private void annulerTrajet(Trajet t) {
        if ("ANNULE".equals(t.getStatut())) return;
        t.setStatut("ANNULE");
        try { service.modifier(t); loadData(); }
        catch (SQLException e) { showError(e.getMessage()); }
    }

    private void updateLabel() {
        if (lblTotal != null) lblTotal.setText(data.size() + " trajet(s)");
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}