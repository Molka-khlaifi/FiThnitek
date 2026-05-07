package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import services.DashboardService;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class dashboardController implements Initializable {

    // ── KPI Labels ─────────────────────────────────────────────────────────
    @FXML private Label totalTransactionsLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label totalRefundedLabel;
    @FXML private Label driversLabel;

    // ── Charts ──────────────────────────────────────────────────────────────
    @FXML private PieChart statusPieChart;
    @FXML private PieChart methodPieChart;
    @FXML private BarChart<String, Number> revenueBarChart;
    @FXML private LineChart<String, Number> passengersLineChart;

    private final DashboardService dashboardService = new DashboardService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDashboard();
    }

    @FXML
    private void refreshDashboard() {
        loadDashboard();
    }

    // ── Master load method ──────────────────────────────────────────────────
    private void loadDashboard() {
        loadKPIs();
        loadStatusPieChart();
        loadMethodPieChart();
        loadRevenueBarChart();
        loadPassengersLineChart();
    }

    // ── KPI Cards ───────────────────────────────────────────────────────────
    private void loadKPIs() {
        totalTransactionsLabel.setText(String.valueOf(dashboardService.getTotalTransactions()));
        totalRevenueLabel.setText(String.format("%.2f DT", dashboardService.getTotalRevenue()));
        totalRefundedLabel.setText(String.format("%.2f DT", dashboardService.getTotalRefunded()));
        driversLabel.setText(String.valueOf(dashboardService.getDistinctDriverCount()));
    }

    // ── Transaction Status Pie ───────────────────────────────────────────────
    private void loadStatusPieChart() {
        statusPieChart.getData().clear();
        Map<String, Integer> counts = dashboardService.getTransactionStatusCounts();
        if (counts.isEmpty()) {
            statusPieChart.getData().add(new PieChart.Data("No Data", 1));
            return;
        }
        counts.forEach((status, count) ->
                statusPieChart.getData().add(
                        new PieChart.Data(capitalize(status) + " (" + count + ")", count)));
    }

    // ── Payment Method Pie ───────────────────────────────────────────────────
    private void loadMethodPieChart() {
        methodPieChart.getData().clear();
        Map<String, Integer> counts = dashboardService.getPaymentMethodCounts();
        if (counts.isEmpty()) {
            methodPieChart.getData().add(new PieChart.Data("No Data", 1));
            return;
        }
        counts.forEach((method, count) ->
                methodPieChart.getData().add(
                        new PieChart.Data(capitalize(method) + " (" + count + ")", count)));
    }

    // ── Revenue by Month Bar Chart ───────────────────────────────────────────
    private void loadRevenueBarChart() {
        revenueBarChart.getData().clear();
        Map<String, Double> data = dashboardService.getRevenueByMonth();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");
        if (data.isEmpty()) {
            series.getData().add(new XYChart.Data<>("No Data", 0));
        } else {
            data.forEach((month, amount) ->
                    series.getData().add(new XYChart.Data<>(month, amount)));
        }
        revenueBarChart.getData().add(series);
    }

    // ── Passengers by Month Line Chart ───────────────────────────────────────
    private void loadPassengersLineChart() {
        passengersLineChart.getData().clear();
        Map<String, Integer> data = dashboardService.getPassengersByMonth();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Passengers");
        if (data.isEmpty()) {
            series.getData().add(new XYChart.Data<>("No Data", 0));
        } else {
            data.forEach((month, count) ->
                    series.getData().add(new XYChart.Data<>(month, count)));
        }
        passengersLineChart.getData().add(series);
    }

    // ── Utility ──────────────────────────────────────────────────────────────
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
