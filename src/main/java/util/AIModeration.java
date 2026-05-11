package util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class AIModeration {

    // 🔑 Clé Perspective API (gratuite sur https://perspectiveapi.com)
    // À mettre dans une variable d'environnement, PAS ici !
    private static final String API_KEY = System.getenv("");

    private static final String API_URL =
            "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=" + API_KEY;

    private static final HttpClient client = HttpClient.newHttpClient();

    // Seuil de toxicité (0.0 à 1.0) — 0.7 = strict, 0.8 = modéré
    private static final double TOXICITY_THRESHOLD = 0.75;

    // ─────────────────────────────────────────
    // 🟢 MÉTHODE PRINCIPALE
    // ─────────────────────────────────────────
    public static boolean isInappropriate(String text) {
        if (text == null || text.isBlank()) return true;

        // 1. Filtre local rapide (mots évidents sans appel réseau)
        if (localCheck(text)) return true;

        // 2. Analyse IA via Perspective API
        try {
            String requestBody = buildRequest(text);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Perspective API error: " + response.statusCode());
                return true; // fail-safe
            }

            return parseScore(response.body()) >= TOXICITY_THRESHOLD;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return true; // fail-safe
        }
    }

    // ─────────────────────────────────────────
    // 🧠 FILTRE LOCAL (mots évidents, zéro latence)
    // ─────────────────────────────────────────
    private static final List<String> BAD_WORDS = List.of(
            "kill", "tuer", "merde", "connard", "pute",
            "kahba", "9ahba", "kalb", "7mar", "zebi",
            "hate", "racist", "raciste", "اذبح", "نقتل"
    );

    private static boolean localCheck(String text) {
        String lower = text.toLowerCase();

        // Mots interdits directs
        for (String word : BAD_WORDS) {
            if (lower.contains(word)) return true;
        }

        // Spam par répétition de caractère (ex: "aaaaaaaaaa")
        if (text.matches(".*(.)\\1{8,}.*")) return true;

        // Insultes masquées (espaces ou points entre lettres : t.f.o.u / t f o u)
        String stripped = lower.replaceAll("[^a-z\u0600-\u06FF]", "");
        if (stripped.contains("tfou") || stripped.contains("kahba")) return true;

        return false;
    }

    // ─────────────────────────────────────────
    // 📦 CONSTRUCTION DE LA REQUÊTE JSON
    // ─────────────────────────────────────────
    private static String buildRequest(String text) {
        String safe = escapeJson(text);

        // Perspective supporte FR, EN, AR nativement
        return "{"
                + "\"comment\":{\"text\":\"" + safe + "\"},"
                + "\"languages\":[\"fr\",\"en\",\"ar\"],"
                + "\"requestedAttributes\":{"
                + "  \"TOXICITY\":{},"
                + "  \"INSULT\":{},"
                + "  \"THREAT\":{},"
                + "  \"IDENTITY_ATTACK\":{}"
                + "}"
                + "}";
    }

    // ─────────────────────────────────────────
    // 📊 PARSING DU SCORE (prend le max des attributs)
    // ─────────────────────────────────────────
    private static double parseScore(String json) {
        double max = 0.0;
        // Extrait tous les summaryScore value
        String[] parts = json.split("\"summaryScore\"");
        for (int i = 1; i < parts.length; i++) {
            try {
                String segment = parts[i];
                int vi = segment.indexOf("\"value\":");
                if (vi == -1) continue;
                int start = vi + 8;
                int end = segment.indexOf(",", start);
                if (end == -1) end = segment.indexOf("}", start);
                double score = Double.parseDouble(segment.substring(start, end).trim());
                if (score > max) max = score;
            } catch (NumberFormatException ignored) {}
        }
        return max;
    }

    // ─────────────────────────────────────────
    // 🛡️ ESCAPE JSON
    // ─────────────────────────────────────────
    private static String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}