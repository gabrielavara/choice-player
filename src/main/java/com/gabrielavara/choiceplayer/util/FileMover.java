package com.gabrielavara.choiceplayer.util;

import java.io.IOException;
import java.util.Comparator;
import java.util.stream.IntStream;

import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import javafx.collections.ObservableList;
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
            try {
                playlistItemViews.removeAll(item);
                moveFile(item);
                IntStream.range(0, playlistItemViews.size()).forEach(i -> playlistItemViews.get(i).setIndex(i + 1));
            } catch (IOException e) {
                playlistItemViews.add(item.getIndex() - 1, item);
                log.error("Could not move {} to {} because {}", item.getMp3(), getTarget(), e.getMessage());
                sortPlaylist();
            }
        });
    }

    protected abstract String getTarget();

    protected abstract void moveFile(PlaylistItemView itemView) throws IOException;

    private void sortPlaylist() {
        playlistItemViews.sort(Comparator.comparingInt(PlaylistItemView::getIndex));
    }
}
