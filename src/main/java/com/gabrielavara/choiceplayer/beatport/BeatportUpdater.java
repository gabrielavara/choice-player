package com.gabrielavara.choiceplayer.beatport;

import java.util.Optional;

import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class BeatportUpdater {
    private ObservableList<PlaylistItemView> playlistItems;
    private Task<Void> updaterTask;

    public BeatportUpdater(ObservableList<PlaylistItemView> playlistItems) {
        this.playlistItems = playlistItems;
    }

    public void update() {
        if (updaterTask != null) {
            updaterTask.cancel();
        }

        updaterTask = createUpdaterTask();
        new Thread(updaterTask).start();
    }

    private Task<Void> createUpdaterTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                playlistItems.forEach(pi -> {
                    Mp3 mp3 = pi.getMp3();
                    update(mp3);
                });
                return null;
            }
        };
    }

    private void update(Mp3 mp3) {
        Optional<BeatportAlbum> beatportAlbum = BeatportSearcher.search(mp3);
        beatportAlbum.ifPresent(album -> {
        });
    }
}
