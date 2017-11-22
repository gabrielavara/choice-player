package com.gabrielavara.choiceplayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabrielavara.choiceplayer.settings.Settings;
import com.gabrielavara.choiceplayer.views.ChoicePlayerSplashScreen;
import com.gabrielavara.choiceplayer.views.PlayerView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import lombok.Getter;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class ChoicePlayerApplication extends AbstractJavaFxApplicationSupport {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.ChoicePlayerApplication");
    @Getter
    private static Settings settings;

    public static void main(String[] args) {
        settings = loadSettings();
        launchApp(ChoicePlayerApplication.class, PlayerView.class, new ChoicePlayerSplashScreen(), args);
    }

    private static Settings loadSettings() {
        try {
            return new ObjectMapper().readValue(new File("settings.json"), Settings.class);
        } catch (IOException e) {
            log.info("Could not load settings.json file");
            return new Settings();
        }
    }

    @Override
    public void stop() throws Exception {
        unregisterNativeHook();
        super.stop();
    }

    private static void unregisterNativeHook() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            log.error("Could not unregister native hook!", e.getMessage());
        }
    }
}
