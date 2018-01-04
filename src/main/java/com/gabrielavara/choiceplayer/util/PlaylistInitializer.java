package com.gabrielavara.choiceplayer.util;

import static com.gabrielavara.choiceplayer.Constants.ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.LONG_ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.SHORT_DELAY;
import static com.gabrielavara.choiceplayer.controls.AnimationDirection.IN;
import static com.gabrielavara.choiceplayer.controls.AnimationDirection.OUT;
import static java.util.stream.Collectors.toList;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.api.service.PlaylistCache;
import com.gabrielavara.choiceplayer.api.service.PlaylistLoader;
import com.gabrielavara.choiceplayer.controls.AnimationDirection;
import com.gabrielavara.choiceplayer.messages.SelectionChangedMessage;
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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class PlaylistInitializer {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.util.PlaylistInitializer");

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
        List<PlaylistItemView> cachedItems = PlaylistCache.load();
        if (!cachedItems.isEmpty()) {
            playlistItemViews.addAll(cachedItems);
            showItems();
        }

        Task<List<PlaylistItemView>> playListLoaderTask = new Task<List<PlaylistItemView>>() {
            @Override
            protected List<PlaylistItemView> call() {
                List<Mp3> files = new PlaylistLoader().load(Paths.get(ChoicePlayerApplication.getSettings().getFolder()));
                return IntStream.range(0, files.size()).mapToObj(index -> new PlaylistItemView(index + 1, files.get(index))).collect(toList());
            }
        };

        playListLoaderTask.setOnSucceeded(e -> {
            List<PlaylistItemView> items = playListLoaderTask.getValue();
            if (!cachedItems.equals(items)) {
                log.info("Loaded playlist not equals cached playlist");
                if (playlistItemViews.isEmpty()) {
                    playlistItemViews.addAll(items);
                    showItems();
                } else {
                    animateOutItems(ev -> {
                        playlistItemViews.clear();
                        playlistItemViews.addAll(items);
                        showItems();
                    });
                }
            }
        });

        new Thread(playListLoaderTask).start();
    }

    private void showItems() {
        PauseTransition wait = new PauseTransition(Duration.millis(50));
        wait.setOnFinished(ev -> {
            animateItems(IN);
            if (playlistItemViews.size() > 0) {
                Messenger.send(new SelectionChangedMessage(playlistItemViews.get(0).getMp3(), null, false));
            }
        });
        wait.play();
    }

    private void animateOutItems(EventHandler<ActionEvent> finishedEventHandler) {
        animateSpinner(IN);
        animateListItems(OUT, finishedEventHandler);
    }

    public void animateItems(AnimationDirection direction) {
        animateSpinner(direction.getInverse());
        animateListItems(direction, null);
    }

    private void animateSpinner(AnimationDirection direction) {
        ParallelTransition parallelTransition = getSpinnerAnimation(direction);
        if (direction == IN) {
            if (!playlistStackPane.getChildren().contains(spinner)) {
                playlistStackPane.getChildren().add(spinner);
            }
            parallelTransition.setDelay(Duration.millis(ANIMATION_DURATION));
        }
        if (direction == OUT) {
            parallelTransition.setOnFinished(e -> playlistStackPane.getChildren().remove(spinner));
        }
        parallelTransition.play();
    }

    private void animateListItems(AnimationDirection direction, EventHandler<ActionEvent> finishedEventHandler) {
        int[] delay = new int[1];
        delay[0] = 0;
        beforeAnimate = direction == OUT;
        cells.forEach((PlaylistCell item) -> {
            animateItem(item, direction, delay[0], finishedEventHandler);
            if (direction == IN) {
                delay[0] += SHORT_DELAY;
            }
        });
    }

    private void animateItem(PlaylistCell item, AnimationDirection direction, int delay, EventHandler<ActionEvent> finishedEventHandler) {
        Transition transition = getItemTransition(item, direction, delay);
        if (direction == IN) {
            transition.setOnFinished(e -> item.setOpacity(1));
        }
        if (direction == OUT && cells.indexOf(item) == cells.size() - 1) {
            transition.setOnFinished(finishedEventHandler);
        }
        transition.play();
    }

    private Transition getItemTransition(Node item, AnimationDirection direction, int delay) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(LONG_ANIMATION_DURATION), item);
        fadeTransition.setFromValue(direction == IN ? 0 : 1);
        fadeTransition.setToValue(direction == IN ? 1 : 0);
        fadeTransition.setDelay(Duration.millis(delay));
        return fadeTransition;
    }

    private ParallelTransition getSpinnerAnimation(AnimationDirection direction) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(ANIMATION_DURATION), spinner);
        fadeTransition.setFromValue(direction == IN ? 0 : 1);
        fadeTransition.setToValue(direction == IN ? 1 : 0);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(ANIMATION_DURATION), spinner);
        translateTransition.setByY(direction == IN ? -350 : 350);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition, translateTransition);
        return parallelTransition;
    }

    private ListCell<PlaylistItemView> playListCellFactory(ListView<PlaylistItemView> lv) {
        PlaylistCell cell = new PlaylistCell();
        if (beforeAnimate) {
            cell.setOpacity(0);
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
