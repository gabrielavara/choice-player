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
import com.gabrielavara.choiceplayer.Constants;
import com.gabrielavara.choiceplayer.settings.AccentColor;
import com.gabrielavara.choiceplayer.settings.Colors;
import com.gabrielavara.choiceplayer.settings.Settings;

import javafx.scene.layout.StackPane;

public class CssModifier {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.util.CssModifier");

    private CssModifier() {
    }

    public static void modify(StackPane rootContainer) {
        try {
            Settings settings = ChoicePlayerApplication.getSettings();
            Path path = Paths.get("src/main/resources/css/style.css");
            String content = new String(Files.readAllBytes(path));
            content = replaceAccentColor(content, settings.getTheme().getAccentColor());
            content = replaceAccentBrightColor(content, settings.getTheme().getAccentBrightColor());
            content = replaceBackgroundBrightColor(content, settings.getTheme().getStyle().getBackgroundBrightColor());
            content = replaceBackgroundColor(content, settings.getTheme().getStyle().getBackgroundColor());
            content = replaceForegroundBrightColor(content, settings.getTheme().getStyle().getForegroundBrightColor());
            content = replaceForegroundColor(content, settings.getTheme().getStyle().getForegroundColor());

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
        return content.replace("<accent-color-placeholder>",
                        format(Constants.COLOR_PATTERN, accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue()));
    }

    private static String replaceAccentBrightColor(String content, AccentColor accentBrightColor) {
        return content.replace("<accent-bright-color-placeholder>",
                        format(Constants.COLOR_PATTERN, accentBrightColor.getRed(), accentBrightColor.getGreen(), accentBrightColor.getBlue()));
    }

    private static String replaceBackgroundBrightColor(String content, Color backgroundBrightColor) {
        return content.replace("<background-bright-color-placeholder>",
                        format(Constants.COLOR_PATTERN, backgroundBrightColor.getRed(), backgroundBrightColor.getGreen(), backgroundBrightColor.getBlue()));
    }

    private static String replaceBackgroundColor(String content, Color backgroundColor) {
        return content.replaceAll("<background-color-placeholder>",
                        format(Constants.COLOR_PATTERN, backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue()));
    }

    private static String replaceForegroundColor(String content, Color foregroundColor) {
        return content.replaceAll("<foreground-color-placeholder>",
                        format(Constants.COLOR_PATTERN, foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue()));
    }

    private static String replaceForegroundBrightColor(String content, Color foregroundBrightColor) {
        return content.replaceAll("<foreground-bright-color-placeholder>",
                        format(Constants.COLOR_PATTERN, foregroundBrightColor.getRed(), foregroundBrightColor.getGreen(), foregroundBrightColor.getBlue()));
    }
}
