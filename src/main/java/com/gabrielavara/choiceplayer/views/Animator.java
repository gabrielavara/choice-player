package com.gabrielavara.choiceplayer.views;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animator {
    private static final int DURATION = 700;
    private static final int DELAY = 100;
    private static final int TRANSLATE_Y = 50;

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
            node.setTranslateY(direction == Direction.IN ? TRANSLATE_Y : -TRANSLATE_Y);
        }
    }

    public Animator add(Node... nodes) {
        step++;
        for (Node node : nodes) {
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(DURATION), node);
            fadeTransition.setFromValue(direction == Direction.IN ? 0 : 1);
            fadeTransition.setToValue(direction == Direction.IN ? 1 : 0);
            fadeTransition.setDelay(Duration.millis(step * DELAY));

            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(DURATION), node);
            translateTransition.setByY(direction == Direction.IN ? -TRANSLATE_Y : TRANSLATE_Y);
            translateTransition.setDelay(Duration.millis(step * DELAY));

            parallelTransition.getChildren().addAll(fadeTransition, translateTransition);
        }
        return this;
    }

    public ParallelTransition build() {
        return parallelTransition;
    }
}
