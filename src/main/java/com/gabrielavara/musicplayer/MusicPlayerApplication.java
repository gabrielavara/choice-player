package com.gabrielavara.musicplayer;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.gabrielavara.musicplayer.views.HelloWorldView;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;

@SpringBootApplication
public class MusicPlayerApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launchApp(MusicPlayerApplication.class, HelloWorldView.class, args);
    }
}
