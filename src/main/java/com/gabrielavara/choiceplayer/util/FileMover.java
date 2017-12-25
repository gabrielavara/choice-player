package com.gabrielavara.choiceplayer.util;

import java.io.IOException;
import java.util.stream.IntStream;

import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileMover {
    protected static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.utils.FileMover");

    private PlaylistUtil playlistUtil;
    private ObservableList<PlaylistItemView> playlistItemViews;

    FileMover(PlaylistUtil playlistUtil, ObservableList<PlaylistItemView> playlistItemViews) {
        this.playlistUtil = playlistUtil;
        this.playlistItemViews = playlistItemViews;
    }

    public void moveFile() {
        log.info("Move file to {}", getTarget());
        playlistUtil.getCurrentlyPlayingPlaylistItemView().ifPresent(item -> {
            playlistUtil.getNextPlaylistItemView().ifPresent(playlistUtil::select);
            playlistItemViews.removeAll(item);
            startMoveTask(item);
        });
    }

    private void startMoveTask(PlaylistItemView item) {
        Task<Void> task = createMoveTask(item);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
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
            IntStream.range(0, playlistItemViews.size()).forEach(i -> playlistItemViews.get(i).setIndex(i + 1));
            log.info("Successfully moved to {}", getTarget());
        });
        task.setOnFailed(e -> {
            playlistItemViews.add(item.getIndex() - 1, item);
            log.error("Could not move to {}: {}", getTarget(), item.getMp3());
        });
        return task;
    }

    protected abstract String getTarget();

    protected abstract void moveFile(PlaylistItemView itemView) throws IOException;
}
