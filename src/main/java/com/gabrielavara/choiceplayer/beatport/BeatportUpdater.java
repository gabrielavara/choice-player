package com.gabrielavara.choiceplayer.beatport;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.List;
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
            Optional<BeatportTrack> track = getBestTrack(mp3, album);
            track.ifPresent(t -> update(mp3, t));
        });
    }

    private Optional<BeatportTrack> getBestTrack(Mp3 mp3, BeatportAlbum album) {
        List<Integer> distances = album.getTracks().stream().map(t -> {
            String title = t.getTitle() + " (" + t.getMix() + ")";
            return LevenshteinDistance.calculate(title, mp3.getTitle());
        }).collect(toList());

        Optional<Integer> minDistance = distances.stream().min(Integer::compareTo);
        if (minDistance.isPresent()) {
            int index = distances.indexOf(minDistance.get());
            return Optional.of(album.getTracks().get(index));
        } else {
            return Optional.empty();
        }
    }

    private void update(Mp3 mp3, BeatportTrack track) {
        String newArtist = getArtist(mp3, track);
        mp3.setArtist(newArtist);
    }

    private String getArtist(Mp3 mp3, BeatportTrack track) {
        String artist = mp3.getArtist();
        List<String> splitted = asList(artist.split(" "));
        List<String> artists = track.getArtists();

        List<String> previous = artists.stream().map(a -> {
            int i = splitted.indexOf(a);
            if (i > 0) {
                return splitted.get(i - 1);
            } else if (i == 0) {
                return "";
            }
            return " ";
        }).collect(toList());

        StringBuilder stringBuilder = new StringBuilder();
        artists.forEach(a -> stringBuilder.append(previous).append(a));
        return stringBuilder.toString();
    }
}
