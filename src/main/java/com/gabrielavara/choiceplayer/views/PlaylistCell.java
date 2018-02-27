package com.gabrielavara.choiceplayer.views;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.controls.playlistitem.PlaylistItem;
import com.gabrielavara.choiceplayer.dto.Mp3;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.ListCell;
import lombok.Getter;

public class PlaylistCell extends ListCell<PlaylistItemView> {
    protected static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.views.PlaylistCell");

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10, r -> {
        Thread t = new Thread(r);
        t.setName("Album art loader thread " + t.getId());
        t.setDaemon(true);
        return t;
    });

    @Getter
    private PlaylistItem playlistItem;
    @Getter
    private PlaylistItemView playlistItemView;

    private ChangeListener<Boolean> changedPropertyListener = (ov, oldValue, newValue) -> changed(newValue);

    private void changed(Boolean value) {
        if (value && playlistItem != null && playlistItemView != null) {
            Mp3 mp3 = playlistItemView.getMp3();
            playlistItem.setArtist(mp3.getArtist());
            playlistItem.setTitle(mp3.getTitle());
            loadAlbumArt(playlistItemView);
        }
    }

    @Override
    protected void updateItem(PlaylistItemView item, boolean empty) {
        if (playlistItemView != null && !playlistItemView.equals(item)) {
            playlistItemView.getMp3().getChanged().removeListener(changedPropertyListener);
        }
        super.updateItem(item, empty);
        if (playlistItemView != null) {
            playlistItemView.getMp3().getChanged().addListener(changedPropertyListener);
        }
        playlistItemView = item;
        if (empty) {
            setGraphic(null);
        } else {
            playlistItem = createPlaylistItem(item);
            setGraphic(playlistItem);
            playlistItem.setState(playlistItemView.getMp3().isCurrentlyPlaying());
            loadAlbumArt(item);
        }
    }

    private PlaylistItem createPlaylistItem(PlaylistItemView itemView) {
        PlaylistItem item = new PlaylistItem();
        item.setIndex(itemView.getIndexAsString());
        item.setArtist(itemView.getArtist());
        item.setTitle(itemView.getTitle());
        item.setLength(itemView.getLength());
        return item;
    }

    private void loadAlbumArt(PlaylistItemView item) {
        Mp3 mp3 = item.getMp3();
        AlbumArtLoaderTask task = new AlbumArtLoaderTask(mp3);
        task.setOnSucceeded(e -> playlistItem.getAlbumArt().setImage(task.getValue()));
        executorService.submit(task);
    }

    public void changeTheme() {
        playlistItem.changeTheme();
    }
}
