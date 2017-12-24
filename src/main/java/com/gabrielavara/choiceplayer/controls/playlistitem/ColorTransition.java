package com.gabrielavara.choiceplayer.controls.playlistitem;

import static com.gabrielavara.choiceplayer.Constants.SHORT_ANIMATION_DURATION;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ColorTransition extends Transition {
    private Label label;
    private Color from;
    private Color to;

    ColorTransition(Label label, Color from, Color to) {
        this.label = label;
        this.from = from;
        this.to = to;
        setCycleDuration(Duration.millis(SHORT_ANIMATION_DURATION));
        setInterpolator(Interpolator.EASE_OUT);
    }

    @Override
    protected void interpolate(double fracture) {
        double rf = (to.getRed() - from.getRed()) * fracture;
        double gf = (to.getGreen() - from.getGreen()) * fracture;
        double bf = (to.getBlue() - from.getBlue()) * fracture;
        Color vColor = new Color(from.getRed() + rf, from.getGreen() + gf, from.getBlue() + bf, 1);
        label.setTextFill(vColor);
    }
}
