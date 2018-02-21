package com.gabrielavara.choiceplayer.beatport;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

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
            track.ifPresent(t -> update(mp3, t, album.getTracks().size()));
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

    private void update(Mp3 mp3, BeatportTrack track, int trackCount) {
        String artist = getArtist(mp3);
        mp3.setArtist(artist);
        mp3.setTrack(getTrackString(track.getTrackNumber()) + "/" + getTrackString(trackCount));
    }

    private String getTrackString(int num) {
        return num < 10 ? "0" + num : "" + num;
    }

    private String getTrackString(String num) {
        return num.length() < 2 ? "0" + num : num;
    }

    private String getArtist(Mp3 mp3) {
        String artist = mp3.getArtist();
        for (RegexPattern regexPattern : RegexPattern.values()) {
            for (Pattern pattern : regexPattern.getPatterns()) {
                artist = pattern.matcher(artist).replaceAll(regexPattern.getReplaceWith());
            }
        }
        return artist;
    }

}
