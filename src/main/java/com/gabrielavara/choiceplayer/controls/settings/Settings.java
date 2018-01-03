package com.gabrielavara.choiceplayer.controls.settings;

import java.io.IOException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class Settings extends StackPane {
    private static final String COULD_NOT_LOAD = "Could not load settings";
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controls.settings.Settings");

    private SettingsController controller;

    public Settings() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("language.player");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/controls/settings.fxml"), resourceBundle);

        fxmlLoader.setControllerFactory(param -> {
            controller = new SettingsController();
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
}
