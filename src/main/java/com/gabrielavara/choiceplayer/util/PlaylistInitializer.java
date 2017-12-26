package com.gabrielavara.choiceplayer.util;

import static com.gabrielavara.choiceplayer.Constants.ALMOST_TOTALLY_HIDDEN;
import static com.gabrielavara.choiceplayer.Constants.ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.LONG_ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.SHORT_DELAY;
import static java.util.stream.Collectors.toList;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.api.service.PlaylistLoader;
import com.gabrielavara.choiceplayer.views.PlaylistCell;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class PlaylistInitializer {
    private JFXListView<PlaylistItemView> playlist;
    private ObservableList<PlaylistItemView> playlistItemViews;
    private JFXSpinner spinner;
    private StackPane playlistStackPane;
    private List<PlaylistCell> cells = new ArrayList<>();
    private boolean beforeAnimate = true;

    public PlaylistInitializer(JFXListView<PlaylistItemView> playlist, ObservableList<PlaylistItemView> playlistItemViews, JFXSpinner spinner,
                               StackPane playlistStackPane) {
        this.playlist = playlist;
        this.playlistItemViews = playlistItemViews;
        this.spinner = spinner;
        this.playlistStackPane = playlistStackPane;
    }

    public void loadPlaylist() {
        playlist.setItems(playlistItemViews);
        playlist.setCellFactory(this::playListCellFactory);
        loadPlaylistItems();
        playlist.getSelectionModel().selectedItemProperty().addListener(new PlaylistSelectionChangedListener());
    }

    private void loadPlaylistItems() {
        Task<List<PlaylistItemView>> playListLoaderTask = new Task<List<PlaylistItemView>>() {
            @Override
            protected List<PlaylistItemView> call() {
                List<Mp3> files = new PlaylistLoader().load(Paths.get(ChoicePlayerApplication.getSettings().getFolder()));
                return IntStream.range(0, files.size()).mapToObj(index -> new PlaylistItemView(index + 1, files.get(index))).collect(toList());
            }
        };

        playListLoaderTask.setOnSucceeded(e -> {
            List<PlaylistItemView> items = playListLoaderTask.getValue();
            playlistItemViews.addAll(items);

            PauseTransition wait = new PauseTransition(Duration.millis(50));
            wait.setOnFinished(ev -> {
                animateOutSpinner();
                animateListItems();
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

    private void animateListItems() {
        int[] delay = new int[1];
        delay[0] = 0;
        cells.forEach((PlaylistCell row) -> {
            animateItem(row, delay[0]);
            delay[0] += SHORT_DELAY;
        });
    }

    private void animateItem(PlaylistCell item, int delay) {
        Transition transition = getItemTransition(item, delay);
        transition.setOnFinished(e -> {
            item.setOpacity(1);
            if (cells.indexOf(item) == cells.size() - 1) {
                beforeAnimate = false;
            }
        });
        transition.play();
    }

    private Transition getItemTransition(Node item, int delay) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(LONG_ANIMATION_DURATION), item);
        fadeTransition.setFromValue(ALMOST_TOTALLY_HIDDEN);
        fadeTransition.setToValue(1);
        fadeTransition.setDelay(Duration.millis(delay));
        return fadeTransition;
    }

    private ParallelTransition getSpinnerOutAnimation() {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(ANIMATION_DURATION), spinner);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(ANIMATION_DURATION), spinner);
        translateTransition.setByY(350);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition, translateTransition);
        return parallelTransition;
    }

    private ListCell<PlaylistItemView> playListCellFactory(ListView<PlaylistItemView> lv) {
        PlaylistCell cell = new PlaylistCell();
        if (beforeAnimate) {
            cell.setOpacity(ALMOST_TOTALLY_HIDDEN);
        } else {
            cell.hoverProperty().addListener((o, oldValue, newValue) -> cell.getPlaylistItem().getAlbumArt().hover(newValue));
        }
        cells.add(cell);
        return cell;
    }

    public Optional<PlaylistCell> getCell(PlaylistItemView playlistItemView) {
        return cells.stream().filter(c -> playlistItemView.equals(c.getPlaylistItemView())).findFirst();
    }

    public List<PlaylistCell> getCellsAfter(PlaylistItemView playlistItemView) {
        return cells.stream().filter(c -> c.getPlaylistItemView() != null && playlistItemView.getIndex() < c.getPlaylistItemView().getIndex()).collect(toList());
    }
}
