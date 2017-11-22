package com.gabrielavara.choiceplayer.settings;

import java.awt.*;

import lombok.Data;

@Data
public class ThemeSettings {
    private ThemeStyle style = ThemeStyle.DARK;
    private boolean transparent;
    private Color accentColor = new Color(255, 185, 84);
}
