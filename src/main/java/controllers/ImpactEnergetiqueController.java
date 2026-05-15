package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import services.NavigationManager;
import models.Vehicule;
import services.VehiculeService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ImpactEnergetiqueController {

    @FXML
    private PieChart energiePieChart;

    @FXML
    private VBox statsBox;

    @FXML
    private Label totalLabel;

    @FXML
    private Label messageLabel;

    private final VehiculeService vehiculeService = new VehiculeService();

    @FXML
    public void initialize() {
        chargerStatistiquesEnergie();
    }

    private void chargerStatistiquesEnergie() {
        List<Vehicule> vehicules = vehiculeService.getAll();

        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("ESSENCE", 0);
        stats.put("DIESEL", 0);
        stats.put("HYBRIDE", 0);
        stats.put("ELECTRIQUE", 0);
        stats.put("GPL", 0);
        stats.put("AUTRE", 0);

        for (Vehicule vehicule : vehicules) {
            String energie = vehicule.getEnergie();

            if (energie == null || energie.trim().isEmpty()) {
                energie = "AUTRE";
            }

            if (!stats.containsKey(energie)) {
                energie = "AUTRE";
            }

            stats.put(energie, stats.get(energie) + 1);
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            if (entry.getValue() > 0) {
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
        }

        energiePieChart.setData(pieChartData);

        totalLabel.setText("Total : " + vehicules.size() + " véhicule(s)");

        afficherResume(stats);

        if (vehicules.isEmpty()) {
            messageLabel.setText("Aucun véhicule trouvé pour générer les statistiques.");
        } else {
            messageLabel.setText("Statistiques énergétiques chargées avec succès.");
        }
    }

    private void afficherResume(Map<String, Integer> stats) {
        statsBox.getChildren().clear();

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            Label label = new Label(entry.getKey() + " : " + entry.getValue());
            label.setStyle("-fx-font-size: 13px; -fx-text-fill: #2c3e50;");
            statsBox.getChildren().add(label);
        }
    }

    @FXML
    private void monEspaceVehiculeAction() {
        try {
            NavigationManager.navigateFrom(messageLabel, "/GestionVehicule.fxml");
        } catch (IOException e) {
            messageLabel.setText("Erreur lors du retour vers Mon Espace Véhicule.");
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void impactEnergetiqueAction() {
        messageLabel.setText("Vous êtes déjà dans Impact énergétique.");
    }

    @FXML
    private void maintenanceAction() {
        try {
            NavigationManager.navigateFrom(messageLabel, "/Maintenance.fxml");
        } catch (IOException e) {
            messageLabel.setText("Erreur lors de l'ouverture de la page Maintenance.");
            System.out.println(e.getMessage());
        }
    }
}
