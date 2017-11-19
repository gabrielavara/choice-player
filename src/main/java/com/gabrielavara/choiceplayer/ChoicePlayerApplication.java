package com.gabrielavara.choiceplayer;

import com.gabrielavara.choiceplayer.views.ChoicePlayerSplashScreen;
import com.gabrielavara.choiceplayer.views.PlayerView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChoicePlayerApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launchApp(ChoicePlayerApplication.class, PlayerView.class, new ChoicePlayerSplashScreen(), args);
    }
}
