package com.gabrielavara.choiceplayer.views;

import static java.text.MessageFormat.format;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.messages.ThemeChangedMessage;
import com.gabrielavara.choiceplayer.settings.AccentColor;
import com.gabrielavara.choiceplayer.settings.ThemeStyle;
import com.gabrielavara.choiceplayer.util.Messenger;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;

@FXMLView(value = "/fxml/player.fxml", bundle = "language.player", css = "/css/style.css")
public class PlayerView extends AbstractFxmlView {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.views.PlayerView");

    public PlayerView() {
        Messenger.register(ThemeChangedMessage.class, this::accessColorChanged);
    }

    private void accessColorChanged(ThemeChangedMessage m) {
        modifyCss(m.getStyle(), m.getAccentColor());
    }

    private void modifyCss(ThemeStyle style, AccentColor accentColor) {
        Color backgroundColor = style.getBackgroundColor();
        Color backgroundBrightColor = style.getBackgroundBrightColor();

        try {
            Path path = Paths.get("src/main/resources/css/style.css");
            String content = new String(Files.readAllBytes(path));
            content = replaceAccentColor(content, accentColor);
            content = replaceBackgroundBrightColor(content, backgroundBrightColor);
            content = replaceBackgroundColor(content, backgroundColor);
            Files.write(path, content.getBytes());

            log.info("Change css");
            String css = getClass().getResource("css/style.css").toExternalForm();
            ChoicePlayerApplication.getScene().getStylesheets().clear();
            ChoicePlayerApplication.getScene().getStylesheets().add(css);
        } catch (IOException e) {
            log.error("Could not modify style.css");
        }
    }

    private static String replaceAccentColor(String content, AccentColor accentColor) {
        return content.replaceAll("accent-color: rgb(29, 185, 84);",
                format("accent-color: rgba({0}, {1}, {2}, {3})", accentColor.getRed(),
                                        accentColor.getGreen(), accentColor.getBlue(), 1));
    }

    private String replaceBackgroundBrightColor(String content, Color backgroundBrightColor) {
        return content.replaceAll("background-bright-color: rgb(60, 60, 60);",
                format("background-bright-color: rgba({0}, {1}, {2}, {3});",
                        backgroundBrightColor.getRed(), backgroundBrightColor.getGreen(),
                                        backgroundBrightColor.getBlue(), 1));
    }

    private String replaceBackgroundColor(String content, Color backgroundColor) {
        return content.replaceAll("background-color: rgb(60, 60, 60);",
                format("background-color: rgba({0}, {1}, {2}, {3});",
                        backgroundColor.getRed(), backgroundColor.getGreen(),
                                        backgroundColor.getBlue(), 1));
    }
}
