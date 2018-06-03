package com.gabrielavara.choiceplayer.controls.overlay;

import java.io.IOException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class Overlay extends StackPane {
    private static final String COULD_NOT_LOAD = "Could not load overlay";
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controls.overlay.Overlay");

    private OverlayController controller;

    public Overlay() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("language.player");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/controls/overlay.fxml"), resourceBundle);

        fxmlLoader.setControllerFactory(param -> {
            controller = new OverlayController();
            return controller;
        });

        try {
            Node view = fxmlLoader.load();
            getChildren().add(view);
        } catch (IOException e) {
            log.error(COULD_NOT_LOAD);
            throw new IllegalStateException(COULD_NOT_LOAD, e);
        }
    }

    public void setBackground() {
        controller.setBackground();
    }

    public void setSize(double height, double width) {
        controller.setSize(height, width);
    }
}

