package com.gabrielavara.choiceplayer.util;

import static com.gabrielavara.choiceplayer.Constants.STYLE_CSS;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.text.MessageFormat.format;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.settings.AccentColor;
import com.gabrielavara.choiceplayer.settings.Colors;
import com.gabrielavara.choiceplayer.settings.ThemeStyle;

import javafx.scene.layout.StackPane;

public class CssModifier {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.util.CssModifier");

    private CssModifier() {
    }

    public static void modify(StackPane rootContainer, ThemeStyle style, AccentColor accentColor) {
        Color backgroundColor = style.getBackgroundColor();
        Color backgroundBrightColor = style.getBackgroundBrightColor();

        try {
            Path path = Paths.get("src/main/resources/css/style.css");
            String content = new String(Files.readAllBytes(path));
            content = replaceAccentColor(content, accentColor);
            content = replaceBackgroundBrightColor(content, backgroundBrightColor);
            content = replaceBackgroundColor(content, backgroundColor);

            Files.write(Paths.get(STYLE_CSS), content.getBytes(), WRITE, CREATE, TRUNCATE_EXISTING);

            log.info("Change css");
            String css = new File(STYLE_CSS).toURI().toURL().toExternalForm();
            rootContainer.getStylesheets().clear();
            rootContainer.getStylesheets().add(css);
            ChoicePlayerApplication.setColors(new Colors(ChoicePlayerApplication.getSettings()));
        } catch (IOException e) {
            log.error("Could not modify style.css");
        }
    }

    private static String replaceAccentColor(String content, AccentColor accentColor) {
        return content.replaceAll("accent-color: rgb\\(29, 185, 84\\);",
                format("accent-color: rgb({0}, {1}, {2});", accentColor.getRed(),
                        accentColor.getGreen(), accentColor.getBlue()));
    }

    private static String replaceBackgroundBrightColor(String content, Color backgroundBrightColor) {
        return content.replaceAll("background-bright-color: rgb\\(30, 30, 30\\);",
                format("background-bright-color: rgb({0}, {1}, {2});",
                        backgroundBrightColor.getRed(), backgroundBrightColor.getGreen(),
                        backgroundBrightColor.getBlue()));
    }

    private static String replaceBackgroundColor(String content, Color backgroundColor) {
        return content.replaceAll("background-color: rgb\\(20, 20, 20\\);",
                format("background-color: rgb({0}, {1}, {2});",
                        backgroundColor.getRed(), backgroundColor.getGreen(),
                        backgroundColor.getBlue()));
    }
}
