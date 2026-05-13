
package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Reclamation;
import services.ReclamationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatistiquesController {

    @FXML private Label   lblTotal;
    @FXML private Label   lblEnAttente;
    @FXML private Label   lblEnCours;
    @FXML private Label   lblResolues;
    @FXML private BarChart<String, Number>  barChartType;
    @FXML private PieChart                  pieChartType;

    ReclamationService service = new ReclamationService();

    @FXML
    public void initialize() {
        List<Reclamation> liste = service.getAll();

        // ── Cartes résumé ──────────────────────────────────────────
        long total      = liste.size();
        long enAttente  = liste.stream().filter(r -> "En attente".equalsIgnoreCase(r.getEtat())).count();
        long enCours    = liste.stream().filter(r -> "En cours".equalsIgnoreCase(r.getEtat())).count();
        long resolues   = liste.stream().filter(r -> "Résolu".equalsIgnoreCase(r.getEtat())).count();

        lblTotal.setText(String.valueOf(total));
        lblEnAttente.setText(String.valueOf(enAttente));
        lblEnCours.setText(String.valueOf(enCours));
        lblResolues.setText(String.valueOf(resolues));

        // ── Comptage par type ──────────────────────────────────────
        Map<String, Integer> parType = new HashMap<>();
        for (Reclamation r : liste) {
            String type = r.getType() != null ? r.getType() : "Inconnu";
            parType.put(type, parType.getOrDefault(type, 0) + 1);
        }

        // ── BarChart ───────────────────────────────────────────────
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Réclamations");

        for (Map.Entry<String, Integer> entry : parType.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChartType.getData().add(series);
        barChartType.setLegendVisible(false);
        barChartType.setAnimated(true);

        // ── PieChart ───────────────────────────────────────────────
        for (Map.Entry<String, Integer> entry : parType.entrySet()) {
            PieChart.Data slice = new PieChart.Data(
                    entry.getKey() + " (" + entry.getValue() + ")",
                    entry.getValue()
            );
            pieChartType.getData().add(slice);
        }

        pieChartType.setAnimated(true);
        pieChartType.setLegendVisible(true);
    }

    @FXML
    public void retourListe() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/ListeReclamation.fxml")
            );
            Stage stage = (Stage) barChartType.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Liste des Réclamations");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
