package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ChatbotAideController {

    @FXML private ListView<String> chatList;
    @FXML private TextField inputField;


    private static final String API_KEY = ""; // ← remplace par ta clé Groq
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.3-70b-versatile";
    private static final String SYSTEM_PROMPT =
            "Tu es un assistant IA intégré dans une application de gestion de forum. " +
                    "Réponds toujours dans la même langue que l'utilisateur (français, anglais, arabe, etc.). " +
                    "Tu peux répondre aux salutations normalement. " +
                    "Tu réponds UNIQUEMENT aux questions concernant les fonctionnalités suivantes de l'application : " +
                    "1. Ajouter Forum : créer un nouveau post. " +
                    "2. Mes Forums / Mes Posts : consulter ses propres posts. " +
                    "3. Liste Forum : consulter tous les posts des utilisateurs. " +
                    "4. Rechercher un post : utiliser la barre de recherche pour trouver un post. " +
                    "5. Détails d'un post : double-cliquer sur un post ou cliquer sur 'Voir détails'. " +
                    "6. Commentaires : commenter un post, liker un commentaire, supprimer un commentaire. " +
                    "7. Épingler un post : depuis 'Mes Forums' avec le bouton épingler. " +
                    "8. Modifier / Supprimer un post : depuis 'Mes Forums'. " +
                    "9. Activité utilisateur : consulter son historique et activité depuis son profil. " +
                    "Si l'utilisateur pose une question qui ne concerne PAS ces fonctionnalités, " +
                    "réponds dans sa langue : je suis limité aux fonctionnalités du forum. " +
                    "Réponds de façon courte et utile.";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void initialize() {
        inputField.setOnAction(event -> sendMessage());
        chatList.getItems().add("🤖 Asslema ! Je suis ton assistant IA. Comment puis-je t'aider ?");
    }

    @FXML
    void sendMessage() {
        String userText = inputField.getText().trim();
        if (userText.isEmpty()) return;

        chatList.getItems().add("👤 " + userText);
        inputField.clear();
        inputField.setDisable(true);
        chatList.getItems().add("🤖 ...");

        CompletableFuture.supplyAsync(() -> callGroqAPI(userText))
                .thenAccept(response -> Platform.runLater(() -> {
                    int last = chatList.getItems().size() - 1;
                    chatList.getItems().set(last, "🤖 " + response);
                    chatList.scrollTo(last);
                    inputField.setDisable(false);
                    inputField.requestFocus();
                }));
    }

    private String callGroqAPI(String userMessage) {
        try {
            String escapedSystem = escapeJson(SYSTEM_PROMPT);
            String escapedUser   = escapeJson(userMessage);

            // Format OpenAI-compatible (utilisé par Groq)
            String jsonBody = "{"
                    + "\"model\":\"" + MODEL + "\","
                    + "\"messages\":["
                    + "{\"role\":\"system\",\"content\":\"" + escapedSystem + "\"},"
                    + "{\"role\":\"user\",\"content\":\"" + escapedUser + "\"}"
                    + "],"
                    + "\"max_tokens\":512"
                    + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString()
            );

            return extractGroqText(response.body());

        } catch (Exception e) {
            return "Erreur de connexion 😅 (" + e.getMessage() + ")";
        }
    }

    // Extraire "content" de la réponse Groq
    // Structure : ...{"message":{"role":"assistant","content":"réponse"}}...
    private String extractGroqText(String json) {
        String key = "\"content\":\"";
        // Chercher le dernier "content" qui est la réponse assistant
        int start = json.lastIndexOf(key);

        if (start == -1) {
            // Chercher message d'erreur
            String errKey = "\"message\":\"";
            int errStart = json.indexOf(errKey);
            if (errStart != -1) {
                errStart += errKey.length();
                int errEnd = json.indexOf("\"", errStart);
                return "Erreur API : " + json.substring(errStart, errEnd);
            }
            return "Réponse inattendue 😅";
        }

        start += key.length();

        StringBuilder result = new StringBuilder();
        int i = start;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                switch (next) {
                    case 'n'  -> result.append('\n');
                    case 't'  -> result.append('\t');
                    case '"'  -> result.append('"');
                    case '\\' -> result.append('\\');
                    default   -> result.append(next);
                }
                i += 2;
            } else if (c == '"') {
                break;
            } else {
                result.append(c);
                i++;
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