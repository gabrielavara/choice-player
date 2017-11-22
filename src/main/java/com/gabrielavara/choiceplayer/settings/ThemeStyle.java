package com.gabrielavara.choiceplayer.settings;

import lombok.Getter;

import java.awt.*;

@Getter
public enum ThemeStyle {
    LIGHT(new Color(230, 230, 230), new Color(255, 255, 255)),
    DARK(new Color(40, 40, 40), new Color(60, 60, 60));

    private Color backgroundColor;
    private Color backgroundBrightColor;

    private ThemeStyle(Color backgroundColor, Color backgroundBrightColor) {
        this.backgroundColor = backgroundColor;
        this.backgroundBrightColor = backgroundBrightColor;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
