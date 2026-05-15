package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import models.publication;
import services.forumService;
import services.NavigationManager;
import services.SessionManager;

import java.util.List;

public class MonActiviteController {

    @FXML private Label scoreLabel;
    @FXML private Label badgeLabel;
    @FXML private BarChart<String, Number> activityChart;

    private final forumService forumService = new forumService();

    @FXML
    public void initialize() {
        afficherStats();
        chargerDiagramme();
    }

    private void afficherStats() {
        int userId = SessionManager.getCurrentUser().getId(); // âœ… session rÃ©elle
        int score  = forumService.calculScore(userId);
        String badge = forumService.getBadge(userId);
        scoreLabel.setText(String.valueOf(score));
        badgeLabel.setText(badge);
    }

    private void chargerDiagramme() {
        activityChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("ActivitÃ©");

        List<publication> posts = forumService.getAll();
        int totalPosts   = posts.size();
        int epingles     = (int) posts.stream().filter(publication::isEpingle).count();
        int questions    = (int) posts.stream().filter(p -> "question".equalsIgnoreCase(p.getCategorie())).count();
        int discussions  = (int) posts.stream().filter(p -> "discussion".equalsIgnoreCase(p.getCategorie())).count();

        series.getData().add(new XYChart.Data<>("Posts",       totalPosts));
        series.getData().add(new XYChart.Data<>("Ã‰pinglÃ©s",    epingles));
        series.getData().add(new XYChart.Data<>("Questions",   questions));
        series.getData().add(new XYChart.Data<>("Discussions", discussions));

        activityChart.getData().add(series);
    }

    @FXML
    void retourMesForumsAction(ActionEvent event) {
        // âœ… Retour dans le conteneur FORUM
        try {
            NavigationManager.navigateFrom(scoreLabel, "/MesForums.FXML");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

