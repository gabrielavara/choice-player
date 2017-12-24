package com.gabrielavara.choiceplayer.settings;

import java.awt.*;

import lombok.Getter;

@Getter
public enum ThemeStyle {
    LIGHT(new Color(200, 200, 200), new Color(230, 230, 230), new javafx.scene.paint.Color(1, 1, 1, 1)),
    DARK(new Color(40, 40, 40), new Color(60, 60, 60), new javafx.scene.paint.Color(0, 0, 0, 1));

    private Color backgroundColor;
    private Color backgroundBrightColor;
    private javafx.scene.paint.Color absoluteColor;

    ThemeStyle(Color backgroundColor, Color backgroundBrightColor, javafx.scene.paint.Color absoluteColor) {
        this.backgroundColor = backgroundColor;
        this.backgroundBrightColor = backgroundBrightColor;
        this.absoluteColor = absoluteColor;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
