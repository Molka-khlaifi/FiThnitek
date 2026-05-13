package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // ✅ CORRECTION: Utiliser javafx.scene.image.Image au lieu de java.awt.Image
        try {
            Image icon = new Image(getClass().getResourceAsStream("/FT.png"));
            primaryStage.getIcons().add(icon);
            System.out.println("✅ Logo chargé avec succès");
        } catch (Exception e) {
            System.out.println("⚠️ Logo non trouvé, utilisation de l'icône par défaut");
        }

        changeScene("login.fxml", "FiThnitek - Connexion");
        stage.show();
    }

    public static void changeScene(String fxml, String title) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/" + fxml));

        // Amélioration des tailles
        double width;
        double height;

        switch (fxml) {
            case "admin_dashboard.fxml":
                width = 1200;
                height = 800;
                break;
            case "ConducteurHomePage.fxml":
            case "PassagerHomePage.fxml":
                width = 1300;
                height = 800;
                break;
            default:
                width = 600;
                height = 700;
                break;
        }

        Scene scene = new Scene(fxmlLoader.load(), width, height);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();

        // Garder le logo après changement de scène
        try {
            Image icon = new Image(Main.class.getResourceAsStream("/FT.png"));
            if (primaryStage.getIcons().isEmpty()) {
                primaryStage.getIcons().add(icon);
            }
        } catch (Exception e) {
            // Ignorer
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}