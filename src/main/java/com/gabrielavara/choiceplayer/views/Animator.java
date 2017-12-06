package com.gabrielavara.choiceplayer.views;

import com.gabrielavara.choiceplayer.Constants;

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
            node.setTranslateY(direction == Direction.IN ? Constants.TRANSLATE_Y : -Constants.TRANSLATE_Y);
        }
    }

    public Animator add(Node... nodes) {
        step++;
        for (Node node : nodes) {
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(Constants.ANIMATION_DURATION), node);
            fadeTransition.setFromValue(direction == Direction.IN ? 0 : 1);
            fadeTransition.setToValue(direction == Direction.IN ? 1 : 0);
            fadeTransition.setDelay(Duration.millis(step * Constants.DELAY));

            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(Constants.ANIMATION_DURATION), node);
            translateTransition.setByY(direction == Direction.IN ? -Constants.TRANSLATE_Y : Constants.TRANSLATE_Y);
            translateTransition.setDelay(Duration.millis(step * Constants.DELAY));

            parallelTransition.getChildren().addAll(fadeTransition, translateTransition);
        }
        return this;
    }

    public ParallelTransition build() {
        return parallelTransition;
    }
}
