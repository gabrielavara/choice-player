package com.gabrielavara.choiceplayer.controls;

import static com.gabrielavara.choiceplayer.Constants.SHORT_ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.controls.Direction.IN;
import static com.gabrielavara.choiceplayer.controls.Direction.OUT;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.controllers.PlayerController;
import com.jfoenix.controls.JFXButton;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import lombok.Setter;

public class AnimatedButton extends AnchorPane {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controls.AnimatedButton");

    @FXML
    public JFXButton playButton;
    @FXML
    public JFXButton pauseButton;
    @Setter
    private PlayerController controller;
    private boolean playShowed = true;

    public AnimatedButton() {
        loadFxml();
        setInitialState();
        setButtonListeners();
    }

    private void loadFxml() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/controls/animated_button.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            log.error("Could not load animated button");
            throw new IllegalStateException("Could not load animated button", e);
        }
    }

    private void setInitialState() {
        pauseButton.setOpacity(0);
        pauseButton.setRotate(-90);
    }

    private void setButtonListeners() {
        playButton.setOnMouseClicked(e -> {
            controller.playPause();
            animate(playButton, playShowed ? OUT : IN);
            animate(pauseButton, playShowed ? IN : OUT);
            playShowed = !playShowed;
        });
        pauseButton.setOnMouseClicked(e -> controller.playPause());
    }

    private void animate(JFXButton button, Direction direction) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(SHORT_ANIMATION_DURATION), button);
        fadeTransition.setFromValue(direction == IN ? 0 : 1);
        fadeTransition.setToValue(direction == IN ? 1 : 0);

        RotateTransition rotateTransition = new RotateTransition(Duration.millis(SHORT_ANIMATION_DURATION), button);
        rotateTransition.setFromAngle(direction == IN ? -90 : 0);
        rotateTransition.setToAngle(direction == IN ? 0 : 90);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition, rotateTransition);

        parallelTransition.play();
    }

}

enum Direction {
    IN, OUT
}
