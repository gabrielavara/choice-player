package com.gabrielavara.choiceplayer.controllers;

import static java.text.MessageFormat.format;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.api.service.MusicService;
import com.gabrielavara.choiceplayer.api.service.PlaylistLoader;
import com.gabrielavara.choiceplayer.settings.Settings;
import com.gabrielavara.choiceplayer.util.GlobalKeyListener;
import com.gabrielavara.choiceplayer.views.AnimatingLabel;
import com.gabrielavara.choiceplayer.views.Animator;
import com.gabrielavara.choiceplayer.views.FlippableImage;
import com.gabrielavara.choiceplayer.views.TableItem;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import de.felixroske.jfxsupport.FXMLController;
import javafx.animation.ParallelTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
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
    private StackPane albumArtStackPane;
    @FXML
    @Setter
    private JFXTreeTableView<TableItem> playlist;
    @FXML
    private VBox currentlyPlayingBox;
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
    private ObservableList<TableItem> mp3Files;

    @Getter
    private TimeSliderConverter timeSliderConverter = new TimeSliderConverter();

    @Setter
    private Duration duration;

    private PlaylistSelectionChangedListener playlistSelectionChangedListener;
    private ResourceBundle resourceBundle;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Settings settings = loadSettings();
        Color accentColor = settings.getTheme().getAccentColor();
        modifyCss(accentColor);
        // Scene scene = ChoicePlayerApplication.getScene();
        // scene.getStylesheets().clear();
        // scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        resourceBundle = ResourceBundle.getBundle("language.player");

        flippableAlbumArt = new FlippableImage();
        artist = new AnimatingLabel("", 20);
        title = new AnimatingLabel("", 16);

        playlistSelectionChangedListener = new PlaylistSelectionChangedListener(this);
        playlist.getSelectionModel().selectedItemProperty().addListener(playlistSelectionChangedListener);
        loadPlaylist();

        albumArtStackPane.getChildren().add(flippableAlbumArt);
        VBox.setMargin(artist, new Insets(6, 24, 6, 24));
        VBox.setMargin(title, new Insets(6, 24, 6, 24));

        currentlyPlayingBox.getChildren().add(1, artist);
        currentlyPlayingBox.getChildren().add(2, title);

        timeSlider.setLabelFormatter(timeSliderConverter);

        setButtonListeners();
        animateItems();
        registerGlobalKeyListener();
    }

    private static Settings loadSettings() {
        try {
            return new ObjectMapper().readValue(new File("settings.json"), Settings.class);
        } catch (IOException e) {
            log.info("Could not load settings.json file");
            return new Settings();
        }
    }

    private static void modifyCss(Color accentColor) {
        try {
            Path path = Paths.get("src/main/resources/css/style.css");
            String content = new String(Files.readAllBytes(path));
            String accentColorReplacement = format("accent-color: rgba({0}, {1}, {2}, {3})", accentColor.getRed(),
                            accentColor.getGreen(), accentColor.getBlue(), accentColor.getAlpha());
            content = content.replaceAll("accent-color: rgb(29, 185, 84);", accentColorReplacement);
            Files.write(path, content.getBytes());
        } catch (IOException e) {
            log.error("Could not modify style.css");
        }
    }

    private void animateItems() {
        Animator animator = new Animator(Animator.Direction.IN);
        animator.setup(albumArtStackPane, artist, title, timeSlider, elapsedLabel, remainingLabel, previousTrackButton,
                playPauseButton, nextTrackButton);
        animator.add(albumArtStackPane).add(artist).add(title).add(timeSlider).add(elapsedLabel, remainingLabel)
                .add(previousTrackButton, playPauseButton, nextTrackButton);
        ParallelTransition transition = animator.build();
        transition.play();
    }

    private void setButtonListeners() {
        previousTrackButton.setOnMouseClicked(event -> {
            goToPreviousTrack();
        });

        nextTrackButton.setOnMouseClicked(event -> {
            goToNextTrack();
        });

        playPauseButton.setOnMouseClicked(event -> {
            if (mediaPlayer == null) {
                return;
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
        });

        timeSlider.setOnMouseClicked(event -> {
            seek(false);
        });

        timeSlider.valueProperty().addListener(ov -> {
            seek(true);
        });
    }

    private void seek(boolean shouldConsiderValueChanging) {
        if (mediaPlayer == null) {
            return;
        }
        if (!shouldConsiderValueChanging || timeSlider.isValueChanging()) {
            mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
        }
    }

    public void goToPreviousTrack() {
        getPreviousTrack().ifPresent(nextTrack -> {
            playlistSelectionChangedListener.changed(getCurrentlyPlaying().orElse(null), nextTrack);
        });
    }

    public void goToNextTrack() {
        getNextTrack().ifPresent(previousTrack -> {
            playlistSelectionChangedListener.changed(getCurrentlyPlaying().orElse(null), previousTrack);
        });
    }

    private void loadPlaylist() {
        loadMp3Files();
        TreeItem<TableItem> root = new RecursiveTreeItem<>(mp3Files, RecursiveTreeObject::getChildren);
        playlist.setRoot(root);
        playlist.setShowRoot(false);
        playlist.setEditable(false);
        addColumns();
    }

    private void addColumns() {
        JFXTreeTableColumn<TableItem, String> indexColumn = new JFXTreeTableColumn<>("#");
        indexColumn.setPrefWidth(150);
        indexColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableItem, String> param) -> {
            if (indexColumn.validateValue(param)) {
                return param.getValue().getValue().getIndex();
            } else {
                return indexColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<TableItem, String> artistColumn = new JFXTreeTableColumn<>(resourceBundle.getString("artist").toUpperCase());
        artistColumn.setPrefWidth(150);
        artistColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableItem, String> param) -> {
            if (artistColumn.validateValue(param)) {
                return param.getValue().getValue().getArtist();
            } else {
                return artistColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<TableItem, String> titleColumn = new JFXTreeTableColumn<>(resourceBundle.getString("title").toUpperCase());
        titleColumn.setPrefWidth(150);
        titleColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableItem, String> param) -> {
            if (titleColumn.validateValue(param)) {
                return param.getValue().getValue().getTrack();
            } else {
                return titleColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<TableItem, String> lengthColumn = new JFXTreeTableColumn<>(resourceBundle.getString("length").toUpperCase());
        lengthColumn.setPrefWidth(150);
        lengthColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableItem, String> param) -> {
            if (lengthColumn.validateValue(param)) {
                return param.getValue().getValue().getLength();
            } else {
                return lengthColumn.getComputedValue(param);
            }
        });

        indexColumn.setCellFactory((TreeTableColumn<TableItem, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        indexColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<TableItem, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition().getRow()).getValue().getIndex().set(t.getNewValue()));

        artistColumn.setCellFactory((TreeTableColumn<TableItem, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        artistColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<TableItem, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition().getRow()).getValue().getArtist().set(t.getNewValue()));

        titleColumn.setCellFactory((TreeTableColumn<TableItem, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        titleColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<TableItem, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition().getRow()).getValue().getTrack().set(t.getNewValue()));

        lengthColumn.setCellFactory((TreeTableColumn<TableItem, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        lengthColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<TableItem, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition().getRow()).getValue().getLength().set(t.getNewValue()));

        playlist.getColumns().setAll(indexColumn, artistColumn, titleColumn, lengthColumn);
    }

    void loadMp3Files() {
        List<Mp3> files = new PlaylistLoader().load(Paths.get("src/test/resources/mp3folder"));
        List<TableItem> tableItems = IntStream.range(0, files.size())
                .mapToObj(index -> new TableItem(index + 1, files.get(index))).collect(Collectors.toList());
        mp3Files = FXCollections.observableArrayList(tableItems);
    }

    public Optional<Mp3> getCurrentlyPlaying() {
        List<Mp3> playing = mp3Files.stream().filter(s -> s.getMp3().isCurrentlyPlaying()).map(TableItem::getMp3)
                .collect(Collectors.toList());
        return playing.size() == 1 ? Optional.of(playing.get(0)) : Optional.empty();
    }

    Optional<Mp3> getNextTrack() {
        OptionalInt first = IntStream.range(0, mp3Files.size())
                .filter(i -> mp3Files.get(i).getMp3().isCurrentlyPlaying()).findFirst();
        if (first.isPresent()) {
            int index = first.getAsInt();
            return mp3Files.size() > index + 1 ? Optional.of(mp3Files.get(index + 1).getMp3()) : Optional.empty();
        }
        return Optional.of(mp3Files.get(0).getMp3());
    }

    Optional<Mp3> getPreviousTrack() {
        OptionalInt first = IntStream.range(0, mp3Files.size())
                .filter(i -> mp3Files.get(i).getMp3().isCurrentlyPlaying()).findFirst();
        if (first.isPresent()) {
            int index = first.getAsInt();
            return 0 <= index - 1 ? Optional.of(mp3Files.get(index - 1).getMp3()) : Optional.empty();
        }
        return Optional.of(mp3Files.get(0).getMp3());
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
    }

    public void moveFileToRecycleBin() {
        log.info("Move file to recycle bin");
    }

    public void rewind() {
        log.info("Rewind");
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.seek(Duration.seconds(-5));
    }

    public void fastForward() {
        log.info("Fast forward");
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.seek(Duration.seconds(5));
    }
}
