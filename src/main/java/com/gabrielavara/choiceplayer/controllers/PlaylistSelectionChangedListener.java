package com.gabrielavara.choiceplayer.controllers;

import static de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon.PAUSE;
import static de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon.PLAY;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.util.TimeFormatter;
import com.gabrielavara.choiceplayer.views.TableItem;
import com.jfoenix.controls.JFXSlider;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class PlaylistSelectionChangedListener implements ListChangeListener<TableItem> {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controllers.PlaylistSelectionChangedListener");
    private PlayerController playerController;

    PlaylistSelectionChangedListener(PlayerController playerController) {
        this.playerController = playerController;
    }

    @Override
    public void onChanged(Change<? extends TableItem> c) {
        changed(c.getRemovedSize() > 0 ? c.getRemoved().get(0).getMp3() : null, c.getAddedSize() > 0 ? c.getAddedSubList().get(0).getMp3() : null);
    }

    private void changed(Mp3 oldValue, Mp3 newValue) {
        if (newValue == null) {
            return;
        }

        log.info("Playlist selection changed from: {}, to {}", oldValue, newValue);
        newValue.setCurrentlyPlaying(true);
        if (oldValue != null) {
            oldValue.setCurrentlyPlaying(false);
        }
        playerController.getArtist().setText(newValue.getArtist());
        playerController.getTitle().setText(newValue.getTitle());
        playerController.getTimeSliderConverter().setLength(newValue.getLength());

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

            addMediaPlayerListeners(mediaPlayer);

            mediaPlayer.play();
            playerController.setAlbumArt();
        }
    }

    private void addMediaPlayerListeners(MediaPlayer mediaPlayer) {
        mediaPlayer.currentTimeProperty().addListener(ov -> updateValues());

        mediaPlayer.setOnPlaying(() -> {
            if (playerController.isStopRequested()) {
                log.info("Pause requested");
                mediaPlayer.pause();
                playerController.setStopRequested(false);
            } else {
                log.info("Play");
                playerController.getPlayPauseButton().setGraphic(getIcon(PAUSE));
            }
        });

        mediaPlayer.setOnPaused(() -> {
            log.info("Paused");
            playerController.getPlayPauseButton().setGraphic(getIcon(PLAY));
        });

        mediaPlayer.setOnReady(() -> {
            playerController.setDuration(mediaPlayer.getMedia().getDuration());
            updateValues();
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            log.info("Reached end of media");
            playerController.getPlayPauseButton().setGraphic(getIcon(PLAY));
            playerController.goToNextTrack();
        });
    }

    private MaterialDesignIconView getIcon(MaterialDesignIcon icon) {
        MaterialDesignIconView iconView = new MaterialDesignIconView(icon);
        iconView.setSize("56");
        iconView.setStyleClass("icon");
        return iconView;
    }

    private void updateValues() {
        JFXSlider timeSlider = playerController.getTimeSlider();
        Label remainingLabel = playerController.getRemainingLabel();
        Label elapsedLabel = playerController.getElapsedLabel();
        Duration duration = playerController.getDuration();

        Platform.runLater(() -> {
            Duration currentTime = updateElapsedRemainingLabels(remainingLabel, elapsedLabel, duration);
            updateTimeSlider(timeSlider, duration, currentTime);
        });
    }

    private Duration updateElapsedRemainingLabels(Label remainingLabel, Label elapsedLabel, Duration duration) {
        Duration currentTime = playerController.getMediaPlayer().getCurrentTime();
        TimeFormatter.Times formattedTimes = TimeFormatter.getFormattedTimes(currentTime, duration);
        elapsedLabel.setText(formattedTimes.getElapsed());
        remainingLabel.setText(formattedTimes.getRemaining());
        return currentTime;
    }

    private void updateTimeSlider(JFXSlider timeSlider, Duration duration, Duration currentTime) {
        playerController.getTimeSlider().setDisable(duration.isUnknown());
        if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging()) {
            timeSlider.setValue(currentTime.divide(duration.toMillis()).toMillis() * 100.0);
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
