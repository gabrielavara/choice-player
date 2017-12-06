package com.gabrielavara.choiceplayer.controllers;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.api.service.MusicService;
import com.gabrielavara.choiceplayer.api.service.PlaylistLoader;
import com.gabrielavara.choiceplayer.util.GlobalKeyListener;
import com.gabrielavara.choiceplayer.views.AnimatingLabel;
import com.gabrielavara.choiceplayer.views.Animator;
import com.gabrielavara.choiceplayer.views.FlippableImage;
import com.gabrielavara.choiceplayer.views.TableItem;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableRow;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.sun.jna.platform.FileUtils;
import de.felixroske.jfxsupport.FXMLController;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.gabrielavara.choiceplayer.Constants.ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.DELAY;
import static com.gabrielavara.choiceplayer.Constants.SEEK_VOLUME;
import static com.gabrielavara.choiceplayer.Constants.TRANSLATE_Y;
import static java.util.Arrays.asList;

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

    private ResourceBundle resourceBundle;
    private List<JFXTreeTableRow<TableItem>> rows = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceBundle = ResourceBundle.getBundle("language.player");

        flippableAlbumArt = new FlippableImage();
        artist = new AnimatingLabel("artist-label");
        title = new AnimatingLabel("title-label");

        playlist.getSelectionModel().selectedItemProperty().addListener(new PlaylistSelectionChangedListener(this));
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
        previousTrackButton.setOnMouseClicked(event -> goToPreviousTrack());
        nextTrackButton.setOnMouseClicked(event -> goToNextTrack());
        playPauseButton.setOnMouseClicked(event -> playPause());
        timeSlider.setOnMouseClicked(event -> seek(false));
        timeSlider.valueProperty().addListener(ov -> seek(true));
        likeButton.setOnMouseClicked(event -> moveFileToGoodFolder());
        dislikeButton.setOnMouseClicked(event -> moveFileToRecycleBin());
    }

    public void playPause() {
        if (mediaPlayer == null) {
            select(mp3Files.get(0));
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

    public void goToPreviousTrack() {
        getPreviousTableItem().ifPresent(this::select);
    }

    public void goToNextTrack() {
        getNextTableItem().ifPresent(this::select);
    }

    private void select(TableItem tableItem) {
        int index = tableItem.getIndex().get() - 1;
        TreeTableView.TreeTableViewSelectionModel<TableItem> selectionModel = playlist.getSelectionModel();
        selectionModel.select(index);
    }

    private void loadPlaylist() {
        TreeItem<TableItem> root = new RecursiveTreeItem<>(mp3Files, RecursiveTreeObject::getChildren);
        playlist.setRoot(root);
        playlist.setShowRoot(false);

        playlist.setRowFactory(row -> {
            JFXTreeTableRow<TableItem> newRow = new JFXTreeTableRow<>();
            rows.add(newRow);
            return newRow;
        });
        addColumns();
        loadMp3Files();
    }

    private void addColumns() {
        JFXTreeTableColumn<TableItem, Number> indexColumn = new JFXTreeTableColumn<>("#");
        indexColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableItem, Number> param) -> {
            if (indexColumn.validateValue(param)) {
                return param.getValue().getValue().getIndex();
            } else {
                return indexColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<TableItem, String> artistColumn = new JFXTreeTableColumn<>(resourceBundle.getString("artist").toUpperCase());
        artistColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableItem, String> param) -> {
            if (artistColumn.validateValue(param)) {
                return param.getValue().getValue().getArtist();
            } else {
                return artistColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<TableItem, String> titleColumn = new JFXTreeTableColumn<>(resourceBundle.getString("title").toUpperCase());
        titleColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableItem, String> param) -> {
            if (titleColumn.validateValue(param)) {
                return param.getValue().getValue().getTitle();
            } else {
                return titleColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<TableItem, String> lengthColumn = new JFXTreeTableColumn<>(resourceBundle.getString("length").toUpperCase());
        lengthColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableItem, String> param) -> {
            if (lengthColumn.validateValue(param)) {
                return param.getValue().getValue().getLength();
            } else {
                return lengthColumn.getComputedValue(param);
            }
        });

        indexColumn.setCellFactory((TreeTableColumn<TableItem, Number> param) -> new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder()));
        artistColumn.setCellFactory((TreeTableColumn<TableItem, String> param) -> new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder()));
        titleColumn.setCellFactory((TreeTableColumn<TableItem, String> param) -> new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder()));
        lengthColumn.setCellFactory((TreeTableColumn<TableItem, String> param) -> new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder()));

        indexColumn.prefWidthProperty().bind(playlist.widthProperty().multiply(0.1));
        artistColumn.prefWidthProperty().bind(playlist.widthProperty().multiply(0.35));
        titleColumn.prefWidthProperty().bind(playlist.widthProperty().multiply(0.35));
        lengthColumn.prefWidthProperty().bind(playlist.widthProperty().multiply(0.2));

        List<JFXTreeTableColumn<TableItem, ? extends Serializable>> columns = asList(indexColumn, artistColumn, titleColumn, lengthColumn);

        columns.forEach(c -> c.setResizable(false));

        playlist.getColumns().setAll(columns);
    }

    private void loadMp3Files() {
        long loadStart = System.currentTimeMillis();
        Task<List<TableItem>> playListLoaderTask = new Task<List<TableItem>>() {
            @Override
            protected List<TableItem> call() {
                List<Mp3> files = new PlaylistLoader().load(Paths.get(ChoicePlayerApplication.getSettings().getFolder()));
                return IntStream.range(0, files.size()).mapToObj(index -> new TableItem(index + 1, files.get(index))).collect(Collectors.toList());
            }
        };

        playListLoaderTask.setOnSucceeded(e -> {
            List<TableItem> tableItems = playListLoaderTask.getValue();
            mp3Files.addAll(tableItems);

            PauseTransition wait = new PauseTransition(Duration.millis(DELAY));
            wait.setOnFinished(ev -> {
                animateTableItems();
            });
            wait.play();
        });

        playListLoaderTask.run();
    }

    private void animateTableItems() {
        rows.forEach((JFXTreeTableRow<TableItem> row) -> {
            ImageView imageView = createImageView(row);
            final Point2D animationEndPoint = row.localToScene(new Point2D(0, 0));
            final Point2D animationStartPoint = row.localToScene(new Point2D(0, TRANSLATE_Y));
        });
    }

    private ImageView createImageView(final JFXTreeTableRow<TableItem> row) {
        final Image image = row.snapshot(null, null);
        final ImageView imageView = new ImageView(image);
        imageView.setManaged(false);
        return imageView;
    }

    private void animateRow(IndexedCell newRow) {
        newRow.setOpacity(0);
        newRow.setTranslateY(TRANSLATE_Y);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(ANIMATION_DURATION), newRow);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(ANIMATION_DURATION), newRow);
        translateTransition.setByY(-TRANSLATE_Y);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.setDelay(Duration.millis(300));
        parallelTransition.getChildren().addAll(fadeTransition, translateTransition);

        parallelTransition.play();
    }

    public Optional<Mp3> getCurrentlyPlaying() {
        return getCurrentlyPlayingTableItem().map(TableItem::getMp3);
    }

    private Optional<TableItem> getCurrentlyPlayingTableItem() {
        List<TableItem> playing = mp3Files.stream().filter(s -> s.getMp3().isCurrentlyPlaying()).collect(Collectors.toList());
        return playing.size() == 1 ? Optional.of(playing.get(0)) : Optional.empty();
    }

    Optional<Mp3> getNextTrack() {
        return getNextTableItem().map(TableItem::getMp3);
    }

    private Optional<TableItem> getNextTableItem() {
        OptionalInt first = IntStream.range(0, mp3Files.size())
                .filter(i -> mp3Files.get(i).getMp3().isCurrentlyPlaying()).findFirst();
        if (first.isPresent()) {
            int index = first.getAsInt();
            return mp3Files.size() > index + 1 ? Optional.of(mp3Files.get(index + 1)) : Optional.empty();
        }
        return Optional.of(mp3Files.get(0));
    }

    Optional<Mp3> getPreviousTrack() {
        return getPreviousTableItem().map(TableItem::getMp3);
    }

    private Optional<TableItem> getPreviousTableItem() {
        OptionalInt first = IntStream.range(0, mp3Files.size())
                .filter(i -> mp3Files.get(i).getMp3().isCurrentlyPlaying()).findFirst();
        if (first.isPresent()) {
            int index = first.getAsInt();
            return 0 <= index - 1 ? Optional.of(mp3Files.get(index - 1)) : Optional.empty();
        }
        return Optional.of(mp3Files.get(0));
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
        getCurrentlyPlayingTableItem().ifPresent(tableItem -> {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            getNextTableItem().ifPresent(this::select);
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
        getCurrentlyPlayingTableItem().ifPresent(tableItem -> {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            FileUtils fileUtils = FileUtils.getInstance();
            getNextTableItem().ifPresent(this::select);
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
