package com.gabrielavara.choiceplayer.settings;

import lombok.Data;

@Data
public class ThemeSettings {
    private ThemeStyle style = ThemeStyle.DARK;
    private boolean transparent = false;
    private AccentColor accentColor = new AccentColor(255, 185, 84);
}
