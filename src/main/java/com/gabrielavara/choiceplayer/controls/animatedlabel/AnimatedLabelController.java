package com.gabrielavara.choiceplayer.controls.animatedlabel;

import static com.gabrielavara.choiceplayer.Constants.ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.LABEL_TRANSLATE_X;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class AnimatedLabelController implements Initializable {
    @FXML
    public Label first;
    @FXML
    public Label second;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        first.setOpacity(0);
        first.setTranslateX(LABEL_TRANSLATE_X);
        second.setOpacity(0);
        second.setTranslateX(LABEL_TRANSLATE_X);
    }

    public void setStyleClass(String styleClass) {
        first.getStyleClass().add(styleClass);
        second.getStyleClass().add(styleClass);
    }

    public void setText(String text) {
        Platform.runLater(() -> {
            second.setText(text);
            animateOut(first);
            animateIn(second);
        });
    }

    private void animateIn(Label label) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(ANIMATION_DURATION * 2), label);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(ANIMATION_DURATION * 2), label);
        translateTransition.setByX(-LABEL_TRANSLATE_X);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition, translateTransition);

        parallelTransition.setOnFinished(event -> {
            Label temp = first;
            first = second;
            second = temp;
            second.setTranslateX(LABEL_TRANSLATE_X);
        });
        parallelTransition.play();
    }

    private void animateOut(Label label) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(ANIMATION_DURATION * 2), label);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(ANIMATION_DURATION * 2), label);
        translateTransition.setByX(-LABEL_TRANSLATE_X);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition, translateTransition);

        parallelTransition.play();
    }
}
