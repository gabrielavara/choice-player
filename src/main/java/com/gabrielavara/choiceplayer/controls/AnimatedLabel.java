package com.gabrielavara.choiceplayer.controls;

import static com.gabrielavara.choiceplayer.Constants.ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.LABEL_TRANSLATE_X;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AnimatedLabel extends StackPane {
    private Label first;
    private Label second;

    public AnimatedLabel(String styleClass, Color color) {
        first = new Label();
        second = new Label();

        first.setOpacity(0);
        first.setTranslateX(LABEL_TRANSLATE_X);
        first.getStyleClass().add(styleClass);
        first.setTextFill(color);

        second.setOpacity(0);
        second.setTranslateX(LABEL_TRANSLATE_X);
        second.getStyleClass().add(styleClass);
        second.setTextFill(color);

        getChildren().addAll(second, first);

        setText("");
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

    public void setText(String text) {
        Platform.runLater(() -> {
            second.setText(text);
            animateOut(first);
            animateIn(second);
        });
    }
}
