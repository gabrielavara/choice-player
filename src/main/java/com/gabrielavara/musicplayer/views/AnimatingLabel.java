package com.gabrielavara.musicplayer.views;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class AnimatingLabel extends StackPane {
    private static final int TRANSLATE_X = 20;
    private Label first;
    private Label second;

    public AnimatingLabel(String text, int fontSize) {
        first = new Label();
        second = new Label();

        first.setFont(new Font(fontSize));
        first.setOpacity(0);
        first.setTranslateX(TRANSLATE_X);

        second.setFont(new Font(fontSize));
        second.setOpacity(0);
        second.setTranslateX(TRANSLATE_X);
        getChildren().addAll(second, first);

        setText(text);
    }

    private void animateIn(Label label) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), label);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), label);
        translateTransition.setByX(-TRANSLATE_X);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition, translateTransition);

        parallelTransition.play();
        parallelTransition.setOnFinished(event -> {
            Label temp = first;
            first = second;
            second = temp;
            second.setTranslateX(TRANSLATE_X);
        });
    }

    private void animateOut(Label label) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), label);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), label);
        translateTransition.setByX(-TRANSLATE_X);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition, translateTransition);

        parallelTransition.play();
    }

    public void setText(String text) {
        second.setText(text);
        animateOut(first);
        animateIn(second);
    }
}
