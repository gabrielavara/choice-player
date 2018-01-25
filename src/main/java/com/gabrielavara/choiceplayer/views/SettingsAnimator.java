package com.gabrielavara.choiceplayer.views;

import static com.gabrielavara.choiceplayer.Constants.ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.SETTINGS_SCALE;
import static com.gabrielavara.choiceplayer.Constants.SETTINGS_TRANSLATE_Y;
import static com.gabrielavara.choiceplayer.controls.AnimationDirection.IN;
import static com.gabrielavara.choiceplayer.controls.AnimationDirection.OUT;
import static com.gabrielavara.choiceplayer.views.QuadraticInterpolator.QUADRATIC_EASE_IN;
import static com.gabrielavara.choiceplayer.views.QuadraticInterpolator.QUADRATIC_EASE_OUT;

import com.gabrielavara.choiceplayer.controls.AnimationDirection;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class SettingsAnimator {
    private Node mainContainer;
    private Node settings;

    public SettingsAnimator(Node mainContainer, Node settings) {
        this.mainContainer = mainContainer;
        this.settings = settings;
        this.settings.setOpacity(0);
        this.settings.setTranslateY(SETTINGS_TRANSLATE_Y);
    }

    public void animate(AnimationDirection direction) {
        animate(direction, null);
    }

    public void animate(AnimationDirection direction, SettingsAnimatorFinishedCallback callback) {
        ParallelTransition mainContainerTransition = getMainContainerTransition(mainContainer, direction.getInverse());
        ParallelTransition settingsTransition = getSettingsTransition(settings, direction);
        if (direction == IN) {
            mainContainerTransition.setOnFinished(e -> {
                if (callback != null) {
                    settingsTransition.setOnFinished(ev -> callback.finished());
                }
                settingsTransition.play();
            });
            mainContainerTransition.play();
        } else {
            settingsTransition.setOnFinished(e -> {
                if (callback != null) {
                    mainContainerTransition.setOnFinished(ev -> callback.finished());
                }
                mainContainerTransition.play();
            });
            settingsTransition.play();
        }
    }

    private ParallelTransition getMainContainerTransition(Node node, AnimationDirection animationDirection) {
        FadeTransition fadeTransition = getFadeTransition(node, animationDirection);
        ScaleTransition scaleXTransition = getScaleXTransition(node, animationDirection);
        ScaleTransition scaleYTransition = getScaleYTransition(node, animationDirection);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition, scaleXTransition, scaleYTransition);
        return parallelTransition;
    }

    private ParallelTransition getSettingsTransition(Node node, AnimationDirection animationDirection) {
        FadeTransition fadeTransition = getFadeTransition(node, animationDirection);
        TranslateTransition translateYTransition = getTranslateYTransition(node, animationDirection);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition, translateYTransition);
        return parallelTransition;
    }

    private FadeTransition getFadeTransition(Node node, AnimationDirection animationDirection) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(ANIMATION_DURATION), node);
        fadeTransition.setFromValue(node.getOpacity());
        fadeTransition.setToValue(animationDirection == OUT ? 0 : 1);
        fadeTransition.setInterpolator(animationDirection == IN ? QUADRATIC_EASE_IN : QUADRATIC_EASE_OUT);
        return fadeTransition;
    }

    private ScaleTransition getScaleXTransition(Node node, AnimationDirection animationDirection) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(ANIMATION_DURATION), node);
        scaleTransition.setFromX(node.getScaleX());
        scaleTransition.setToX(animationDirection == OUT ? SETTINGS_SCALE : 1);
        return scaleTransition;
    }

    private ScaleTransition getScaleYTransition(Node node, AnimationDirection animationDirection) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(ANIMATION_DURATION), node);
        scaleTransition.setFromY(node.getScaleY());
        scaleTransition.setToY(animationDirection == OUT ? SETTINGS_SCALE : 1);
        return scaleTransition;
    }

    private TranslateTransition getTranslateYTransition(Node node, AnimationDirection animationDirection) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(ANIMATION_DURATION), node);
        translateTransition.setFromY(node.getTranslateY());
        translateTransition.setToY(animationDirection == IN ? 0 : SETTINGS_TRANSLATE_Y);
        translateTransition.setInterpolator(animationDirection == IN ? QUADRATIC_EASE_OUT : QUADRATIC_EASE_IN);
        return translateTransition;
    }

    public interface SettingsAnimatorFinishedCallback {
        void finished();
    }
}
