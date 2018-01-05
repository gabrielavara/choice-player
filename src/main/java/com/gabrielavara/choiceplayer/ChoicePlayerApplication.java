package com.gabrielavara.choiceplayer;

import java.io.File;
import java.io.IOException;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gabrielavara.choiceplayer.api.service.PlaylistCache;
import com.gabrielavara.choiceplayer.settings.Colors;
import com.gabrielavara.choiceplayer.settings.Settings;
import com.gabrielavara.choiceplayer.views.ChoicePlayerSplashScreen;
import com.gabrielavara.choiceplayer.views.PlayerView;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

@SpringBootApplication
public class ChoicePlayerApplication extends AbstractJavaFxApplicationSupport {
    private static final String SETTINGS_FILE = "settings.json";
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.ChoicePlayerApplication");

    @Getter
    private static Settings settings;
    @Getter
    @Setter
    private static Colors colors;
    private static ObservableList<PlaylistItemView> playlistItems;

    public static void main(String[] args) {
        settings = loadSettings();
        colors = new Colors(settings);
        launchApp(ChoicePlayerApplication.class, PlayerView.class, new ChoicePlayerSplashScreen(), args);
    }

    private static Settings loadSettings() {
        try {
            return new ObjectMapper().readValue(new File(SETTINGS_FILE), Settings.class);
        } catch (IOException e) {
            log.info("Could not load settings.json file");
            Settings settings = new Settings();
            saveSettings(settings);
            return settings;
        }
    }

    private static void saveSettings(Settings settings) {
        try {
            log.info("Save settings file: {}", settings);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File(SETTINGS_FILE), settings);
        } catch (IOException e) {
            log.info("Could not save settings.json file");
        }
    }

    @Override
    public void stop() throws Exception {
        log.info("Stop application");
        PlaylistCache.save(playlistItems);
        saveSettings(settings);
        unregisterNativeHook();
        super.stop();
        Platform.exit();
    }

    private static void unregisterNativeHook() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            log.error("Could not unregister native hook!", e.getMessage());
        }
    }

    public static void setPlaylistItems(ObservableList<PlaylistItemView> playlistItems) {
        ChoicePlayerApplication.playlistItems = playlistItems;
    }
}
