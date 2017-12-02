package com.gabrielavara.choiceplayer.views;

import com.gabrielavara.choiceplayer.settings.AccentColor;
import com.gabrielavara.choiceplayer.settings.ThemeSettings;
import com.gabrielavara.choiceplayer.settings.ThemeStyle;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.text.MessageFormat.format;

@FXMLView(value = "/fxml/player.fxml", bundle = "language.player", css = "/css/style.css")
public class PlayerView extends AbstractFxmlView {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.views.PlayerView");

    private void modifyCss(ThemeSettings theme) {
        AccentColor accentColor = theme.getAccentColor();
        boolean transparent = theme.isTransparent();
        ThemeStyle style = theme.getStyle();
        Color backgroundColor = style.getBackgroundColor();
        Color backgroundBrightColor = style.getBackgroundBrightColor();

        try {
            Path path = Paths.get("src/main/resources/css/style.css");
            String content = new String(Files.readAllBytes(path));
            content = replaceAccentColor(content, accentColor, transparent);
            content = replaceBackgroundBrightColor(content, backgroundBrightColor, transparent);
            content = replaceBackgroundColor(content, backgroundColor, transparent);
            Files.write(path, content.getBytes());
        } catch (IOException e) {
            log.error("Could not modify style.css");
        }
    }

    private static String replaceAccentColor(String content, AccentColor accentColor, boolean transparent) {
        return content.replaceAll("accent-color: rgb(29, 185, 84);",
                format("accent-color: rgba({0}, {1}, {2}, {3})", accentColor.getRed(),
                        accentColor.getGreen(), accentColor.getBlue(), opacity(transparent)));
    }

    private String replaceBackgroundBrightColor(String content, Color backgroundBrightColor, boolean transparent) {
        return content.replaceAll("background-bright-color: rgb(60, 60, 60);",
                format("background-bright-color: rgba({0}, {1}, {2}, {3});",
                        backgroundBrightColor.getRed(), backgroundBrightColor.getGreen(),
                        backgroundBrightColor.getBlue(), opacity(transparent)));
    }

    private String replaceBackgroundColor(String content, Color backgroundColor, boolean transparent) {
        return content.replaceAll("background-color: rgb(60, 60, 60);",
                format("background-color: rgba({0}, {1}, {2}, {3});",
                        backgroundColor.getRed(), backgroundColor.getGreen(),
                        backgroundColor.getBlue(), opacity(transparent)));
    }

    private static double opacity(boolean transparent) {
        return transparent ? 0.6 : 1.0;
    }
}
