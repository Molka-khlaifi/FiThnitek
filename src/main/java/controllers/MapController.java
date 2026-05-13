package controllers;

import entities.Trajet;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import services.MapService;
import services.ServiceTrajet;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MapController implements Initializable {

    @FXML private WebView           webView;
    @FXML private ComboBox<Trajet>  cbTrajets;
    @FXML private Label             lblInfo;
    @FXML private ProgressIndicator progressMap;

    private final MapService    mapService = new MapService();
    private final ServiceTrajet service    = new ServiceTrajet();
    private WebEngine engine;

    // Scheduler pour forcer invalidateSize apres chargement
    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "map-invalidate");
                t.setDaemon(true);
                return t;
            });

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        engine = webView.getEngine();
        engine.setJavaScriptEnabled(true);

        // Quand la page finit de charger, forcer invalidateSize
        // plusieurs fois pour corriger les tuiles grises
        engine.getLoadWorker().stateProperty().addListener(
                (obs, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        scheduleInvalidate();
                    }
                }
        );

        // Ecouter le redimensionnement du WebView et re-invalider
        ChangeListener<Number> sizeListener = (obs, o, n) ->
                Platform.runLater(this::invalidateMap);

        webView.widthProperty().addListener(sizeListener);
        webView.heightProperty().addListener(sizeListener);

        // Charge la vue d'ensemble
        loadPage(mapService.buildOverviewMapHtml());

        // Remplit le ComboBox
        try {
            List<Trajet> trajets = service.getAll();
            cbTrajets.setItems(FXCollections.observableArrayList(trajets));
            cbTrajets.setConverter(new javafx.util.StringConverter<>() {
                public String toString(Trajet t) {
                    return t == null ? "Selectionner un trajet"
                            : t.getDepart() + " -> " + t.getDestination();
                }
                public Trajet fromString(String s) { return null; }
            });
        } catch (SQLException e) {
            if (lblInfo != null) lblInfo.setText("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void handleShowTrajet() {
        Trajet t = cbTrajets.getValue();
        if (t == null) {
            if (lblInfo != null) lblInfo.setText("Veuillez selectionner un trajet.");
            return;
        }
        if (progressMap != null) progressMap.setVisible(true);
        if (lblInfo != null)
            lblInfo.setText(t.getDepart() + " -> " + t.getDestination()
                    + "  |  " + String.format("%.2f DT", t.getPrix())
                    + "  |  " + t.getPlacesDisponibles() + "/" + t.getPlacesTotal()
                    + " places  |  " + t.getStatut());

        String dep  = t.getDepart();
        String dest = t.getDestination();
        new Thread(() -> {
            String html = mapService.buildMapHtml(dep, dest);
            Platform.runLater(() -> {
                loadPage(html);
                if (progressMap != null) progressMap.setVisible(false);
            });
        }).start();
    }

    @FXML
    private void handleOverview() {
        loadPage(mapService.buildOverviewMapHtml());
        if (lblInfo != null) lblInfo.setText("Vue d'ensemble — Tunisie");
        cbTrajets.setValue(null);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void loadPage(String html) {
        engine.loadContent(html, "text/html");
    }

    /** Appelle map.invalidateSize() dans Leaflet depuis Java */
    private void invalidateMap() {
        try {
            engine.executeScript(
                    "if(typeof map !== 'undefined') {"
                            + "  map.invalidateSize({animate:false, pan:false});"
                            + "}"
            );
        } catch (Exception ignored) {}
    }

    /** Planifie plusieurs appels a invalidateSize apres chargement */
    private void scheduleInvalidate() {
        long[] delays = {100, 300, 600, 1000, 2000, 3500};
        for (long delay : delays) {
            scheduler.schedule(
                    () -> Platform.runLater(this::invalidateMap),
                    delay,
                    TimeUnit.MILLISECONDS
            );
        }
    }
}