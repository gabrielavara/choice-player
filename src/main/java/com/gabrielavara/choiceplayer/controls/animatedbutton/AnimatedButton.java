package com.gabrielavara.choiceplayer.controls.animatedbutton;

import java.io.IOException;

import com.gabrielavara.choiceplayer.controllers.PlayerController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimatedButton extends AnchorPane {
    private static final String COULD_NOT_LOAD = "Could not load animated button";
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controls.animatedbutton.AnimatedButton");

    private AnimatedButtonController controller;

    public AnimatedButton() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/controls/animated_button.fxml"));

        fxmlLoader.setControllerFactory(param -> {
            controller = new AnimatedButtonController();
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

    public void setController(PlayerController playerController) {
        controller.setController(playerController);
    }

    public void play() {
        controller.play();
    }
}
