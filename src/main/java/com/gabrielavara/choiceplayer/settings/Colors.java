package com.gabrielavara.choiceplayer.settings;

import javafx.scene.paint.Color;
import lombok.Getter;

@Getter
public class Colors {
    private final Color accentColor;
    private final Color foregroundBrightColor;
    private final Color foregroundColor;
    private final Color absoluteColor;
    private final Color errorColor = new Color(1, 0, 0, 1);

    public Colors(Settings settings) {
        accentColor = ColorConverter.convert(settings.getTheme().getAccentColor());
        foregroundBrightColor = ColorConverter.convert(settings.getTheme().getStyle().getForegroundBrightColor());
        foregroundColor = ColorConverter.convert(settings.getTheme().getStyle().getForegroundColor());
        absoluteColor = ColorConverter.convert(settings.getTheme().getStyle().getAbsoluteColor());
    }
}
