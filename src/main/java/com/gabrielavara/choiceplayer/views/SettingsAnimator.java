package com.gabrielavara.choiceplayer.views;

import static com.gabrielavara.choiceplayer.Constants.LONG_ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.SETTINGS_TRANSLATE_X;
import static com.gabrielavara.choiceplayer.controls.AnimationDirection.IN;
import static com.gabrielavara.choiceplayer.controls.AnimationDirection.OUT;
import static com.gabrielavara.choiceplayer.views.QuadraticInterpolator.QUADRATIC_EASE_IN;
import static com.gabrielavara.choiceplayer.views.QuadraticInterpolator.QUADRATIC_EASE_OUT;

import com.gabrielavara.choiceplayer.controls.AnimationDirection;
import com.gabrielavara.choiceplayer.controls.overlay.Overlay;
import com.gabrielavara.choiceplayer.controls.settings.Settings;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class SettingsAnimator {
    private Node mainContainer;
    private Settings settings;
    private Overlay overlay;

    public SettingsAnimator(Node mainContainer, Settings settings, Overlay overlay) {
        this.mainContainer = mainContainer;
        this.settings = settings;
        this.overlay = overlay;
        this.settings.setOpacity(0);
        this.settings.setMouseTransparent(true);
        this.overlay.setOpacity(0);
        this.overlay.setTranslateX(SETTINGS_TRANSLATE_X);
    }

    public void animate(AnimationDirection direction) {
        animate(direction, null);
    }

    public void animate(AnimationDirection direction, SettingsAnimatorFinishedCallback callback) {
        animate(direction == IN ? mainContainer : settings, direction == IN ? settings : mainContainer, callback);
    }

    private void animate(Node toRemove, Node toAdd, SettingsAnimatorFinishedCallback callback) {
        overlay.setOpacity(1);
        toAdd.setMouseTransparent(true);

        ParallelTransition first = getFirstTransition(toRemove, toAdd);
        ParallelTransition second = getSecondTransition(toAdd);

        SequentialTransition sequentialTransition = new SequentialTransition(first, second);
        if (callback != null) {
            sequentialTransition.setOnFinished(e -> callback.finished());
        }
        sequentialTransition.play();
    }

    private ParallelTransition getFirstTransition(Node toRemove, Node toAdd) {
        TranslateTransition overlayFirstTransition = getOverlayFirstTranslateXTransition();
        FadeTransition fadeOutTransition = getFadeTransition(toRemove, OUT);
        ParallelTransition first = new ParallelTransition(overlayFirstTransition, fadeOutTransition);
        first.setOnFinished(e -> {
            toRemove.setOpacity(0);
            toRemove.setMouseTransparent(true);
            toAdd.setMouseTransparent(false);
        });
        return first;
    }

    private ParallelTransition getSecondTransition(Node toAdd) {
        TranslateTransition overlaySecondTransition = getOverlaySecondTranslateXTransition();
        FadeTransition fadeInTransition = getFadeTransition(toAdd, IN);
        return new ParallelTransition(overlaySecondTransition, fadeInTransition);
    }

    private FadeTransition getFadeTransition(Node node, AnimationDirection animationDirection) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(LONG_ANIMATION_DURATION), node);
        fadeTransition.setFromValue(node.getOpacity());
        fadeTransition.setToValue(animationDirection == OUT ? 0 : 1);
        fadeTransition.setInterpolator(animationDirection == IN ? QUADRATIC_EASE_IN : QUADRATIC_EASE_OUT);
        return fadeTransition;
    }

    private TranslateTransition getOverlayFirstTranslateXTransition() {
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(LONG_ANIMATION_DURATION), overlay);
        translateTransition.setFromX(-overlay.getWidth());
        translateTransition.setToX(0);
        return translateTransition;
    }

    private TranslateTransition getOverlaySecondTranslateXTransition() {
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(LONG_ANIMATION_DURATION), overlay);
        translateTransition.setFromX(0);
        translateTransition.setToX(overlay.getWidth());
        return translateTransition;
    }

    public interface SettingsAnimatorFinishedCallback {
        void finished();
    }
}
