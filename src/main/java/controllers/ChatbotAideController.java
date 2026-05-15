package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import services.ChatbotService;

import java.util.concurrent.CompletableFuture;

public class ChatbotAideController {

    @FXML private ListView<String> chatList;
    @FXML private TextField inputField;

    private static final String SYSTEM_PROMPT =
            "Tu es un assistant IA integre dans une application de gestion de forum. " +
            "Reponds toujours dans la meme langue que l'utilisateur. " +
            "Tu peux repondre aux salutations normalement. " +
            "Tu reponds uniquement aux questions concernant ces fonctionnalites de l'application : " +
            "1. Ajouter Forum : creer un nouveau post. " +
            "2. Mes Forums / Mes Posts : consulter ses propres posts. " +
            "3. Liste Forum : consulter tous les posts des utilisateurs. " +
            "4. Rechercher un post : utiliser la barre de recherche pour trouver un post. " +
            "5. Details d'un post : double-cliquer sur un post ou cliquer sur Voir details. " +
            "6. Commentaires : commenter un post, liker un commentaire, supprimer un commentaire. " +
            "7. Epingler un post : depuis Mes Forums avec le bouton epingler. " +
            "8. Modifier / Supprimer un post : depuis Mes Forums. " +
            "9. Activite utilisateur : consulter son historique depuis son profil. " +
            "Si la question ne concerne pas ces fonctionnalites, dis que tu es limite aux fonctionnalites du forum. " +
            "Reponds de facon courte et utile.";

    private final ChatbotService chatbotService = new ChatbotService();

    @FXML
    public void initialize() {
        inputField.setOnAction(event -> sendMessage());
        chatList.getItems().add("Assistant IA: Asslema ! Comment puis-je t'aider sur le forum ?");
    }

    @FXML
    void sendMessage() {
        String userText = inputField.getText().trim();
        if (userText.isEmpty()) {
            return;
        }

        chatList.getItems().add("Vous: " + userText);
        inputField.clear();
        inputField.setDisable(true);
        chatList.getItems().add("Assistant IA: ...");

        CompletableFuture.supplyAsync(() -> chatbotService.askForumAssistant(userText, SYSTEM_PROMPT))
                .thenAccept(response -> Platform.runLater(() -> {
                    int last = chatList.getItems().size() - 1;
                    chatList.getItems().set(last, "Assistant IA: " + response);
                    chatList.scrollTo(last);
                    inputField.setDisable(false);
                    inputField.requestFocus();
                }));
    }
}
