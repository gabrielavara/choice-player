package com.gabrielavara.choiceplayer.views;

import static com.gabrielavara.choiceplayer.Constants.SHORT_ANIMATION_DURATION;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Control;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ButtonBox {
    private final HBox buttonHBox;
    private final Control playlist;

    public void initialize() {
        buttonHBox.setOpacity(0);
        buttonHBox.setTranslateY(buttonHBox.getHeight());

        playlist.setOnMouseEntered(e -> getButtonTransition(true).play());
        playlist.setOnMouseExited(e -> {
            if (e.getY() >= playlist.getHeight() || e.getY() <= 0 || e.getX() >= playlist.getWidth() || e.getX() <= 0) {
                getButtonTransition(false).play();
            }
        });
    }

    private ParallelTransition getButtonTransition(boolean in) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(SHORT_ANIMATION_DURATION), buttonHBox);
        fadeTransition.setToValue(in ? 1 : 0);
        fadeTransition.setInterpolator(QuadraticInterpolator.QUADRATIC_EASE_OUT);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(SHORT_ANIMATION_DURATION), buttonHBox);
        translateTransition.setFromY(buttonHBox.getTranslateY());
        translateTransition.setToY(in ? 0 : buttonHBox.getHeight());
        translateTransition.setInterpolator(QuadraticInterpolator.QUADRATIC_EASE_OUT);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition, translateTransition);
        return parallelTransition;
    }
}
