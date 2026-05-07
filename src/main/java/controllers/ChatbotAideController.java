package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ChatbotAideController {

    @FXML private ListView<String> chatList;
    @FXML private TextField inputField;
    private void ajouterMessageBot(String msg) {
        chatList.getItems().add("Bot: " + msg);
    }
    @FXML
    void sendMessage() {

        String msg = inputField.getText().toLowerCase().trim();
        if (msg.isEmpty()) return;

        // message utilisateur
        chatList.getItems().add("👤 " + msg);

        // réponse bot
        chatList.getItems().add("🤖 " + getResponse(msg));

        inputField.clear();
    }

    private String getResponse(String msg) {

        // --- NAVIGATION ---
        if (msg.contains("hello") || msg.contains("hey")  || msg.contains("hi")  || msg.contains("aslema")  || msg.contains("bonjour") || msg.contains("asslema") ) {
            return "ASSLEMA ! Dis moi.";
        }

        if (msg.contains("comment ajouter") || msg.contains("poster")) {
            return "Va dans 'Ajouter Forum' pour créer un nouveau post.";
        }

        if (msg.contains("mes posts")) {
            return "Va dans 'Mes Forums' pour voir uniquement tes publications.";
        }

        if (msg.contains("liste")) {
            return "Dans 'Liste Forum' tu vois tous les posts des utilisateurs.";
        }

        // --- ACTIONS ---
        if (msg.contains("comment supprimer")) {
            return "Dans 'Mes Forums', sélectionne un post puis clique sur Supprimer.";
        }

        if (msg.contains("comment modifier")) {
            return "Dans 'Mes Forums', sélectionne un post puis clique sur Modifier.";
        }

        if (msg.contains("comment commenter")) {
            return "Ouvre un post puis clique sur 'Voir commentaires'.";
        }

        if (msg.contains("epingle")) {
            return "Dans 'Mes Forums', utilise le bouton 📌 pour épingler un post.";
        }

        // --- HELP ---
        if (msg.contains("help") || msg.contains("aide")) {
            return "Je peux t'aider avec : poster, supprimer, modifier, commentaires, navigation.";
        }

        return "Je ne comprends pas 😅 Essaie : 'comment poster' ou 'mes posts'.";
    }
}