
package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.*;
        import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Reclamation;
import services.ReclamationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;

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

        long total     = liste.size();
        long enAttente = liste.stream().filter(r -> "En attente".equalsIgnoreCase(r.getEtat())).count();
        long enCours   = liste.stream().filter(r -> "En cours".equalsIgnoreCase(r.getEtat())).count();
        long resolues  = liste.stream().filter(r -> "Résolu".equalsIgnoreCase(r.getEtat())).count();

        lblTotal.setText(String.valueOf(total));
        lblEnAttente.setText(String.valueOf(enAttente));
        lblEnCours.setText(String.valueOf(enCours));
        lblResolues.setText(String.valueOf(resolues));

        Map<String, Integer> parType = new HashMap<>();
        for (Reclamation r : liste) {
            String type = r.getType() != null ? r.getType() : "Inconnu";
            parType.put(type, parType.getOrDefault(type, 0) + 1);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Réclamations");
        for (Map.Entry<String, Integer> entry : parType.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChartType.getData().add(series);
        barChartType.setLegendVisible(false);
        barChartType.setAnimated(true);

        for (Map.Entry<String, Integer> entry : parType.entrySet()) {
            pieChartType.getData().add(new PieChart.Data(
                    entry.getKey() + " (" + entry.getValue() + ")",
                    entry.getValue()
            ));
        }
        pieChartType.setAnimated(true);
        pieChartType.setLegendVisible(true);

        // ── Appliquer les couleurs APRÈS rendu complet ───────────────────────
        Platform.runLater(() -> {
            // Axes BarChart
            barChartType.lookupAll(".axis-label").forEach(n ->
                    n.setStyle("-fx-text-fill: #2c3e50;"));
            barChartType.lookupAll(".axis").forEach(n ->
                    n.setStyle("-fx-tick-label-fill: #2c3e50;"));
            barChartType.lookupAll(".chart-plot-background").forEach(n ->
                    n.setStyle("-fx-background-color: rgba(255,255,255,0.4);"));

            // Légende + labels PieChart
            pieChartType.lookupAll(".chart-legend-item").forEach(n ->
                    n.setStyle("-fx-text-fill: #2c3e50;"));
            pieChartType.lookupAll(".chart-pie-label").forEach(n ->
                    n.setStyle("-fx-fill: #2c3e50;"));
        });
    }

    // ✅ Retour vers la liste admin (au lieu de l'ancien ListeReclamation.fxml)
    @FXML
    public void retourListe() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/ListeReclamationAdmin.fxml")
            );
            Stage stage = (Stage) barChartType.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Administration — Liste des Réclamations");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
