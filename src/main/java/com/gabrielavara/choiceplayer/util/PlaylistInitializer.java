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
    private ObservableList<PlaylistItemView> playlistItemViews;
    private JFXSpinner spinner;
    private StackPane playlistStackPane;
    private List<PlaylistCell> cells = new ArrayList<>();
    private boolean beforeAnimate = true;

    public PlaylistInitializer(JFXListView<PlaylistItemView> playlist, ObservableList<PlaylistItemView> playlistItemViews, JFXSpinner spinner,
                               StackPane playlistStackPane) {
        this.playlistItemViews = playlistItemViews;
        this.spinner = spinner;
        this.playlistStackPane = playlistStackPane;
        playlist.setItems(playlistItemViews);
        playlist.setCellFactory(this::playListCellFactory);
        playlist.getSelectionModel().selectedItemProperty().addListener(new PlaylistSelectionChangedListener());
    }

    public void loadPlaylist() {
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
            wait.setOnFinished(ev -> animateItems(true));
            wait.play();
        });

        new Thread(playListLoaderTask).start();
    }

    public void animateItems(boolean in) {
        animateSpinner(!in);
        animateListItems(in);
    }

    private void animateSpinner(boolean in) {
        ParallelTransition parallelTransition = getSpinnerAnimation(in);
        if (in) {
            if (!playlistStackPane.getChildren().contains(spinner)) {
                playlistStackPane.getChildren().add(spinner);
            }
            parallelTransition.setDelay(Duration.millis(ANIMATION_DURATION));
        }
        if (!in) {
            parallelTransition.setOnFinished(e -> playlistStackPane.getChildren().remove(spinner));
        }
        parallelTransition.play();
    }

    private void animateListItems(boolean in) {
        int[] delay = new int[1];
        delay[0] = 0;
        cells.forEach((PlaylistCell row) -> {
            animateItem(row, in, delay[0]);
            if (in) {
                delay[0] += SHORT_DELAY;
            }
        });
    }

    private void animateItem(PlaylistCell item, boolean in, int delay) {
        Transition transition = getItemTransition(item, in, delay);
        if (in) {
            transition.setOnFinished(e -> {
                item.setOpacity(1);
                if (cells.indexOf(item) == cells.size() - 1) {
                    beforeAnimate = false;
                }
            });
        }
        transition.play();
    }

    private Transition getItemTransition(Node item, boolean in, int delay) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(LONG_ANIMATION_DURATION), item);
        fadeTransition.setFromValue(in ? ALMOST_TOTALLY_HIDDEN : 1);
        fadeTransition.setToValue(in ? 1 : ALMOST_TOTALLY_HIDDEN);
        fadeTransition.setDelay(Duration.millis(delay));
        return fadeTransition;
    }

    private ParallelTransition getSpinnerAnimation(boolean in) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(ANIMATION_DURATION), spinner);
        fadeTransition.setFromValue(in ? 0 : 1);
        fadeTransition.setToValue(in ? 1 : 0);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(ANIMATION_DURATION), spinner);
        translateTransition.setByY(in ? -350 : 350);

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
