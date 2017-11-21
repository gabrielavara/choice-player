package com.gabrielavara.choiceplayer;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.gabrielavara.choiceplayer.views.ChoicePlayerSplashScreen;
import com.gabrielavara.choiceplayer.views.PlayerView;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;

@SpringBootApplication
public class ChoicePlayerApplication extends AbstractJavaFxApplicationSupport {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.ChoicePlayerApplication");

    public static void main(String[] args) {
        launchApp(ChoicePlayerApplication.class, PlayerView.class, new ChoicePlayerSplashScreen(), args);
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
