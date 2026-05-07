package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Revenue;
import services.RevenueService;
import services.exportService;

import java.net.URL;
import java.util.ResourceBundle;

public class revenueController implements Initializable {
    @FXML private exportService exportService = new exportService();
    @FXML private TextField filterUserIdField;
    @FXML private TextField filterMoisField;
    @FXML private ComboBox<String> filterTypeField;
    @FXML private TextField searchField;
    @FXML private Button deleteBtn;

    @FXML private TableView<Revenue> revenueTable;
    @FXML private TableColumn<Revenue, Integer> idCol;
    @FXML private TableColumn<Revenue, Integer> transactionIdCol;
    @FXML private TableColumn<Revenue, Integer> userIdCol;
    @FXML private TableColumn<Revenue, String> userTypeCol;
    @FXML private TableColumn<Revenue, Double> montantCol;
    @FXML private TableColumn<Revenue, String> dateCol;
    @FXML private TableColumn<Revenue, String> typeRevenueCol;
    @FXML private TableColumn<Revenue, String> moisCol;
    @FXML private TableColumn<Revenue, Integer> nbPassagersCol;
    @FXML private TableColumn<Revenue, String> statutCol;

    private RevenueService revenueService = new RevenueService();
    private ObservableList<Revenue> revenueList = FXCollections.observableArrayList();
    private FilteredList<Revenue> filteredRevenues;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filterTypeField.setItems(FXCollections.observableArrayList("All", "per_trip", "monthly"));
        filterTypeField.setValue("All");

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        transactionIdCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userTypeCol.setCellValueFactory(new PropertyValueFactory<>("userType"));
        montantCol.setCellValueFactory(new PropertyValueFactory<>("montant"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateRevenue"));
        typeRevenueCol.setCellValueFactory(new PropertyValueFactory<>("typeRevenue"));
        moisCol.setCellValueFactory(new PropertyValueFactory<>("mois"));
        nbPassagersCol.setCellValueFactory(new PropertyValueFactory<>("nbPassagers"));
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));

        loadTable();
    }

    private void loadTable() {
        revenueList.clear();
        revenueList.addAll(revenueService.getAll());

        filteredRevenues = new FilteredList<>(revenueList, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredRevenues.setPredicate(r -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return String.valueOf(r.getUserId()).contains(newVal.trim());
            });
        });

        SortedList<Revenue> sortedRevenues = new SortedList<>(filteredRevenues);
        sortedRevenues.comparatorProperty().bind(revenueTable.comparatorProperty());
        revenueTable.setItems(sortedRevenues);
    }

    @FXML
    private void applyFilter() {
        filteredRevenues.setPredicate(r -> {
            String userId = filterUserIdField.getText().trim();
            String mois = filterMoisField.getText().trim();
            String type = filterTypeField.getValue();

            boolean matchUserId = userId.isEmpty() || String.valueOf(r.getUserId()).equals(userId);
            boolean matchMois = mois.isEmpty() || mois.equals(r.getMois());
            boolean matchType = type.equals("All") || type.equals(r.getTypeRevenue());

            return matchUserId && matchMois && matchType;
        });
    }

    @FXML
    private void resetFilter() {
        filterUserIdField.clear();
        filterMoisField.clear();
        filterTypeField.setValue("All");
        searchField.clear();
        loadTable();
    }

    @FXML
    private void deleteRevenue() {
        Revenue selected = revenueTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a revenue to delete.", Alert.AlertType.ERROR);
            return;
        }
        revenueService.delete(selected);
        loadTable();
        showAlert("Success", "Revenue deleted successfully.", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void exportPDF() {
        String path = System.getProperty("user.home") + "/Desktop/revenues_report.pdf";
        exportService.exportRevenuesToPDF(revenueService.getAll(), path);
        showAlert("Success", "PDF exported to your Desktop!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void exportExcel() {
        String path = System.getProperty("user.home") + "/Desktop/revenues_report.xlsx";
        exportService.exportRevenuesToExcel(revenueService.getAll(), path);
        showAlert("Success", "Excel exported to your Desktop!", Alert.AlertType.INFORMATION);
    }
}