package com.gabrielavara.choiceplayer.controls.animatedbutton;

import static com.gabrielavara.choiceplayer.Constants.SHORT_ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.controls.animatedbutton.Direction.IN;
import static com.gabrielavara.choiceplayer.controls.animatedbutton.Direction.OUT;

import java.net.URL;
import java.util.ResourceBundle;

import com.gabrielavara.choiceplayer.controllers.PlayerController;
import com.jfoenix.controls.JFXButton;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import lombok.Setter;

public class AnimatedButtonController implements Initializable {
    @FXML
    public JFXButton playButton;
    @FXML
    public JFXButton pauseButton;
    @Setter
    private PlayerController controller;
    private boolean playShowed = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setInitialState();
        setButtonListeners();
    }

    private void setInitialState() {
        pauseButton.setOpacity(0);
        pauseButton.setRotate(-90);
        playButton.toFront();
    }

    private void setButtonListeners() {
        playButton.setOnMouseClicked(this::buttonClicked);
        pauseButton.setOnMouseClicked(this::buttonClicked);
    }

    public void play() {
        if (playShowed) {
            buttonClicked(null);
        }
    }

    @SuppressWarnings("squid:S1172")
    private void buttonClicked(MouseEvent e) {
        controller.playPause(false);
        animate();
    }

    public void animate() {
        animate(playButton, playShowed ? OUT : IN);
        animate(pauseButton, playShowed ? IN : OUT);
        playShowed = !playShowed;
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

        if (direction == IN) {
            parallelTransition.setOnFinished(e -> button.toFront());
        }

        parallelTransition.play();
    }
}
