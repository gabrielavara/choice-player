package com.gabrielavara.choiceplayer.controllers;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.messages.SelectionChangedMessage;
import com.gabrielavara.choiceplayer.messages.TableItemSelectedMessage;
import com.gabrielavara.choiceplayer.util.GlobalKeyListener;
import com.gabrielavara.choiceplayer.util.Messenger;
import com.gabrielavara.choiceplayer.util.TimeFormatter;
import com.gabrielavara.choiceplayer.views.AnimatingLabel;
import com.gabrielavara.choiceplayer.views.Animator;
import com.gabrielavara.choiceplayer.views.FlippableImage;
import com.gabrielavara.choiceplayer.views.TableItem;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTreeTableView;
import com.sun.jna.platform.FileUtils;
import de.felixroske.jfxsupport.FXMLController;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lombok.Getter;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.gabrielavara.choiceplayer.Constants.SEEK_VOLUME;
import static de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon.PAUSE;
import static de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon.PLAY;

@Getter
@FXMLController
public class PlayerController implements Initializable {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controllers.PlayerController");

    @FXML
    public JFXSpinner spinner;
    @FXML
    public StackPane playlistStackPane;
    @FXML
    private HBox rootContainer;
    @FXML
    private StackPane albumArtStackPane;
    @FXML
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

    private MediaPlayer mediaPlayer;
    private boolean stopRequested;

    private FlippableImage flippableAlbumArt = new FlippableImage();
    private AnimatingLabel artist;
    private AnimatingLabel title;
    @Getter
    private ObservableList<TableItem> mp3Files = FXCollections.observableArrayList();

    private TimeSliderConverter timeSliderConverter = new TimeSliderConverter();

    private Duration duration;

    public PlaylistUtil playlistUtil = new PlaylistUtil(mp3Files);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addAlbumArt();
        setupAlbumAndTitleLabels();
        new PlaylistInitializer(playlist, mp3Files, rootContainer, spinner, playlistStackPane).loadPlaylist();
        timeSlider.setLabelFormatter(timeSliderConverter);
        setButtonListeners();
        animateItems();
        registerGlobalKeyListener();
        Messenger.register(SelectionChangedMessage.class, this::selectionChanged);
        Messenger.register(TableItemSelectedMessage.class, this::selectTableItem);
    }

    private void selectTableItem(TableItemSelectedMessage message) {
        int index = message.getTableItem().getIndex().get() - 1;
        TreeTableView.TreeTableViewSelectionModel<TableItem> selectionModel = playlist.getSelectionModel();
        selectionModel.select(index);
    }

    private void selectionChanged(SelectionChangedMessage message) {
        Mp3 mp3 = message.getMp3();
        artist.setText(mp3.getArtist());
        title.setText(mp3.getTitle());
        timeSliderConverter.setLength(mp3.getLength());
        setAlbumArt();
        play(mp3);
    }

    private void play(Mp3 newValue) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        String mediaUrl = createMediaUrl(newValue);
        if (mediaUrl != null) {
            Media media = new Media(mediaUrl);
            mediaPlayer = new MediaPlayer(media);
            addMediaPlayerListeners(mediaPlayer);
            mediaPlayer.play();
        }
    }

    private String createMediaUrl(Mp3 mp3) {
        try {
            String path = Paths.get(mp3.getFilename()).toAbsolutePath().toString();
            String mediaUrl = URLEncoder.encode(path, "UTF-8");
            return "file:/" + mediaUrl.replace("\\", "/").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            log.error("Could not play mp3: {}", e.getMessage());
        }
        return null;
    }

    private void addMediaPlayerListeners(MediaPlayer mediaPlayer) {
        mediaPlayer.currentTimeProperty().addListener(ov -> updateValues());

        mediaPlayer.setOnPlaying(() -> {
            if (stopRequested) {
                log.info("Pause requested");
                mediaPlayer.pause();
                stopRequested = false;
            } else {
                log.info("Play");
                playPauseButton.setGraphic(getIcon(PAUSE));
            }
        });

        mediaPlayer.setOnPaused(() -> {
            log.info("Paused");
            playPauseButton.setGraphic(getIcon(PLAY));
        });

        mediaPlayer.setOnReady(() -> {
            duration = mediaPlayer.getMedia().getDuration();
            updateValues();
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            log.info("Reached end of media");
            playPauseButton.setGraphic(getIcon(PLAY));
            playlistUtil.goToNextTrack();
        });
    }

    private MaterialDesignIconView getIcon(MaterialDesignIcon icon) {
        MaterialDesignIconView iconView = new MaterialDesignIconView(icon);
        iconView.setSize("56");
        iconView.setStyleClass("icon");
        return iconView;
    }

    private void updateValues() {
        Platform.runLater(() -> {
            Duration currentTime = updateElapsedRemainingLabels();
            updateTimeSlider(currentTime);
        });
    }

    private Duration updateElapsedRemainingLabels() {
        Duration currentTime = mediaPlayer.getCurrentTime();
        TimeFormatter.Times formattedTimes = TimeFormatter.getFormattedTimes(currentTime, duration);
        elapsedLabel.setText(formattedTimes.getElapsed());
        remainingLabel.setText(formattedTimes.getRemaining());
        return currentTime;
    }

    private void updateTimeSlider(Duration currentTime) {
        timeSlider.setDisable(duration.isUnknown());
        if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging()) {
            timeSlider.setValue(currentTime.divide(duration.toMillis()).toMillis() * 100.0);
        }
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
        previousTrackButton.setOnMouseClicked(event -> playlistUtil.goToPreviousTrack());
        nextTrackButton.setOnMouseClicked(event -> playlistUtil.goToNextTrack());
        playPauseButton.setOnMouseClicked(event -> playPause());
        timeSlider.setOnMouseClicked(event -> seek(false));
        timeSlider.valueProperty().addListener(ov -> seek(true));
        likeButton.setOnMouseClicked(event -> moveFileToGoodFolder());
        dislikeButton.setOnMouseClicked(event -> moveFileToRecycleBin());
    }

    public void playPause() {
        if (mediaPlayer == null) {
            playlistUtil.select(mp3Files.get(0));
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

    private void setAlbumArt() {
        Optional<byte[]> albumArtData = playlistUtil.getCurrentlyPlayingAlbumArt();
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
        playlistUtil.getCurrentlyPlayingTableItem().ifPresent(tableItem -> {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            playlistUtil.getNextTableItem().ifPresent(playlistUtil::select);
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
        playlistUtil.getCurrentlyPlayingTableItem().ifPresent(tableItem -> {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            FileUtils fileUtils = FileUtils.getInstance();
            playlistUtil.getNextTableItem().ifPresent(playlistUtil::select);
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
