package com.gabrielavara.choiceplayer.settings;

import lombok.Data;

@Data
public class Settings {
    private ThemeSettings theme = new ThemeSettings();
    private String folder = "F:\\Google Drive\\Music\\Downloaded";
    private String folderToMove = "F:\\Music\\0 New";
}
