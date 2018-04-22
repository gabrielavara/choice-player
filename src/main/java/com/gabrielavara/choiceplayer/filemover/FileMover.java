package com.gabrielavara.choiceplayer.filemover;

import static com.gabrielavara.choiceplayer.Constants.ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.COULD_NOT_DELETE_ORIGINAL_FILE;
import static com.gabrielavara.choiceplayer.Constants.COULD_NOT_MOVE_FILE_TO_RECYCLE_BIN;
import static com.gabrielavara.choiceplayer.Constants.RECYCLE_BIN;
import static com.gabrielavara.choiceplayer.Constants.SHORT_ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.SHORT_DELAY;
import static com.gabrielavara.choiceplayer.views.QuadraticInterpolator.QUADRATIC_EASE_BOTH;
import static com.gabrielavara.choiceplayer.views.QuadraticInterpolator.QUADRATIC_EASE_IN;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.controls.overlay.Action;
import com.gabrielavara.choiceplayer.messages.ActionMessage;
import com.gabrielavara.choiceplayer.messages.FileMovedMessage;
import com.gabrielavara.choiceplayer.messenger.Messenger;
import com.gabrielavara.choiceplayer.playlist.PlaylistInitializer;
import com.gabrielavara.choiceplayer.playlist.PlaylistUtil;
import com.gabrielavara.choiceplayer.util.Opinion;
import com.gabrielavara.choiceplayer.views.PlaylistCell;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import com.jfoenix.controls.JFXSnackbar;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.util.Duration;

public abstract class FileMover {
    protected static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.utils.FileMover");

    private final PlaylistUtil playlistUtil;
    private final ObservableList<PlaylistItemView> playlistItemViews;
    private final PlaylistInitializer playlistInitializer;
    private final JFXSnackbar snackBar;
    private final ResourceBundle resourceBundle;

    FileMover(PlaylistUtil playlistUtil, ObservableList<PlaylistItemView> playlistItemViews, PlaylistInitializer playlistInitializer, JFXSnackbar snackBar) {
        this.playlistUtil = playlistUtil;
        this.playlistItemViews = playlistItemViews;
        this.playlistInitializer = playlistInitializer;
        this.snackBar = snackBar;
        resourceBundle = ResourceBundle.getBundle("language.player");
    }

    public void moveFile() {
        log.info("Move file to {}", getTarget());
        playlistUtil.getCurrentlyPlayingPlaylistItemView().ifPresent(item -> {
            playlistUtil.getNextPlaylistItemView().ifPresent(playlistUtil::select);
            startMoveTask(item);
        });
    }

    private void startMoveTask(PlaylistItemView item) {
        Task<Void> task = createMoveTask(item);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        Messenger.send(new ActionMessage(getOpinion() == Opinion.LIKE ? Action.LIKE : Action.DISLIKE));
    }

    private Task<Void> createMoveTask(PlaylistItemView item) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws IOException {
                moveFile(item);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            log.info("Successfully moved to {}", getTarget());
            animateRemove(item);
        });
        task.setOnFailed(e -> {
            log.error("Could not move to {}: {}", getTarget(), item.getMp3());
            String message = RECYCLE_BIN.equals(getTarget())
                    ? resourceBundle.getString(COULD_NOT_MOVE_FILE_TO_RECYCLE_BIN)
                    : resourceBundle.getString(COULD_NOT_DELETE_ORIGINAL_FILE);
            snackBar.enqueue(new JFXSnackbar.SnackbarEvent(message));
        });
        return task;
    }

    private void animateRemove(PlaylistItemView item) {
        Optional<PlaylistCell> currentCell = playlistInitializer.getCell(item);
        if (currentCell.isPresent()) {
            PlaylistCell cell = currentCell.get();
            List<PlaylistCell> cellsAfter = playlistInitializer.getCellsAfter(item);
            animateCells(item, cell, cellsAfter);
        } else {
            remove(item);
        }
        Messenger.send(new FileMovedMessage(getOpinion()));
    }

    private void animateCells(PlaylistItemView item, PlaylistCell cell, List<PlaylistCell> cellsAfter) {
        ParallelTransition parallelTransition = new ParallelTransition();
        animateCurrentCell(cell, parallelTransition);
        animateCellsAfter(cellsAfter, parallelTransition);

        parallelTransition.setOnFinished(e -> {
            resetCells(cell, cellsAfter);
            remove(item);
        });
        parallelTransition.play();
    }

    private void animateCurrentCell(PlaylistCell cell, ParallelTransition parallelTransition) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(ANIMATION_DURATION), cell);
        fadeTransition.setFromValue(cell.getOpacity());
        fadeTransition.setToValue(0);
        fadeTransition.setInterpolator(QUADRATIC_EASE_IN);

        parallelTransition.getChildren().addAll(fadeTransition);
    }

    private void animateCellsAfter(List<PlaylistCell> cellsAfter, ParallelTransition parallelTransition) {
        for (int i = 0; i < cellsAfter.size(); i++) {
            PlaylistCell c = cellsAfter.get(i);
            TranslateTransition tt = new TranslateTransition(Duration.millis(ANIMATION_DURATION), c);
            tt.setFromY(c.getTranslateY());
            tt.setByY(-c.getHeight());
            tt.setInterpolator(QUADRATIC_EASE_BOTH);
            tt.setDelay(Duration.millis(SHORT_ANIMATION_DURATION + SHORT_DELAY * i));
            parallelTransition.getChildren().add(tt);
        }
    }

    private void resetCells(PlaylistCell cell, List<PlaylistCell> cellsAfter) {
        cellsAfter.add(cell);
        cellsAfter.forEach(c -> {
            c.setTranslateX(0);
            c.setTranslateY(0);
            c.setOpacity(1);
        });
    }

    private void remove(PlaylistItemView item) {
        playlistItemViews.remove(item);
        IntStream.range(0, playlistItemViews.size()).forEach(i -> playlistItemViews.get(i).setIndex(i + 1));
    }

    protected abstract String getTarget();

    protected abstract Opinion getOpinion();

    protected abstract void moveFile(PlaylistItemView itemView) throws IOException;
}
