package com.gabrielavara.choiceplayer.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class ThemeSettings {
    private ThemeStyle style = ThemeStyle.DARK;
    private AccentColor accentColor = new AccentColor(29, 185, 84);
    @JsonIgnore
    private AccentColor accentBrightColor = ColorConverter.convert(ColorConverter.convert(accentColor).brighter());

    public void setAccentColor(AccentColor accentColor) {
        this.accentColor = accentColor;
        accentBrightColor = ColorConverter.convert(ColorConverter.convert(accentColor).brighter());
    }
}
