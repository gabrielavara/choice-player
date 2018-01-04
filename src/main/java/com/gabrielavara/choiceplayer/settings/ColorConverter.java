package com.gabrielavara.choiceplayer.settings;

import javafx.scene.paint.Color;

public class ColorConverter {
    private ColorConverter() {
    }

    public static Color convert(java.awt.Color c) {
        return new Color(((double) c.getRed()) / 255, ((double) c.getGreen()) / 255, ((double) c.getBlue()) / 255, 1.0);
    }

    public static Color convert(AccentColor c) {
        return new Color(((double) c.getRed()) / 255, ((double) c.getGreen()) / 255, ((double) c.getBlue()) / 255, 1.0);
    }

    public static AccentColor convert(Color c) {
        return new AccentColor((int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255));
    }
}
