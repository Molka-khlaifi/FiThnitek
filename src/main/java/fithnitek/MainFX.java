package fithnitek;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(
                        getClass().getResource("/dashboard.fxml")
                )
        );

        Scene scene = new Scene(
                loader.load(),
                1280,
                800
        );

        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/style.css")
                ).toExternalForm()
        );

        primaryStage.setTitle(
                "Fthnitek - Covoiturage Intelligent"
        );

        primaryStage.setScene(scene);

        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);

        primaryStage.show();
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) {

        launch(args);
    }
}