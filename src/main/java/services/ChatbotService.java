package services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChatbotService {

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.3-70b-versatile";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String apiKey;

    public ChatbotService() {
        this.apiKey = System.getenv("GROQ_API_KEY");
    }

    public String askForumAssistant(String userMessage, String systemPrompt) {
        if (apiKey == null || apiKey.isBlank()) {
            return "Cle API manquante. Configure la variable d'environnement GROQ_API_KEY.";
        }

        try {
            String jsonBody = "{"
                    + "\"model\":\"" + MODEL + "\","
                    + "\"messages\":["
                    + "{\"role\":\"system\",\"content\":\"" + escapeJson(systemPrompt) + "\"},"
                    + "{\"role\":\"user\",\"content\":\"" + escapeJson(userMessage) + "\"}"
                    + "],"
                    + "\"max_tokens\":512"
                    + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return extractErrorMessage(response.body());
            }

            return extractAssistantText(response.body());
        } catch (Exception e) {
            return "Erreur de connexion (" + e.getMessage() + ")";
        }
    }

    private String extractAssistantText(String json) {
        String key = "\"content\":\"";
        int start = json.lastIndexOf(key);

        if (start == -1) {
            return "Reponse API inattendue.";
        }

        return readJsonString(json, start + key.length());
    }

    private String extractErrorMessage(String json) {
        String key = "\"message\":\"";
        int start = json.indexOf(key);

        if (start == -1) {
            return "Erreur API : " + json;
        }

        return "Erreur API : " + readJsonString(json, start + key.length());
    }

    private String readJsonString(String json, int start) {
        StringBuilder result = new StringBuilder();

        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(++i);
                switch (next) {
                    case 'n' -> result.append('\n');
                    case 't' -> result.append('\t');
                    case 'r' -> result.append('\r');
                    case '"' -> result.append('"');
                    case '\\' -> result.append('\\');
                    default -> result.append(next);
                }
            } else if (c == '"') {
                break;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
