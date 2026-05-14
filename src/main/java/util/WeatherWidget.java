package util;

// WeatherWidget.java
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherWidget extends VBox {

    private final Label cityLabel    = new Label("Chargement...");
    private final Label tempLabel    = new Label("--°C");
    private final Label descLabel    = new Label("");
    private final Label iconLabel    = new Label("🌤");

    public WeatherWidget() {
        buildUI();
        fetchWeather("Tunis"); // Remplace par la ville de l'utilisateur
    }

    private void buildUI() {
        setStyle("""
            -fx-background-color: white;
            -fx-border-color: #e5e7eb;
            -fx-border-width: 0.5;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            """);
        setPadding(new Insets(12, 16, 12, 16));
        setSpacing(6);

        // Titre section
        Label title = new Label("🌍  Météo locale");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #374151;");

        // Ligne principale : icône + temp
        HBox mainRow = new HBox(10);
        mainRow.setAlignment(Pos.CENTER_LEFT);
        iconLabel.setStyle("-fx-font-size: 28px;");
        tempLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        mainRow.getChildren().addAll(iconLabel, tempLabel);

        cityLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        getChildren().addAll(title, mainRow, cityLabel, descLabel);
    }

    private void fetchWeather(String city) {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://wttr.in/" + city + "?format=j1"))
                        .header("User-Agent", "FiThnitek/1.0")
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                JSONObject json    = new JSONObject(response.body());
                JSONObject current = json.getJSONArray("current_condition").getJSONObject(0);

                String temp     = current.getString("temp_C");
                String desc     = current.getJSONArray("weatherDesc")
                        .getJSONObject(0)
                        .getString("value");
                String humidity = current.getString("humidity");
                int    code     = Integer.parseInt(current.getString("weatherCode"));
                String icon     = weatherIcon(code);

                Platform.runLater(() -> {
                    tempLabel.setText(temp + "°C");
                    cityLabel.setText("📍 " + city + "  •  Humidité " + humidity + "%");
                    descLabel.setText(desc);
                    iconLabel.setText(icon);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    tempLabel.setText("--°C");
                    cityLabel.setText("Météo indisponible");
                });
            }
        }).start();
    }

    private String weatherIcon(int code) {
        if (code == 113) return "☀️";
        if (code == 116) return "⛅";
        if (code == 119 || code == 122) return "☁️";
        if (code >= 176 && code <= 263) return "🌧️";
        if (code >= 296 && code <= 321) return "🌦️";
        if (code >= 329 && code <= 395) return "❄️";
        if (code >= 200 && code <= 232) return "⛈️";
        return "🌡️";
    }
}
