package com.gabrielavara.choiceplayer.views;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.controls.playlistitem.PlaylistItem;
import javafx.animation.PauseTransition;
import javafx.scene.control.ListCell;
import javafx.util.Duration;
import lombok.Getter;

public class PlaylistCell extends ListCell<PlaylistItemView> {
    private static Random random = new Random();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    @Getter
    private PlaylistItem playlistItem;
    @Getter
    private PlaylistItemView playlistItemView;

    @Override
    protected void updateItem(PlaylistItemView item, boolean empty) {
        super.updateItem(item, empty);
        playlistItemView = item;
        setText(null);
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
        PauseTransition wait = new PauseTransition(Duration.millis(random.nextInt(200)));
        wait.setOnFinished(ev -> {
            Mp3 mp3 = item.getMp3();
            AlbumArtLoaderTask task = new AlbumArtLoaderTask(mp3);
            task.setOnSucceeded(e -> playlistItem.getAlbumArt().setImage(task.getValue()));
            executorService.submit(task);
        });
        wait.play();
    }
}
