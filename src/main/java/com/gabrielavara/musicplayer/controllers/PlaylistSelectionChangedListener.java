package com.gabrielavara.musicplayer.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.musicplayer.api.service.Mp3;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class PlaylistSelectionChangedListener implements ChangeListener<Mp3> {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.musicplayer.controllers.PlaylistSelectionChangedListener");
    private PlayerController playerController;

    PlaylistSelectionChangedListener(PlayerController playerController) {
        this.playerController = playerController;
    }

    @Override
    public void changed(ObservableValue<? extends Mp3> observable, Mp3 oldValue, Mp3 newValue) {
        log.info("Playlist selection changed from: {}, to {}", oldValue, newValue);
        newValue.setCurrentlyPlaying(true);
        playerController.getArtist().setText(newValue.getArtist());
        playerController.getTitle().setText(newValue.getTitle());
        if (oldValue != null) {
            oldValue.setCurrentlyPlaying(false);
        }
        play(newValue);
    }

    private void play(Mp3 newValue) {
        MediaPlayer mediaPlayer = playerController.getMediaPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        String mediaUrl = createMediaUrl(newValue);
        if (mediaUrl != null) {
            Media media = new Media(mediaUrl);
            playerController.setMediaPlayer(new MediaPlayer(media));
            mediaPlayer = playerController.getMediaPlayer();
            mediaPlayer.play();

            playerController.setAlbumArt();
        }
    }

    private String createMediaUrl(Mp3 newValue) {
        try {
            String path = Paths.get(newValue.getFilename()).toAbsolutePath().toString();
            String mediaUrl = URLEncoder.encode(path, "UTF-8");
            return "file:/" + mediaUrl.replace("\\", "/").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            log.error("Could not play mp3: {}", e.getMessage());
        }
        return null;
    }
}
