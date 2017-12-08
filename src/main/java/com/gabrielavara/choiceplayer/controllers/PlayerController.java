package com.gabrielavara.choiceplayer.controllers;

import static com.gabrielavara.choiceplayer.Constants.SEEK_VOLUME;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.api.service.MusicService;
import com.gabrielavara.choiceplayer.util.GlobalKeyListener;
import com.gabrielavara.choiceplayer.views.AnimatingLabel;
import com.gabrielavara.choiceplayer.views.Animator;
import com.gabrielavara.choiceplayer.views.FlippableImage;
import com.gabrielavara.choiceplayer.views.TableItem;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTreeTableView;
import com.sun.jna.platform.FileUtils;

import de.felixroske.jfxsupport.FXMLController;
import javafx.animation.ParallelTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

@Getter
@FXMLController
public class PlayerController implements Initializable {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controllers.PlayerController");

    @FXML
    private HBox rootContainer;
    @FXML
    private StackPane albumArtStackPane;
    @FXML
    @Setter
    private JFXTreeTableView<TableItem> playlist;
    @FXML
    private VBox currentlyPlayingBox;
    @FXML
    private JFXButton likeButton;
    @FXML
    private JFXButton dislikeButton;
    @FXML
    private JFXButton previousTrackButton;
    @FXML
    private JFXButton nextTrackButton;
    @FXML
    private JFXButton playPauseButton;
    @FXML
    private JFXSlider timeSlider;
    @FXML
    private Label elapsedLabel;
    @FXML
    private Label remainingLabel;

    @Autowired
    private MusicService musicService;

    @Setter
    private MediaPlayer mediaPlayer;
    @Setter
    private boolean stopRequested;

    private FlippableImage flippableAlbumArt = new FlippableImage();
    private AnimatingLabel artist;
    private AnimatingLabel title;
    @Getter
    private ObservableList<TableItem> mp3Files = FXCollections.observableArrayList();

    @Getter
    private TimeSliderConverter timeSliderConverter = new TimeSliderConverter();

    @Setter
    private Duration duration;

    @Getter
    public PlaylistChanger playlistChanger = new PlaylistChanger(playlist, mp3Files);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addAlbumArt();
        setupAlbumAndTitleLabels();

        new PlaylistInitializer(playlist, mp3Files, rootContainer).loadPlaylist();

        timeSlider.setLabelFormatter(timeSliderConverter);

        setButtonListeners();
        animateItems();
        registerGlobalKeyListener();
    }

    private void addAlbumArt() {
        flippableAlbumArt = new FlippableImage();
        albumArtStackPane.getChildren().add(flippableAlbumArt);
    }

    private void setupAlbumAndTitleLabels() {
        artist = new AnimatingLabel("artist-label");
        title = new AnimatingLabel("title-label");
        VBox.setMargin(artist, new Insets(6, 24, 6, 24));
        VBox.setMargin(title, new Insets(6, 24, 6, 24));
        currentlyPlayingBox.getChildren().add(1, artist);
        currentlyPlayingBox.getChildren().add(2, title);
    }

    private void animateItems() {
        Animator animator = new Animator(Animator.Direction.IN);
        animator.setup(albumArtStackPane, artist, title, timeSlider, elapsedLabel, remainingLabel, dislikeButton, previousTrackButton,
                playPauseButton, nextTrackButton, likeButton);
        animator.add(albumArtStackPane).add(artist).add(title).add(timeSlider).add(elapsedLabel, remainingLabel)
                .add(dislikeButton)
                .add(previousTrackButton)
                .add(playPauseButton)
                .add(nextTrackButton)
                .add(likeButton);
        ParallelTransition transition = animator.build();
        transition.play();
    }

    private void setButtonListeners() {
        previousTrackButton.setOnMouseClicked(event -> playlistChanger.goToPreviousTrack());
        nextTrackButton.setOnMouseClicked(event -> playlistChanger.goToNextTrack());
        playPauseButton.setOnMouseClicked(event -> playPause());
        timeSlider.setOnMouseClicked(event -> seek(false));
        timeSlider.valueProperty().addListener(ov -> seek(true));
        likeButton.setOnMouseClicked(event -> moveFileToGoodFolder());
        dislikeButton.setOnMouseClicked(event -> moveFileToRecycleBin());
    }

    public void playPause() {
        if (mediaPlayer == null) {
            playlistChanger.select(mp3Files.get(0));
        }
        MediaPlayer.Status status = mediaPlayer.getStatus();
        if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
            return;
        }
        if (status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.READY
                || status == MediaPlayer.Status.STOPPED) {
            mediaPlayer.play();
        } else {
            mediaPlayer.pause();
        }
    }

    private void seek(boolean shouldConsiderValueChanging) {
        if (mediaPlayer == null) {
            return;
        }
        if (!shouldConsiderValueChanging || timeSlider.isValueChanging()) {
            mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
        }
    }

    void setAlbumArt() {
        Optional<byte[]> albumArtData = musicService.getCurrentlyPlayingAlbumArt();
        if (albumArtData.isPresent()) {
            setExistingAlbumArt(albumArtData.get());
        } else {
            flippableAlbumArt.setImage(flippableAlbumArt.getDefaultImage());
        }
    }

    private void setExistingAlbumArt(byte[] albumArtData) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(albumArtData);
        Image image = new Image(inputStream);
        flippableAlbumArt.setImage(image);
    }

    private void registerGlobalKeyListener() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            log.error("There was a problem registering the native hook.", ex);
        }
        GlobalScreen.addNativeKeyListener(new GlobalKeyListener(this));
    }

    public void moveFileToGoodFolder() {
        log.info("Move file to good folder");
        playlistChanger.getCurrentlyPlayingTableItem().ifPresent(tableItem -> {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            playlistChanger.getNextTableItem().ifPresent(playlistChanger::select);
            try {
                mp3Files.removeAll(tableItem);
                Path from = Paths.get(tableItem.getMp3().getFilename());
                String folderToMove = ChoicePlayerApplication.getSettings().getFolderToMove();
                String fileName = from.getFileName().toString();
                Path to = Paths.get(folderToMove, fileName);
                Files.move(from, to);
            } catch (IOException e) {
                mp3Files.add(tableItem.getIndex().get() - 1, tableItem);
                log.error("Could not move {}", tableItem.getMp3());
                sortPlaylist();
            }
        });
    }

    public void moveFileToRecycleBin() {
        log.info("Move file to recycle bin");
        playlistChanger.getCurrentlyPlayingTableItem().ifPresent(tableItem -> {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            FileUtils fileUtils = FileUtils.getInstance();
            playlistChanger.getNextTableItem().ifPresent(playlistChanger::select);
            try {
                mp3Files.removeAll(tableItem);
                fileUtils.moveToTrash(new File[]{new File(tableItem.getMp3().getFilename())});
            } catch (IOException e) {
                mp3Files.add(tableItem.getIndex().get() - 1, tableItem);
                log.error("Could not delete {}", tableItem.getMp3());
                sortPlaylist();
            }
        });
    }

    private void sortPlaylist() {
        mp3Files.sort(Comparator.comparingInt(o -> o.getIndex().get()));
    }

    public void rewind() {
        log.info("Rewind");
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.setVolume(SEEK_VOLUME);
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(-5)));
        mediaPlayer.setVolume(1.0);
    }

    public void fastForward() {
        log.info("Fast forward");
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.setVolume(SEEK_VOLUME);
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(5)));
        mediaPlayer.setVolume(1.0);
    }
}
