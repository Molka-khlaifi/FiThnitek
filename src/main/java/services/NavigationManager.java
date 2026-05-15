package services;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class NavigationManager {

    private static Stage primaryStage;
    private static Map<String, AnchorPane> tabContainers = new HashMap<>();

    // Navigation en attente si le conteneur n'est pas encore prêt
    private static Map<String, String> pendingNavigations = new HashMap<>();

    public static void registerTabContainer(String tabName, AnchorPane container) {
        tabContainers.put(tabName, container);
        System.out.println("✅ Conteneur enregistré: " + tabName);

        // Exécuter la navigation en attente s'il y en a une
        if (pendingNavigations.containsKey(tabName)) {
            String fxmlPath = pendingNavigations.remove(tabName);
            System.out.println("▶️ Navigation en attente exécutée: " + tabName + " → " + fxmlPath);
            navigateInTab(tabName, fxmlPath);
        }
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void navigateInTab(String tabName, String fxmlPath) {
        AnchorPane container = tabContainers.get(tabName);

        if (container == null) {
            if (loadIntoDashboardTab(tabName, fxmlPath)) {
                return;
            }

            // Mettre en file d'attente au lieu d'abandonner
            System.out.println("⏳ Conteneur pas encore prêt, navigation mise en attente: " + tabName);
            pendingNavigations.put(tabName, fxmlPath);
            return;
        }

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        NavigationManager.class.getResource(fxmlPath)
                );

                if (loader.getLocation() == null) {
                    System.err.println("❌ FXML introuvable: " + fxmlPath);
                    return;
                }

                Parent view = loader.load();

                container.getChildren().clear();
                container.getChildren().add(view);

                AnchorPane.setTopAnchor(view, 0.0);
                AnchorPane.setBottomAnchor(view, 0.0);
                AnchorPane.setLeftAnchor(view, 0.0);
                AnchorPane.setRightAnchor(view, 0.0);

                System.out.println("✅ Navigation réussie: " + tabName + " → " + fxmlPath);

            } catch (IOException e) {
                System.err.println("❌ Erreur chargement FXML: " + fxmlPath);
                e.printStackTrace();
            }
        });
    }

    // Sélectionner l'onglet ET charger le contenu en même temps
    public static void navigateTo(String tabName, String fxmlPath) {
        selectTab(tabName);
        navigateInTab(tabName, fxmlPath);
    }

    private static boolean loadIntoDashboardTab(String tabName, String fxmlPath) {
        if (primaryStage == null || primaryStage.getScene() == null) {
            return false;
        }

        TabPane mainTabPane = (TabPane) primaryStage.getScene().lookup("#mainTabPane");
        if (mainTabPane == null) {
            return false;
        }

        Platform.runLater(() -> {
            Tab targetTab = findTab(mainTabPane, tabName);
            if (targetTab == null) {
                System.err.println("❌ Aucun onglet trouvé avec le nom: " + tabName);
                return;
            }

            try {
                URL resource = NavigationManager.class.getResource(fxmlPath);
                if (resource == null) {
                    System.err.println("❌ FXML introuvable: " + fxmlPath);
                    return;
                }

                Parent view = FXMLLoader.load(resource);
                targetTab.setContent(view);
                mainTabPane.getSelectionModel().select(targetTab);
                System.out.println("✅ Navigation dashboard: " + tabName + " → " + fxmlPath);
            } catch (IOException e) {
                System.err.println("❌ Erreur chargement FXML: " + fxmlPath);
                e.printStackTrace();
            }
        });

        return true;
    }

    private static Tab findTab(TabPane tabPane, String tabName) {
        for (Tab tab : tabPane.getTabs()) {
            String tabId = tab.getId() != null ? tab.getId() : "";
            String tabText = tab.getText() != null ? tab.getText() : "";

            if (tabId.equalsIgnoreCase(tabName) || tabText.equalsIgnoreCase(tabName)) {
                return tab;
            }
        }
        return null;
    }

    public static void selectTab(String tabName) {
        if (primaryStage == null) {
            System.err.println("❌ PrimaryStage non défini");
            return;
        }

        Platform.runLater(() -> {
            try {
                TabPane mainTabPane = (TabPane) primaryStage.getScene().lookup("#mainTabPane");

                if (mainTabPane == null) {
                    System.err.println("❌ #mainTabPane introuvable dans la scène");
                    return;
                }

                for (Tab tab : mainTabPane.getTabs()) {
                    String tabId = tab.getId() != null ? tab.getId() : "";
                    String tabText = tab.getText() != null ? tab.getText() : "";

                    if (tabId.equalsIgnoreCase(tabName) || tabText.equalsIgnoreCase(tabName)) {
                        mainTabPane.getSelectionModel().select(tab);
                        System.out.println("✅ Onglet sélectionné: " + tab.getText());
                        return;
                    }
                }

                System.err.println("❌ Aucun onglet trouvé avec le nom: " + tabName);

            } catch (Exception e) {
                System.err.println("❌ Erreur sélection onglet: " + e.getMessage());
            }
        });

    }
    // Dans NavigationManager.java — à ajouter
    public static void loadIntoTab(String tabName, Parent view) {
        AnchorPane container = tabContainers.get(tabName);
        if (container == null) {
            System.err.println("❌ Conteneur non trouvé: " + tabName);
            return;
        }
        Platform.runLater(() -> {
            container.getChildren().clear();
            container.getChildren().add(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
        });
    }

    public static <T> T navigateFrom(Node source, String fxmlPath, Consumer<T> controllerConfigurer) throws IOException {
        URL resource = NavigationManager.class.getResource(fxmlPath);
        if (resource == null) {
            throw new IOException("FXML introuvable: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Parent view = loader.load();
        T controller = loader.getController();

        if (controllerConfigurer != null && controller != null) {
            controllerConfigurer.accept(controller);
        }

        showFrom(source, view);
        return controller;
    }

    public static void navigateFrom(Node source, String fxmlPath) throws IOException {
        navigateFrom(source, fxmlPath, null);
    }

    private static void showFrom(Node source, Parent view) {
        Scene scene = source.getScene();
        if (scene == null) {
            return;
        }

        TabPane mainTabPane = (TabPane) scene.lookup("#mainTabPane");
        if (mainTabPane != null && mainTabPane.getSelectionModel().getSelectedItem() != null) {
            mainTabPane.getSelectionModel().getSelectedItem().setContent(view);
        } else {
            scene.setRoot(view);
        }
    }
}
