package com.gabrielavara.choiceplayer.views;

import static com.gabrielavara.choiceplayer.Constants.DELAY;
import static com.gabrielavara.choiceplayer.Constants.INITIAL_ANIMATION_TRANSLATE_Y;
import static com.gabrielavara.choiceplayer.Constants.LONG_ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.WAIT_TILL_ANIMATING_ITEMS;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class InitialAnimator {
    private ParallelTransition parallelTransition = new ParallelTransition();
    private int step = 0;

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
            fadeTransition.setFromValue(node.getOpacity());
            fadeTransition.setToValue(1);
            fadeTransition.setDelay(Duration.millis(WAIT_TILL_ANIMATING_ITEMS + step * DELAY));

            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(LONG_ANIMATION_DURATION), node);
            translateTransition.setByY(-INITIAL_ANIMATION_TRANSLATE_Y);
            translateTransition.setDelay(Duration.millis(WAIT_TILL_ANIMATING_ITEMS + step * DELAY));

            parallelTransition.getChildren().addAll(fadeTransition, translateTransition);
        }
        return this;
    }

    public ParallelTransition build() {
        return parallelTransition;
    }
}
