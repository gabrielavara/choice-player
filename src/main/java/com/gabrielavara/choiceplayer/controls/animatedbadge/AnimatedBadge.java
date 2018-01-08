package com.gabrielavara.choiceplayer.controls.animatedbadge;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class AnimatedBadge extends StackPane {
    private static final String COULD_NOT_LOAD = "Could not load animated badge";
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controls.animatedbutton.AnimatedBadge");

    private AnimatedBadgeController controller;

    public AnimatedBadge(StackPane rootContainer, Button toDecorate) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/controls/animated_badge.fxml"));

        fxmlLoader.setControllerFactory(param -> {
            controller = new AnimatedBadgeController(rootContainer, toDecorate);
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

    public void increaseCount() {
        controller.increaseCount();
    }
}
