package com.gabrielavara.choiceplayer.util;

import static com.gabrielavara.choiceplayer.Constants.COLOR_PATTERN;
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
import com.gabrielavara.choiceplayer.settings.Settings;

import javafx.scene.layout.Pane;

public class CssModifier {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.util.CssModifier");

    private CssModifier() {
    }

    public static void modify(Pane rootContainer) {
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

    private static String replaceAccentColor(String content, AccentColor color) {
        return replace(content, color, "<accent-color-placeholder>");
    }

    private static String replaceAccentBrightColor(String content, AccentColor color) {
        return replace(content, color, "<accent-bright-color-placeholder>");
    }

    private static String replaceBackgroundBrightColor(String content, Color color) {
        return replace(content, color, "<background-bright-color-placeholder>");
    }

    private static String replaceBackgroundColor(String content, Color color) {
        return replace(content, color, "<background-color-placeholder>");
    }

    private static String replaceForegroundColor(String content, Color color) {
        return replace(content, color, "<foreground-color-placeholder>");
    }

    private static String replaceForegroundBrightColor(String content, Color color) {
        return replace(content, color, "<foreground-bright-color-placeholder>");
    }

    private static String replace(String content, Color color, String s) {
        return content.replaceAll(s, format(COLOR_PATTERN, color.getRed(), color.getGreen(), color.getBlue()));
    }

    private static String replace(String content, AccentColor color, String s) {
        return content.replaceAll(s, format(COLOR_PATTERN, color.getRed(), color.getGreen(), color.getBlue()));
    }
}
