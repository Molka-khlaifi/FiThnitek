package controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Home extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("FiThnitek — Connexion");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Appelé depuis LoginController après authentification réussie.
     * Redirige vers la home page selon le rôle de l'utilisateur connecté.
     *
     * @param stage  le stage courant
     * @param role   "admin" | "conducteur" | "passager"
     */
    public static void naviguerVersHome(Stage stage, String role) {
        try {
            String fxml;
            String titre;

            switch (role.toLowerCase()) {
                case "admin":
                    fxml  = "/AdminHome.fxml";
                    titre = "FiThnitek— Administration";
                    break;
                case "conducteur":
                    fxml  = "/ConducteurHome.fxml";
                    titre = "FiThnitek — Espace Conducteur";
                    break;
                case "passager":
                    fxml  = "/PassagerHome.fxml";
                    titre = "FiThnitek — Espace Passager";
                    break;
                default:
                    System.out.println("Rôle inconnu : " + role);
                    return;
            }

            Parent root = FXMLLoader.load(Home.class.getResource(fxml));
            Scene scene = new Scene(root);
            stage.setTitle(titre);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}