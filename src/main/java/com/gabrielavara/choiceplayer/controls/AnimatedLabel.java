package com.gabrielavara.choiceplayer.controls;

import static com.gabrielavara.choiceplayer.Constants.SHORT_ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.TRANSLATE_X;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class AnimatedLabel extends StackPane {
    private Label first;
    private Label second;

    public AnimatedLabel(String styleClass) {
        first = new Label();
        second = new Label();

        first.setOpacity(0);
        first.setTranslateX(TRANSLATE_X);
        first.getStyleClass().add(styleClass);

        second.setOpacity(0);
        second.setTranslateX(TRANSLATE_X);
        second.getStyleClass().add(styleClass);

        getChildren().addAll(second, first);

        setText("");
    }

    private void animateIn(Label label) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(SHORT_ANIMATION_DURATION), label);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(SHORT_ANIMATION_DURATION), label);
        translateTransition.setByX(-TRANSLATE_X);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition, translateTransition);

        parallelTransition.setOnFinished(event -> {
            Label temp = first;
            first = second;
            second = temp;
            second.setTranslateX(TRANSLATE_X);
        });
        parallelTransition.play();
    }

    private void animateOut(Label label) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(SHORT_ANIMATION_DURATION), label);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(SHORT_ANIMATION_DURATION), label);
        translateTransition.setByX(-TRANSLATE_X);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition, translateTransition);

        parallelTransition.play();
    }

    public void setText(String text) {
        Platform.runLater(() -> {
            second.setText(text);
            animateOut(first);
            animateIn(second);
        });
    }
}