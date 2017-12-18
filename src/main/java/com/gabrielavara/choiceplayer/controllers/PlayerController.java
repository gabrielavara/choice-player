package com.gabrielavara.choiceplayer.controllers;

import static com.gabrielavara.choiceplayer.Constants.ICON_SIZE;
import static com.gabrielavara.choiceplayer.Constants.ICON_STYLE_CLASS;
import static com.gabrielavara.choiceplayer.Constants.SEEK_VOLUME;
import static de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon.PAUSE;
import static de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon.PLAY;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.messages.SelectionChangedMessage;
import com.gabrielavara.choiceplayer.messages.TableItemSelectedMessage;
import com.gabrielavara.choiceplayer.util.FileMover;
import com.gabrielavara.choiceplayer.util.GlobalKeyListener;
import com.gabrielavara.choiceplayer.util.GoodFolderFileMover;
import com.gabrielavara.choiceplayer.util.MediaUrl;
import com.gabrielavara.choiceplayer.util.Messenger;
import com.gabrielavara.choiceplayer.util.PlaylistInitializer;
import com.gabrielavara.choiceplayer.util.PlaylistUtil;
import com.gabrielavara.choiceplayer.util.RecycleBinFileMover;
import com.gabrielavara.choiceplayer.util.TimeFormatter;
import com.gabrielavara.choiceplayer.util.TimeSliderConverter;
import com.gabrielavara.choiceplayer.views.AnimatingLabel;
import com.gabrielavara.choiceplayer.views.Animator;
import com.gabrielavara.choiceplayer.views.FlippableImage;
import com.gabrielavara.choiceplayer.views.TableItem;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTreeTableView;
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

    private PlaylistUtil playlistUtil = new PlaylistUtil(mp3Files);

    private FileMover goodFolderFileMover = new GoodFolderFileMover(playlistUtil, mp3Files);
    private FileMover recycleBinFileMover = new RecycleBinFileMover(playlistUtil, mp3Files);

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

    private void selectionChanged(SelectionChangedMessage message) {
        Mp3 mp3 = message.getMp3();
        artist.setText(mp3.getArtist());
        title.setText(mp3.getTitle());
        timeSliderConverter.setLength(mp3.getLength());
        setAlbumArt();
        play(mp3);
    }

    private void selectTableItem(TableItemSelectedMessage message) {
        int index = message.getTableItem().getIndex().get() - 1;
        TreeTableView.TreeTableViewSelectionModel<TableItem> selectionModel = playlist.getSelectionModel();
        selectionModel.select(index);
        selectionModel.focus(index);
    }

    private void play(Mp3 mp3) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            waitForDispose();
        }

        Optional<String> mediaUrl = MediaUrl.create(mp3);
        if (mediaUrl.isPresent()) {
            Media media = new Media(mediaUrl.get());
            mediaPlayer = new MediaPlayer(media);
            addMediaPlayerListeners(mediaPlayer);
            mediaPlayer.play();
        }
    }

    private void waitForDispose() {
        while (!MediaPlayer.Status.DISPOSED.equals(mediaPlayer.statusProperty().get())) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                log.error("Sleep interrupted");
            }
        }
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
        iconView.setSize(ICON_SIZE);
        iconView.setStyleClass(ICON_STYLE_CLASS);
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
        likeButton.setOnMouseClicked(event -> goodFolderFileMover.moveFile());
        dislikeButton.setOnMouseClicked(event -> recycleBinFileMover.moveFile());
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
