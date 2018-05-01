package com.gabrielavara.choiceplayer.controls.growingbutton;

import com.jfoenix.controls.JFXButton;

public class GrowingButton extends JFXButton {

    private static final double SCALE = 1.1;

    public GrowingButton() {
        setOnMouseEntered(e -> setScale(SCALE));
        setOnMouseExited(e -> setScale(1));
    }

    private void setScale(double scale) {
        setScaleX(scale);
        setScaleY(scale);
    }

}
