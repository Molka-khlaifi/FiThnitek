package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Revenue;
import services.DashboardService;
import services.RevenueService;
import services.exportService;
import util.NavigationManager;

import java.net.URL;
import java.util.Map;
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

    /**
     * Navigates to Transaction.fxml inside the same tab container.
     * Uses the REVENU tab name registered by ConducteurHomePageController.
     * (Temporary — will be replaced by a trigger from the Trajets page later.)
     */
    @FXML
    private void goToTransactions() {
        NavigationManager.navigateInTab("REVENU", "/Transaction.fxml");
    }

    // ── Statistics Popup ─────────────────────────────────────────────────────
    @FXML
    private void showStatistics() {
        DashboardService ds = new DashboardService();

        // ── Root layout ──────────────────────────────────────────────────────
        VBox root = new VBox(18);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #f0f2f5;");
        root.setPrefWidth(870);

        // ── Title bar ────────────────────────────────────────────────────────
        Text title = new Text("📈  Revenue Statistics");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setStyle("-fx-fill: #2c3e50;");
        root.getChildren().add(title);

        // ── KPI Cards ────────────────────────────────────────────────────────
        HBox kpiRow = new HBox(14);
        kpiRow.getChildren().addAll(
            makeKpiCard("💰  Total Revenue",
                String.format("%.2f DT", ds.getTotalRevenue()), "#27ae60", "rgba(39,174,96,0.35)"),
            makeKpiCard("🔄  Total Refunded",
                String.format("%.2f DT", ds.getTotalRefunded()), "#e74c3c", "rgba(231,76,60,0.35)"),
            makeKpiCard("🚗  Active Drivers",
                String.valueOf(ds.getDistinctDriverCount()), "#f39c12", "rgba(243,156,18,0.35)"),
            makeKpiCard("💳  Transactions",
                String.valueOf(ds.getTotalTransactions()), "#3498db", "rgba(52,152,219,0.35)")
        );
        root.getChildren().add(kpiRow);

        // ── Row 1 – Revenue by Month (Bar) + Revenue Status (Pie) ────────────
        HBox row1 = new HBox(14);

        // Bar chart – Revenue by Month
        VBox barBox = makeChartCard("Revenue by Month (DT)");
        CategoryAxis xBar = new CategoryAxis();
        NumberAxis yBar = new NumberAxis();
        xBar.setLabel("Month"); yBar.setLabel("DT");
        BarChart<String, Number> barChart = new BarChart<>(xBar, yBar);
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
        barChart.setPrefHeight(260);
        barChart.setStyle("-fx-background-color: transparent;");
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        Map<String, Double> revenueByMonth = ds.getRevenueByMonth();
        if (revenueByMonth.isEmpty()) {
            barSeries.getData().add(new XYChart.Data<>("No Data", 0));
        } else {
            revenueByMonth.forEach((m, v) -> barSeries.getData().add(new XYChart.Data<>(m, v)));
        }
        barChart.getData().add(barSeries);
        barBox.getChildren().add(barChart);

        // Pie chart – Revenue / Transaction status
        VBox pieBox = makeChartCard("Transaction Status");
        PieChart pieChart = new PieChart();
        pieChart.setLabelsVisible(true);
        pieChart.setAnimated(false);
        pieChart.setPrefHeight(260);
        pieChart.setStyle("-fx-background-color: transparent;");
        Map<String, Integer> statusCounts = ds.getTransactionStatusCounts();
        if (statusCounts.isEmpty()) {
            pieChart.getData().add(new PieChart.Data("No Data", 1));
        } else {
            statusCounts.forEach((s, c) -> pieChart.getData().add(
                new PieChart.Data(capitalize(s) + " (" + c + ")", c)));
        }
        pieBox.getChildren().add(pieChart);

        row1.getChildren().addAll(barBox, pieBox);
        HBox.setHgrow(barBox, Priority.ALWAYS);
        HBox.setHgrow(pieBox, Priority.ALWAYS);
        root.getChildren().add(row1);

        // ── Row 2 – Passengers by Month (Line) + Payment Methods (Pie) ───────
        HBox row2 = new HBox(14);

        // Line chart – Passengers by Month
        VBox lineBox = makeChartCard("Passengers by Month");
        CategoryAxis xLine = new CategoryAxis();
        NumberAxis yLine = new NumberAxis();
        xLine.setLabel("Month"); yLine.setLabel("Passengers");
        LineChart<String, Number> lineChart = new LineChart<>(xLine, yLine);
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);
        lineChart.setPrefHeight(240);
        lineChart.setStyle("-fx-background-color: transparent;");
        XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();
        Map<String, Integer> passByMonth = ds.getPassengersByMonth();
        if (passByMonth.isEmpty()) {
            lineSeries.getData().add(new XYChart.Data<>("No Data", 0));
        } else {
            passByMonth.forEach((m, v) -> lineSeries.getData().add(new XYChart.Data<>(m, v)));
        }
        lineChart.getData().add(lineSeries);
        lineBox.getChildren().add(lineChart);

        // Pie chart – Payment Methods
        VBox methodPieBox = makeChartCard("Payment Methods");
        PieChart methodPie = new PieChart();
        methodPie.setLabelsVisible(true);
        methodPie.setAnimated(false);
        methodPie.setPrefHeight(240);
        methodPie.setStyle("-fx-background-color: transparent;");
        Map<String, Integer> methodCounts = ds.getPaymentMethodCounts();
        if (methodCounts.isEmpty()) {
            methodPie.getData().add(new PieChart.Data("No Data", 1));
        } else {
            methodCounts.forEach((m, c) -> methodPie.getData().add(
                new PieChart.Data(capitalize(m) + " (" + c + ")", c)));
        }
        methodPieBox.getChildren().add(methodPie);

        row2.getChildren().addAll(lineBox, methodPieBox);
        HBox.setHgrow(lineBox, Priority.ALWAYS);
        HBox.setHgrow(methodPieBox, Priority.ALWAYS);
        root.getChildren().add(row2);

        // ── Close button ─────────────────────────────────────────────────────
        Button closeBtn = new Button("✕  Close");
        closeBtn.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; "
                + "-fx-padding: 7 28;");

        HBox footer = new HBox(closeBtn);
        footer.setAlignment(Pos.CENTER_RIGHT);
        root.getChildren().add(footer);

        // ── Stage ────────────────────────────────────────────────────────────
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Revenue Statistics");
        closeBtn.setOnAction(e -> popup.close());

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #f0f2f5;");

        popup.setScene(new Scene(scroll, 900, 720));
        popup.setResizable(true);
        popup.showAndWait();
    }

    // ── UI helper: coloured KPI card ──────────────────────────────────────────
    private VBox makeKpiCard(String label, String value, String bg, String shadow) {
        VBox card = new VBox(6);
        card.setPrefWidth(195);
        card.setPrefHeight(90);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 14; "
                + "-fx-effect: dropshadow(gaussian," + shadow + ",12,0,0,4);");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: rgba(255,255,255,0.88); -fx-font-size: 12px;");

        Label val = new Label(value);
        val.setStyle("-fx-text-fill: white; -fx-font-size: 26px; -fx-font-weight: bold;");

        card.getChildren().addAll(lbl, val);
        return card;
    }

    // ── UI helper: white rounded chart container ──────────────────────────────
    private VBox makeChartCard(String heading) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(16));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 14; "
                + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.08),10,0,0,3);");
        Label hdr = new Label(heading);
        hdr.setFont(Font.font("System", FontWeight.BOLD, 14));
        hdr.setStyle("-fx-text-fill: #2c3e50;");
        box.getChildren().add(hdr);
        return box;
    }

    // ── Utility ───────────────────────────────────────────────────────────────
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}