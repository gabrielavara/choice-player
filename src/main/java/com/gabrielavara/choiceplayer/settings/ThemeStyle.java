package com.gabrielavara.choiceplayer.settings;

import java.awt.*;

import lombok.Getter;

@Getter
public enum ThemeStyle {
    LIGHT(new Color(220, 220, 220), new Color(230, 230, 230), new Color(80, 80, 80), new Color(0, 0, 0), new Color(255, 255, 255), 10, 15),
    DARK(new Color(20, 20, 20), new Color(30, 30, 30), new Color(160, 160, 160), new Color(230, 230, 230), new Color(0, 0, 0), 20, 25);

    private Color backgroundColor;
    private Color backgroundBrightColor;
    private Color opaqueBackgroundColor;
    private Color opaqueBackgroundBrightColor;
    private Color foregroundColor;
    private Color foregroundBrightColor;
    private Color absoluteColor;

    ThemeStyle(Color backgroundColor, Color backgroundBrightColor, Color foregroundColor, Color foregroundBrightColor, Color absoluteColor, int opaqueBackgroundOpacity, int opaqueBackgroundBrightOpacity) {
        this.backgroundColor = backgroundColor;
        this.backgroundBrightColor = backgroundBrightColor;
        this.foregroundColor = foregroundColor;
        this.foregroundBrightColor = foregroundBrightColor;
        this.absoluteColor = absoluteColor;
        opaqueBackgroundColor = new Color(20, 20, 20, opaqueBackgroundOpacity);
        opaqueBackgroundBrightColor = new Color(30, 30, 30, opaqueBackgroundBrightOpacity);
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
