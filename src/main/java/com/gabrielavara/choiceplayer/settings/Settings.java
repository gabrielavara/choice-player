package com.gabrielavara.choiceplayer.settings;

import lombok.Data;

@Data
public class Settings {
    private ThemeSettings theme = new ThemeSettings();
    private String folder = "C:\\Music";
    private String likedFolder = "C:\\Music\\Liked";
    private boolean showToast = true;
}
