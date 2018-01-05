package com.gabrielavara.choiceplayer.controls.animatedlabel;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class AnimatedLabel extends StackPane {
    private static final String COULD_NOT_LOAD = "Could not load animated label";
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controls.animatedlabel.AnimatedLabel");

    private AnimatedLabelController controller;

    public AnimatedLabel() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/controls/animated_label.fxml"));

        fxmlLoader.setControllerFactory(param -> {
            controller = new AnimatedLabelController();
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

    public void setStyleClass(String cssClass) {
        controller.setStyleClass(cssClass);
    }

    public void setText(String text) {
        controller.setText(text);
    }
}
