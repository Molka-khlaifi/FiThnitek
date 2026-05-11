package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import services.forumService;
import models.publication;

import java.io.IOException;
import java.util.List;

public class MonActiviteController {

    @FXML private Label scoreLabel;
    @FXML private Label badgeLabel;
    @FXML private BarChart<String, Number> activityChart;

    private final forumService forumService = new forumService();
    private final int currentUserId = 1;

    @FXML
    public void initialize() {
        afficherStats();
        chargerDiagramme();
    }

    // ───────────────── STATS ─────────────────

    private void afficherStats() {
        int score = forumService.calculScore(currentUserId);
        String badge = forumService.getBadge(currentUserId);

        scoreLabel.setText(String.valueOf(score));
        badgeLabel.setText(badge);
    }

    // ───────────────── CHART ─────────────────

    private void chargerDiagramme() {

        activityChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Activité");

        List<publication> posts = forumService.getAll();

        int totalPosts = posts.size();

        int epingles = (int) posts.stream()
                .filter(publication::isEpingle)
                .count();

        int questions = (int) posts.stream()
                .filter(p -> "question".equalsIgnoreCase(p.getCategorie()))
                .count();

        int discussions = (int) posts.stream()
                .filter(p -> "discussion".equalsIgnoreCase(p.getCategorie()))
                .count();

        series.getData().add(new XYChart.Data<>("Posts", totalPosts));
        series.getData().add(new XYChart.Data<>("Épinglés", epingles));
        series.getData().add(new XYChart.Data<>("Questions", questions));
        series.getData().add(new XYChart.Data<>("Discussions", discussions));

        activityChart.getData().add(series);
    }

    // ───────────────── RETOUR ─────────────────

    @FXML
    void retourMesForumsAction(ActionEvent event) {

        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/MesForums.fxml")
            );

            activityChart.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}