package util;
// TipOfTheDayWidget.java

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.time.LocalDate;

public class TipOfTheDayWidget extends VBox {

    private static final String[] TIPS = {
            "🕐  Prévenez votre conducteur en cas de retard, même de 5 minutes.",
            "🔒  Vérifiez toujours le matricule du véhicule avant de monter.",
            "💬  Un message de confirmation avant le départ évite les malentendus.",
            "⭐  Laissez un avis après chaque trajet pour aider la communauté.",
            "🌦️  Vérifiez la météo avant de partir pour un long trajet.",
            "💳  Préparez le montant exact pour éviter les problèmes de monnaie.",
            "📍  Soyez au point de rendez-vous 5 min avant l'heure prévue.",
    };

    public TipOfTheDayWidget() {
        buildUI();
    }

    private void buildUI() {
        setStyle("""
            -fx-background-color: #fffbeb;
            -fx-border-color: #fcd34d;
            -fx-border-width: 0.5;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            """);
        setPadding(new Insets(12, 16, 12, 16));
        setSpacing(8);

        Label title = new Label("💡  Conseil du jour");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #92400e;");

        int index = LocalDate.now().getDayOfYear() % TIPS.length;
        Label tip = new Label(TIPS[index]);
        tip.setWrapText(true);
        tip.setStyle("-fx-font-size: 12px; -fx-text-fill: #78350f; -fx-line-spacing: 3;");

        // Numéro du jour
        Label dayLabel = new Label("Jour " + LocalDate.now().getDayOfYear());
        dayLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #b45309;");

        getChildren().addAll(title, tip, dayLabel);
    }
}