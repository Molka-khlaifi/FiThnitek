package com.example.couvoiturage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        changeScene("login.fxml", "Fi Thnitek - Connexion");
        stage.show();
    }

    public static void changeScene(String fxml, String title) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxml));
        double width = fxml.equals("admin_dashboard.fxml") ? 1000 : 600;
        double height = fxml.equals("admin_dashboard.fxml") ? 750 : 700;
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch();
    }
}
