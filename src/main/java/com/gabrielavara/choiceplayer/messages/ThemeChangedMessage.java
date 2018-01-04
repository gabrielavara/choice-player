package com.gabrielavara.choiceplayer.messages;

import com.gabrielavara.choiceplayer.settings.AccentColor;
import com.gabrielavara.choiceplayer.settings.ThemeStyle;

import lombok.Getter;

@Getter
public class ThemeChangedMessage {
    private ThemeStyle style;
    private AccentColor accentColor;

    public ThemeChangedMessage(ThemeStyle style, AccentColor accentColor) {
        this.style = style;
        this.accentColor = accentColor;
    }
}
