package com.gabrielavara.choiceplayer.settings;

import lombok.Data;

@Data
public class Settings {
    private ThemeSettings theme = new ThemeSettings();
    private String folder = "src/test/resources/mp3folder";
}
