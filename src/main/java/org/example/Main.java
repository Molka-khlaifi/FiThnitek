
package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // ✅ Première interface : Ajouter une Réclamation
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/AjouterReclamation.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Covoiturage - Réclamation");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}