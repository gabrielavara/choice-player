package com.gabrielavara.choiceplayer.util;

import static com.gabrielavara.choiceplayer.Constants.ALMOST_TOTALLY_HIDDEN;
import static com.gabrielavara.choiceplayer.Constants.ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.SHORT_DELAY;
import static java.util.Arrays.asList;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.api.service.PlaylistLoader;
import com.gabrielavara.choiceplayer.controls.animatedalbumart.AnimatedAlbumArt;
import com.gabrielavara.choiceplayer.views.TableItem;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableRow;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class PlaylistInitializer {
    private JFXTreeTableView<TableItem> playlist;
    private ObservableList<TableItem> tableItems;
    private JFXSpinner spinner;
    private StackPane playlistStackPane;
    private ResourceBundle resourceBundle;
    private List<JFXTreeTableRow<TableItem>> rows = new ArrayList<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });
    private Map<TableItem, AnimatedAlbumArt> map = new HashMap<>();

    public PlaylistInitializer(JFXTreeTableView<TableItem> playlist, ObservableList<TableItem> tableItems, JFXSpinner spinner,
                               StackPane playlistStackPane) {
        this.playlist = playlist;
        this.tableItems = tableItems;
        this.spinner = spinner;
        this.playlistStackPane = playlistStackPane;
        resourceBundle = ResourceBundle.getBundle("language.player");
    }

    public void loadPlaylist() {
        TreeItem<TableItem> root = new RecursiveTreeItem<>(tableItems, RecursiveTreeObject::getChildren);
        playlist.setRoot(root);
        playlist.setShowRoot(false);

        playlist.setRowFactory(row -> {
            JFXTreeTableRow<TableItem> newRow = new JFXTreeTableRow<>();
            newRow.setOpacity(ALMOST_TOTALLY_HIDDEN);
            rows.add(newRow);
            return newRow;
        });
        addColumns();
        loadMp3Files();
        playlist.getSelectionModel().selectedItemProperty().addListener(new PlaylistSelectionChangedListener());
    }

    private void addColumns() {
        JFXTreeTableColumn<TableItem, Number> indexColumn = new JFXTreeTableColumn<>("#");
        indexColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().getIndex());
        indexColumn.setCellFactory((TreeTableColumn<TableItem, Number> param) -> new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder()));

        JFXTreeTableColumn<TableItem, AnchorPane> albumArtColumn = new JFXTreeTableColumn<>("");
        albumArtColumn.setCellValueFactory(this::createAlbumArt);
        albumArtColumn.setCellFactory((TreeTableColumn<TableItem, AnchorPane> param) -> new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder()));

        JFXTreeTableColumn<TableItem, String> artistColumn = new JFXTreeTableColumn<>(resourceBundle.getString("artist").toUpperCase());
        artistColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().getArtist());
        artistColumn.setCellFactory((TreeTableColumn<TableItem, String> param) -> new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder()));

        JFXTreeTableColumn<TableItem, String> titleColumn = new JFXTreeTableColumn<>(resourceBundle.getString("title").toUpperCase());
        titleColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().getTitle());
        titleColumn.setCellFactory((TreeTableColumn<TableItem, String> param) -> new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder()));

        JFXTreeTableColumn<TableItem, String> lengthColumn = new JFXTreeTableColumn<>(resourceBundle.getString("length").toUpperCase());
        lengthColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().getLength());
        lengthColumn.setCellFactory((TreeTableColumn<TableItem, String> param) -> new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder()));

        indexColumn.prefWidthProperty().bind(playlist.widthProperty().multiply(0.1));
        albumArtColumn.prefWidthProperty().bind(playlist.widthProperty().multiply(0.1));
        artistColumn.prefWidthProperty().bind(playlist.widthProperty().multiply(0.325));
        titleColumn.prefWidthProperty().bind(playlist.widthProperty().multiply(0.325));
        lengthColumn.prefWidthProperty().bind(playlist.widthProperty().multiply(0.15));

        List<JFXTreeTableColumn<TableItem, ?>> columns = asList(indexColumn, albumArtColumn, artistColumn, titleColumn, lengthColumn);

        columns.forEach(c -> c.setResizable(false));

        playlist.getColumns().setAll(columns);
    }

    private void loadMp3Files() {
        Task<List<TableItem>> playListLoaderTask = new Task<List<TableItem>>() {
            @Override
            protected List<TableItem> call() {
                List<Mp3> files = new PlaylistLoader().load(Paths.get(ChoicePlayerApplication.getSettings().getFolder()));
                return IntStream.range(0, files.size()).mapToObj(index -> new TableItem(index + 1, files.get(index))).collect(Collectors.toList());
            }
        };

        playListLoaderTask.setOnSucceeded(e -> {
            List<TableItem> items = playListLoaderTask.getValue();
            tableItems.addAll(items);

            PauseTransition wait = new PauseTransition(Duration.millis(50));
            wait.setOnFinished(ev -> {
                animateOutSpinner();
                animateTableItems();
            });
            wait.play();
        });

        new Thread(playListLoaderTask).start();
    }

    private void animateOutSpinner() {
        ParallelTransition parallelTransition = getSpinnerOutAnimation();
        parallelTransition.setOnFinished(e -> playlistStackPane.getChildren().remove(spinner));
        parallelTransition.play();
    }

    private void animateTableItems() {
        int[] delay = new int[1];
        delay[0] = 0;
        Collections.shuffle(rows);
        rows.forEach((JFXTreeTableRow<TableItem> row) -> {
            animateRow(row, delay[0]);
            delay[0] += SHORT_DELAY;
        });
    }

    private void animateRow(JFXTreeTableRow<TableItem> row, int delay) {
        Transition transition = getRowTransition(row, delay);
        transition.setOnFinished(e -> {
            row.setOpacity(1);
            if (rows.indexOf(row) == rows.size() - 1) {
                playlist.setRowFactory(this::playListRowFactory);
            }
        });
        transition.play();
    }

    private Transition getRowTransition(JFXTreeTableRow<TableItem> tableRow, int delay) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(ANIMATION_DURATION), tableRow);
        fadeTransition.setFromValue(ALMOST_TOTALLY_HIDDEN);
        fadeTransition.setToValue(1);
        fadeTransition.setDelay(Duration.millis(delay));
        return fadeTransition;
    }

    private ParallelTransition getSpinnerOutAnimation() {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(ANIMATION_DURATION / 7 * 5), spinner);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(ANIMATION_DURATION / 7 * 5), spinner);
        translateTransition.setByY(350);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition, translateTransition);
        return parallelTransition;
    }

    private ObservableValue<AnchorPane> createAlbumArt(TreeTableColumn.CellDataFeatures<TableItem, AnchorPane> param) {
        AnimatedAlbumArt animatedAlbumArt = new AnimatedAlbumArt();

        PauseTransition wait = new PauseTransition(Duration.millis(100));
        wait.setOnFinished(ev -> {
            Mp3 mp3 = param.getValue().getValue().getMp3();

            AlbumArtLoaderTask task = new AlbumArtLoaderTask(mp3);
            task.setOnSucceeded(e -> animatedAlbumArt.setImage(task.getValue()));
            executorService.submit(task);
        });
        wait.play();

        map.put(param.getValue().getValue(), animatedAlbumArt);

        return new SimpleObjectProperty<>(animatedAlbumArt);
    }

    private TreeTableRow<TableItem> playListRowFactory(TreeTableView<TableItem> r) {
        JFXTreeTableRow<TableItem> newRow = new JFXTreeTableRow<>();
        newRow.hoverProperty().addListener((observable, oldValue, newValue) -> {
            TableItem item = newRow.getItem();
            if (map.containsKey(item)) {
                AnimatedAlbumArt animatedAlbumArt = map.get(item);
                animatedAlbumArt.hover(newValue);
            }
        });
        return newRow;
    }
}
