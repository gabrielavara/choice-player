package com.gabrielavara.choiceplayer.settings;

public enum ThemeStyle {
    LIGHT, DARK;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
