package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import services.ServiceStatistiques;

import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;

public class StatistiquesController implements Initializable {

    @FXML private Label    lblTotalTrajets;
    @FXML private Label    lblTotalReservations;
    @FXML private Label    lblRevenue;
    @FXML private Label    lblTauxOccupation;
    @FXML private Label    lblPlacesVendues;

    @FXML private PieChart                    pieStatut;
    @FXML private BarChart<String, Number>    barTrajets;
    @FXML private LineChart<String, Number>   lineRevenue;
    @FXML private PieChart                    pieReservations;

    private final ServiceStatistiques stats = new ServiceStatistiques();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadStats();
    }

    @FXML
    public void loadStats() {
        try {
            // KPIs
            lblTotalTrajets.setText(String.valueOf(stats.totalTrajets()));
            lblTotalReservations.setText(String.valueOf(stats.totalReservations()));
            lblRevenue.setText(String.format("%.2f DT", stats.revenueTotal()));
            lblTauxOccupation.setText(String.format("%.1f %%", stats.tauxOccupationMoyen()));
            lblPlacesVendues.setText(String.valueOf(stats.placesVendues()));

            // Pie: trajets par statut
            pieStatut.getData().clear();
            for (Map.Entry<String, Integer> e : stats.trajetsByStatut().entrySet()) {
                pieStatut.getData().add(
                        new PieChart.Data(e.getKey() + " (" + e.getValue() + ")", e.getValue()));
            }

            // Bar: top trajets
            barTrajets.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Reservations");
            for (Map.Entry<String, Integer> e : stats.topTrajets().entrySet()) {
                series.getData().add(new XYChart.Data<>(shorten(e.getKey()), e.getValue()));
            }
            barTrajets.getData().add(series);

            // Line: revenue par mois
            lineRevenue.getData().clear();
            XYChart.Series<String, Number> rev = new XYChart.Series<>();
            rev.setName("Chiffre d affaires (DT)");
            for (Map.Entry<String, Double> e : stats.revenueParMois().entrySet()) {
                rev.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
            }
            lineRevenue.getData().add(rev);

            // Pie: reservations par statut
            pieReservations.getData().clear();
            for (Map.Entry<String, Integer> e : stats.reservationsByStatut().entrySet()) {
                pieReservations.getData().add(
                        new PieChart.Data(e.getKey() + " (" + e.getValue() + ")", e.getValue()));
            }

        } catch (SQLException e) {
            System.err.println("Erreur stats: " + e.getMessage());
        }
    }

    private String shorten(String route) {
        if (route == null) return "";
        return route.length() > 18 ? route.substring(0, 16) + "..." : route;
    }
}