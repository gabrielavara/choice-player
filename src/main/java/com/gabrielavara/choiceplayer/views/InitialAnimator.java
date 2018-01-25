package com.gabrielavara.choiceplayer.views;

import static com.gabrielavara.choiceplayer.Constants.DELAY;
import static com.gabrielavara.choiceplayer.Constants.INITIAL_ANIMATION_TRANSLATE_Y;
import static com.gabrielavara.choiceplayer.Constants.LONG_ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.WAIT_TILL_ANIMATING_ITEMS;
import static com.gabrielavara.choiceplayer.views.QuadraticInterpolator.QUADRATIC_EASE_OUT;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class InitialAnimator {
    private ParallelTransition parallelTransition = new ParallelTransition();
    private int step = 0;
    private static final int ALBUM_ART_TRANSLATE = 425;
    private static final int PLAYLIST_TRANSLATE = 700;
    private static final int DELAY_BETWEEN_ITEMS = 400;

    public void setupAlbumArt(Node node) {
        node.setTranslateX(-ALBUM_ART_TRANSLATE);
        node.setOpacity(0);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(LONG_ANIMATION_DURATION), node);
        fadeTransition.setToValue(1);
        fadeTransition.setDelay(Duration.millis(WAIT_TILL_ANIMATING_ITEMS));
        fadeTransition.setInterpolator(QUADRATIC_EASE_OUT);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(LONG_ANIMATION_DURATION), node);
        translateTransition.setByX(ALBUM_ART_TRANSLATE);
        translateTransition.setDelay(Duration.millis(WAIT_TILL_ANIMATING_ITEMS));
        translateTransition.setInterpolator(QUADRATIC_EASE_OUT);

        parallelTransition.getChildren().addAll(translateTransition, fadeTransition);
    }

    public void setupPlaylist(Node node) {
        node.setTranslateX(PLAYLIST_TRANSLATE);
        node.setOpacity(0);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(LONG_ANIMATION_DURATION), node);
        fadeTransition.setToValue(1);
        fadeTransition.setDelay(Duration.millis(WAIT_TILL_ANIMATING_ITEMS + DELAY_BETWEEN_ITEMS));
        fadeTransition.setInterpolator(QUADRATIC_EASE_OUT);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(LONG_ANIMATION_DURATION), node);
        translateTransition.setByX(-PLAYLIST_TRANSLATE);
        translateTransition.setDelay(Duration.millis(WAIT_TILL_ANIMATING_ITEMS + DELAY_BETWEEN_ITEMS));
        translateTransition.setInterpolator(QUADRATIC_EASE_OUT);

        parallelTransition.getChildren().addAll(translateTransition, fadeTransition);
    }

    public void setup(Node... nodes) {
        for (Node node : nodes) {
            node.setOpacity(0);
            node.setTranslateY(INITIAL_ANIMATION_TRANSLATE_Y);
        }
    }

    public InitialAnimator add(Node... nodes) {
        step++;
        for (Node node : nodes) {
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(LONG_ANIMATION_DURATION), node);
            fadeTransition.setToValue(1);
            fadeTransition.setDelay(Duration.millis(WAIT_TILL_ANIMATING_ITEMS + DELAY_BETWEEN_ITEMS * 2 + step * DELAY));
            fadeTransition.setInterpolator(QUADRATIC_EASE_OUT);

            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(LONG_ANIMATION_DURATION), node);
            translateTransition.setByY(-INITIAL_ANIMATION_TRANSLATE_Y);
            translateTransition.setDelay(Duration.millis(WAIT_TILL_ANIMATING_ITEMS + DELAY_BETWEEN_ITEMS * 2 + step * DELAY));
            translateTransition.setInterpolator(QUADRATIC_EASE_OUT);

            parallelTransition.getChildren().addAll(fadeTransition, translateTransition);
        }
        return this;
    }

    public ParallelTransition build() {
        return parallelTransition;
    }
}
