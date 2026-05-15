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
        boolean dashboard = fxml.equals("admin_dashboard.fxml")
                || fxml.equals("/AdminHomePage.fxml")
                || fxml.equals("/ConducteurHomePage.fxml")
                || fxml.equals("/PassagerHomePage.fxml");
        double width = dashboard ? 1000 : 600;
        double height = dashboard ? 750 : 700;
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch();
    }
}
