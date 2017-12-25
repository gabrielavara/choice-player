package com.gabrielavara.choiceplayer.controls.playlistitem;

import static com.gabrielavara.choiceplayer.Constants.ANIMATION_DURATION;

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
        setCycleDuration(Duration.millis(ANIMATION_DURATION));
    }

    @Override
    protected void interpolate(double fracture) {
        Color color = from.interpolate(to, fracture);
        label.setTextFill(color);
    }
}
