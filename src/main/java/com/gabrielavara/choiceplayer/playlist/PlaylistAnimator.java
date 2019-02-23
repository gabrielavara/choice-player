package com.gabrielavara.choiceplayer.playlist;

import com.gabrielavara.choiceplayer.controls.AnimationDirection;
import com.gabrielavara.choiceplayer.messages.PlaylistLoadedMessage;
import com.gabrielavara.choiceplayer.messages.SelectItemInNewPlaylistMessage;
import com.gabrielavara.choiceplayer.messenger.Messenger;
import com.gabrielavara.choiceplayer.views.PlaylistCell;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.gabrielavara.choiceplayer.Constants.ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.DELAY;
import static com.gabrielavara.choiceplayer.Constants.LONG_ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.controls.AnimationDirection.IN;
import static com.gabrielavara.choiceplayer.controls.AnimationDirection.OUT;
import static java.util.stream.Collectors.toList;

public class PlaylistAnimator {
    private final JFXSpinner spinner;
    private final StackPane playlistStackPane;
    private List<PlaylistCell> cells = new ArrayList<>();
    private boolean beforeListAnimatedIn = true;

    public PlaylistAnimator(JFXListView<PlaylistItemView> playlistView, JFXSpinner spinner, StackPane playlistStackPane) {
        this.spinner = spinner;
        this.playlistStackPane = playlistStackPane;
        playlistView.setCellFactory(this::playListCellFactory);
        playlistView.setOnScroll(e -> cells.forEach(c -> c.setOpacity(1)));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    void showItems(Optional<PlaylistItemView> selected, boolean cache) {
        PauseTransition wait = new PauseTransition(Duration.millis(50));
        wait.setOnFinished(ev -> animateInItems(selected, cache));
        wait.play();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void animateInItems(Optional<PlaylistItemView> selected, boolean cache) {
        animateItems(IN, null, selected, cache);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    void animateOutItems(EventHandler<ActionEvent> finishedEventHandler, Optional<PlaylistItemView> selected) {
        animateSpinner(AnimationDirection.OUT.getInverse());
        animateListItems(AnimationDirection.OUT, finishedEventHandler, selected, false);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    void animateItems(AnimationDirection direction, EventHandler<ActionEvent> finishedEventHandler, Optional<PlaylistItemView> selected, boolean cache) {
        animateSpinner(direction.getInverse());
        animateListItems(direction, finishedEventHandler, selected, cache);
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

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void animateListItems(AnimationDirection direction, EventHandler<ActionEvent> finishedEventHandler, Optional<PlaylistItemView> selected, boolean cache) {
        int[] delay = new int[1];
        beforeListAnimatedIn = true;

        ParallelTransition parallelTransition = getItemsParallelTransition(direction, delay);

        parallelTransition.setOnFinished(e -> {
            if (direction == IN) {
                beforeListAnimatedIn = false;
                selected.ifPresent(s -> Messenger.send(new SelectItemInNewPlaylistMessage(s)));
            }
            if (finishedEventHandler != null) {
                finishedEventHandler.handle(null);
            }
            if (!cache && direction == IN) {
                Messenger.send(new PlaylistLoadedMessage());
            }
        });
        parallelTransition.play();
    }

    private ParallelTransition getItemsParallelTransition(AnimationDirection direction, int[] delay) {
        ParallelTransition parallelTransition = new ParallelTransition();

        Stream<PlaylistCell> sortedPlaylistCells = cells.stream().filter(c -> c.getPlaylistItemView() != null)
                .sorted(Comparator.comparing(c2 -> c2.getPlaylistItemView().getIndex()));

        sortedPlaylistCells.forEach((PlaylistCell item) -> {
            Transition transition = getItemTransition(item, direction, delay[0]);
            parallelTransition.getChildren().add(transition);
            if (direction == IN) {
                delay[0] += DELAY;
            }
        });
        return parallelTransition;
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
        translateTransition.setFromY(direction == IN ? 350 : 0);
        translateTransition.setToY(direction == IN ? 0 : 350);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition, translateTransition);
        return parallelTransition;
    }

    @SuppressWarnings({"squid:S1172", "unused"})
    private ListCell<PlaylistItemView> playListCellFactory(ListView<PlaylistItemView> lv) {
        PlaylistCell cell = new PlaylistCell();
        if (beforeListAnimatedIn) {
            cell.setOpacity(0);
        } else {
            cell.hoverProperty().addListener((o, oldValue, newValue) -> cell.getPlaylistItem().getAlbumArt().hover(newValue));
        }
        cells.add(cell);
        return cell;
    }

    Optional<PlaylistCell> getCell(PlaylistItemView playlistItemView) {
        return cells.stream().filter(c -> playlistItemView.equals(c.getPlaylistItemView())).findFirst();
    }

    List<PlaylistCell> getCellsAfter(PlaylistItemView playlistItemView) {
        return cells.stream().filter(c -> c.getPlaylistItemView() != null && playlistItemView.getIndex() < c.getPlaylistItemView().getIndex())
                .sorted(Comparator.comparing(c2 -> c2.getPlaylistItemView().getIndex())).collect(toList());
    }

    void changeTheme() {
        cells.forEach(PlaylistCell::changeTheme);
    }
}
