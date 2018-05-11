package com.gabrielavara.choiceplayer.controls.rotatingbutton;

import static com.gabrielavara.choiceplayer.Constants.LONG_ANIMATION_DURATION;
import static javafx.animation.Interpolator.EASE_BOTH;

import com.gabrielavara.choiceplayer.controls.growingbutton.GrowingButton;

import javafx.animation.RotateTransition;
import javafx.util.Duration;

public class RotatingButton extends GrowingButton {
    private final RotateTransition rotateTransition;

    public RotatingButton() {
        rotateTransition = new RotateTransition(Duration.millis(LONG_ANIMATION_DURATION), this);
        rotateTransition.setByAngle(360 * 2);
        rotateTransition.setInterpolator(EASE_BOTH);
    }

    public void rotate() {
        rotateTransition.playFromStart();
    }
}
