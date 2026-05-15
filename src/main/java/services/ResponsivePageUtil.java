package services;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;

import java.util.ArrayList;
import java.util.List;

public final class ResponsivePageUtil {

    private static final double TOP_CLEARANCE = 28.0;

    private ResponsivePageUtil() {
    }

    public static void fitAnchorContent(AnchorPane root, double designWidth, double designHeight) {
        if (root == null) {
            return;
        }

        Platform.runLater(() -> {
            if (root.getChildren().size() == 1 && root.getChildren().get(0) instanceof Group) {
                bindScale(root, (Group) root.getChildren().get(0), designWidth, designHeight);
                return;
            }

            List<Node> originalChildren = new ArrayList<>(root.getChildren());
            Group content = new Group();
            content.getChildren().setAll(originalChildren);

            root.getChildren().setAll(content);
            root.setMinSize(0, 0);
            root.setPrefSize(designWidth, designHeight);
            bindScale(root, content, designWidth, designHeight);
        });
    }

    private static void bindScale(AnchorPane root, Group content, double designWidth, double designHeight) {
        Runnable resize = () -> {
            double availableWidth = Math.max(root.getWidth(), designWidth);
            double availableHeight = Math.max(root.getHeight(), designHeight);
            double scale = Math.min(availableWidth / designWidth, (availableHeight - TOP_CLEARANCE) / designHeight);

            content.getTransforms().setAll(new Scale(scale, scale, 0, 0));
            content.setTranslateX((availableWidth - designWidth * scale) / 2);
            content.setTranslateY(TOP_CLEARANCE);
        };

        root.widthProperty().addListener((obs, oldValue, newValue) -> resize.run());
        root.heightProperty().addListener((obs, oldValue, newValue) -> resize.run());
        resize.run();
    }
}
