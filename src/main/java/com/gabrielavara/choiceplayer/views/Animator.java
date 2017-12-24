package com.gabrielavara.choiceplayer.views;

import static com.gabrielavara.choiceplayer.Constants.DELAY;
import static com.gabrielavara.choiceplayer.Constants.LONG_ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.TRANSLATE_Y;
import static com.gabrielavara.choiceplayer.views.Animator.Direction.IN;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animator {

    private Direction direction;
    private ParallelTransition parallelTransition = new ParallelTransition();
    private int step = 6;

    public enum Direction {
        IN, OUT
    }

    public Animator(Direction direction) {
        this.direction = direction;
    }

    public void setup(Node... nodes) {
        for (Node node : nodes) {
            node.setOpacity(0);
            node.setTranslateY(direction == IN ? TRANSLATE_Y : -TRANSLATE_Y);
        }
    }

    public Animator add(Node... nodes) {
        step++;
        for (Node node : nodes) {
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(LONG_ANIMATION_DURATION), node);
            fadeTransition.setFromValue(direction == IN ? 0 : 1);
            fadeTransition.setToValue(direction == IN ? 1 : 0);
            fadeTransition.setDelay(Duration.millis(step * DELAY));

            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(LONG_ANIMATION_DURATION), node);
            translateTransition.setByY(direction == IN ? -TRANSLATE_Y : TRANSLATE_Y);
            translateTransition.setDelay(Duration.millis(step * DELAY));

            parallelTransition.getChildren().addAll(fadeTransition, translateTransition);
        }
        return this;
    }

    public ParallelTransition build() {
        return parallelTransition;
    }
}
