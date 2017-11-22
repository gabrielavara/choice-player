package com.gabrielavara.choiceplayer.settings;

import lombok.Data;

import java.awt.*;

@Data
public class ThemeSettings {
    private ThemeStyle style = ThemeStyle.DARK;
    private boolean transparent = false;
    private Color accentColor = new Color(255, 185, 84);
}
