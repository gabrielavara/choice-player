package com.gabrielavara.choiceplayer.controllers;

import static com.gabrielavara.choiceplayer.Constants.ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.DISPOSE_MAX_WAIT_S;
import static com.gabrielavara.choiceplayer.Constants.DISPOSE_WAIT_MS;
import static com.gabrielavara.choiceplayer.Constants.SEEK_SECONDS;
import static com.gabrielavara.choiceplayer.Constants.SHORT_ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.controls.playlistitem.PlaylistItemState.DESELECTED;
import static com.gabrielavara.choiceplayer.controls.playlistitem.PlaylistItemState.SELECTED;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static javafx.scene.media.MediaPlayer.Status.DISPOSED;
import static javafx.scene.media.MediaPlayer.Status.HALTED;
import static javafx.scene.media.MediaPlayer.Status.PAUSED;
import static javafx.scene.media.MediaPlayer.Status.READY;
import static javafx.scene.media.MediaPlayer.Status.STOPPED;
import static javafx.scene.media.MediaPlayer.Status.UNKNOWN;
import static org.hamcrest.CoreMatchers.equalTo;

import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.controls.AnimatedLabel;
import com.gabrielavara.choiceplayer.controls.FlippableImage;
import com.gabrielavara.choiceplayer.controls.animatedbutton.AnimatedButton;
import com.gabrielavara.choiceplayer.messages.PlaylistItemSelectedMessage;
import com.gabrielavara.choiceplayer.messages.SelectionChangedMessage;
import com.gabrielavara.choiceplayer.util.FileMover;
import com.gabrielavara.choiceplayer.util.GlobalKeyListener;
import com.gabrielavara.choiceplayer.util.GoodFolderFileMover;
import com.gabrielavara.choiceplayer.util.ImageUtil;
import com.gabrielavara.choiceplayer.util.MediaUrl;
import com.gabrielavara.choiceplayer.util.Messenger;
import com.gabrielavara.choiceplayer.util.PlaylistInitializer;
import com.gabrielavara.choiceplayer.util.PlaylistUtil;
import com.gabrielavara.choiceplayer.util.RecycleBinFileMover;
import com.gabrielavara.choiceplayer.util.TimeFormatter;
import com.gabrielavara.choiceplayer.util.TimeSliderConverter;
import com.gabrielavara.choiceplayer.views.Animator;
import com.gabrielavara.choiceplayer.views.PlaylistCell;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXSpinner;
import de.felixroske.jfxsupport.FXMLController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lombok.Getter;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
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
    private JFXListView<PlaylistItemView> playlist;
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
    private AnimatedButton playPauseButton;
    @FXML
    private JFXSlider timeSlider;
    @FXML
    private Label elapsedLabel;
    @FXML
    private Label remainingLabel;

    private MediaPlayer mediaPlayer;

    private FlippableImage flippableAlbumArt = new FlippableImage();
    private AnimatedLabel artist;
    private AnimatedLabel title;
    @Getter
    private ObservableList<PlaylistItemView> playlistItems = FXCollections.observableArrayList();

    private TimeSliderConverter timeSliderConverter = new TimeSliderConverter();

    private Duration duration;

    private PlaylistUtil playlistUtil = new PlaylistUtil(playlistItems);

    private FileMover goodFolderFileMover = new GoodFolderFileMover(playlistUtil, playlistItems);
    private FileMover recycleBinFileMover = new RecycleBinFileMover(playlistUtil, playlistItems);
    private boolean timeSliderUpdateDisabled;
    private PlaylistInitializer playlistInitializer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playPauseButton.setController(this);
        addAlbumArt();
        setupAlbumAndTitleLabels();
        timeSlider.setLabelFormatter(timeSliderConverter);
        setButtonListeners();
        animateItems();
        registerGlobalKeyListener();
        Messenger.register(PlaylistItemSelectedMessage.class, this::selectPlaylistItem);
        Messenger.register(SelectionChangedMessage.class, this::selectionChanged);
        playlistInitializer = new PlaylistInitializer(playlist, playlistItems, spinner, playlistStackPane);
        playlistInitializer.loadPlaylist();
    }

    private void selectPlaylistItem(PlaylistItemSelectedMessage message) {
        int index = message.getPlaylistItemView().getIndex() - 1;
        MultipleSelectionModel<PlaylistItemView> selectionModel = playlist.getSelectionModel();
        selectionModel.select(index);
    }

    private void selectionChanged(SelectionChangedMessage message) {
        Mp3 newValue = message.getNewValue();
        artist.setText(newValue.getArtist());
        title.setText(newValue.getTitle());
        timeSliderConverter.setLength(newValue.getLength());
        setCurrentlyPlayingAlbumArt();
        play(newValue);
        playPauseButton.play();
        setPlaylistItemStates(message);
    }

    private void setPlaylistItemStates(SelectionChangedMessage message) {
        message.getOldValue().ifPresent(ov -> {
            Optional<PlaylistItemView> oldPlaylistItem = playlistUtil.getPlaylistItemView(ov);
            oldPlaylistItem.ifPresent(i -> {
                Optional<PlaylistCell> cell = playlistInitializer.getCell(i);
                cell.ifPresent(c -> c.getPlaylistItem().animateToState(DESELECTED));
            });
        });

        Optional<PlaylistItemView> newPlaylistItem = playlistUtil.getCurrentlyPlayingPlaylistItemView();
        newPlaylistItem.ifPresent(i -> {
            Optional<PlaylistCell> cell = playlistInitializer.getCell(i);
            cell.ifPresent(c -> c.getPlaylistItem().animateToState(SELECTED));
        });
    }

    private void play(Mp3 mp3) {
        disposeMediaPlayer();
        loadBeep();
        disposeMediaPlayer();
        loadMediaPlayer(mp3);
    }

    private void disposeMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnReady(null);
            mediaPlayer.setOnEndOfMedia(null);
            mediaPlayer.currentTimeProperty().removeListener(updateValueListener());
            mediaPlayer.dispose();
            waitForDispose();
        }
    }

    private void loadBeep() {
        Optional<String> mediaUrl = MediaUrl.create(Paths.get("src/main/resources/mp3/beep.mp3"));
        mediaUrl.ifPresent(s -> mediaPlayer = new MediaPlayer(new Media(s)));
    }

    private void loadMediaPlayer(Mp3 mp3) {
        Optional<String> mediaUrl = MediaUrl.create(mp3);
        mediaUrl.ifPresent(url -> {
            mediaPlayer = new MediaPlayer(new Media(url));
            addMediaPlayerListeners();
            play();
        });
    }

    private void waitForDispose() {
        try {
            Awaitility.with().pollInterval(DISPOSE_WAIT_MS, MILLISECONDS).await().atMost(DISPOSE_MAX_WAIT_S, SECONDS).until(getStatus(),
                    equalTo(DISPOSED));
        } catch (ConditionTimeoutException e) {
            log.debug("Media player not disposed :( {}");
        }
    }

    private Callable<MediaPlayer.Status> getStatus() {
        return () -> mediaPlayer.statusProperty().get();
    }

    private void addMediaPlayerListeners() {
        mediaPlayer.currentTimeProperty().addListener(updateValueListener());

        mediaPlayer.setOnReady(() -> {
            duration = mediaPlayer.getMedia().getDuration();
            updateValues();
        });

        mediaPlayer.setOnEndOfMedia(() -> playlistUtil.goToNextTrack());
    }

    private InvalidationListener updateValueListener() {
        return ov -> updateValues();
    }

    private void updateValues() {
        if (timeSliderUpdateDisabled) {
            return;
        }
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
        artist = new AnimatedLabel("currently-playing-artist-label");
        title = new AnimatedLabel("currently-playing-title-label");
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
        previousTrackButton.setOnMouseClicked(e -> playlistUtil.goToPreviousTrack());
        nextTrackButton.setOnMouseClicked(e -> playlistUtil.goToNextTrack());
        timeSlider.setOnMousePressed(e -> disableTimeSliderUpdate());
        timeSlider.setOnMouseReleased(e -> enableTimeSliderUpdate());
        timeSlider.setOnMouseClicked(e -> seek(false));
        timeSlider.valueProperty().addListener(ov -> seek(true));
        likeButton.setOnMouseClicked(e -> goodFolderFileMover.moveFile());
        dislikeButton.setOnMouseClicked(e -> recycleBinFileMover.moveFile());
    }

    private void disableTimeSliderUpdate() {
        timeSliderUpdateDisabled = true;
    }

    private void enableTimeSliderUpdate() {
        timeSliderUpdateDisabled = false;
        mediaPlayer.currentTimeProperty().addListener(updateValueListener());
    }

    private void seek(boolean shouldConsiderValueChanging) {
        if (mediaPlayer == null) {
            return;
        }
        if (!shouldConsiderValueChanging || timeSlider.isValueChanging()) {
            createSeekTimeLine(seek()).play();
        }
    }

    private EventHandler<ActionEvent> seek() {
        return t -> mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
    }

    public void playPause() {
        if (mediaPlayer == null) {
            playlistUtil.select(playlistItems.get(0));
        }
        if (mediaPlayer != null) {
            MediaPlayer.Status status = mediaPlayer.getStatus();
            if (status == UNKNOWN || status == HALTED) {
                return;
            }
            if (status == PAUSED || status == READY || status == STOPPED) {
                play();
            } else {
                pause();
            }
        }
    }

    private void play() {
        Timeline timeLine = createPlayPauseVolumeTimeLine(1, ANIMATION_DURATION);
        mediaPlayer.play();
        timeLine.play();
    }

    private void pause() {
        Timeline volumeTimeLine = createPlayPauseVolumeTimeLine(0, SHORT_ANIMATION_DURATION);
        volumeTimeLine.setOnFinished(e -> mediaPlayer.pause());
        volumeTimeLine.play();
    }

    private Timeline createPlayPauseVolumeTimeLine(double volume, int duration) {
        return new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(mediaPlayer.volumeProperty(), mediaPlayer.getVolume())),
                new KeyFrame(Duration.millis(duration), new KeyValue(mediaPlayer.volumeProperty(), volume)));
    }

    private void setCurrentlyPlayingAlbumArt() {
        Optional<byte[]> albumArtData = playlistUtil.getCurrentlyPlayingAlbumArt();
        flippableAlbumArt.setImage(ImageUtil.getAlbumArt(albumArtData));
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
        if (mediaPlayer == null) {
            return;
        }
        createSeekTimeLine(seek(-SEEK_SECONDS)).play();
    }

    public void fastForward() {
        if (mediaPlayer == null) {
            return;
        }
        createSeekTimeLine(seek(SEEK_SECONDS)).play();
    }

    private Timeline createSeekTimeLine(EventHandler<ActionEvent> seek) {
        return new Timeline(
                new KeyFrame(Duration.ZERO, seek, new KeyValue(mediaPlayer.volumeProperty(), 0)),
                new KeyFrame(Duration.millis(ANIMATION_DURATION), new KeyValue(mediaPlayer.volumeProperty(), 1)));
    }

    private EventHandler<ActionEvent> seek(int s) {
        return t -> mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(s)));
    }
}
