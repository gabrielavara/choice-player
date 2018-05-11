package com.gabrielavara.choiceplayer.beatport;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gabrielavara.choiceplayer.util.TimeFormatter;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import com.google.common.base.Joiner;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class BeatportUpdater {
    private static final int MAX_DISTANCE = 10;
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.beatport.BeatportUpdater");

    private ObservableList<PlaylistItemView> playlistItems;
    private Task<Void> updaterTask;
    private Thread thread;
    private BeatportSearcher beatportSearcher = new BeatportSearcher();

    public BeatportUpdater(ObservableList<PlaylistItemView> playlistItems) {
        this.playlistItems = playlistItems;
    }

    public void update() {
        if (updaterTask != null) {
            log.info("Cancel task");
            updaterTask.cancel();
            thread.interrupt();
        }

        log.info("Start task");
        updaterTask = createUpdaterTask();
        thread = new Thread(updaterTask);
        thread.setDaemon(true);
        thread.start();
    }

    private Task<Void> createUpdaterTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                playlistItems.forEach(pi -> {
                    try {
                        log.info("Index on playlist: {}", pi.getIndex());
                        Mp3 mp3 = pi.getMp3();
                        if (mp3.shouldSearchForInfo()) {
                            update(mp3);
                        }
                    } catch (Exception e) {
                        log.error("Exception occurred during Beatport search", e);
                    }
                });
                return null;
            }
        };
    }

    private void update(Mp3 mp3) {
        log.info("Search for: {}", mp3);
        Optional<BeatportAlbum> beatportAlbum = beatportSearcher.search(mp3);
        beatportAlbum.ifPresent(album -> {
            Optional<BeatportTrack> track = getBestTrack(mp3, album);
            track.ifPresent(t -> update(mp3, t, album));
        });
    }

    private Optional<BeatportTrack> getBestTrack(Mp3 mp3, BeatportAlbum album) {
        List<Integer> distances = getDistances(mp3, album);
        Optional<Integer> minDistance = distances.stream().min(comparingInt(i -> i));

        if (minDistance.isPresent()) {
            Integer distance = minDistance.get();
            int minIndex = distances.indexOf(distance);
            BeatportTrack track = album.getTracks().get(minIndex);
            boolean trackEquals = track.getTrackNumber().equals(String.valueOf(mp3.getTrackAsInt()));
            boolean lengthEquals = (track.getLength() + 1 >= mp3.getLength() / 1000) || (track.getLength() - 1 <= mp3.getLength() / 1000);
            String beatportLength = TimeFormatter.getFormattedLength(track.getLength());
            String mp3Length = TimeFormatter.getFormattedLength((int) (mp3.getLength() / 1000));
            log.info("Beatport track number: {}, Mp3 track number: {}, Beatport length: {}, Mp3 length: {}, Distance: {}", track.getTrackNumber(), mp3.getTrackAsInt(), beatportLength, mp3Length, distance);
            return mp3.getTrackAsInt() == 0 || (trackEquals && lengthEquals) || lengthEquals || distance < MAX_DISTANCE ? Optional.of(track) : Optional.empty();
        } else {
            return Optional.empty();
        }
    }

    private List<Integer> getDistances(Mp3 mp3, BeatportAlbum album) {
        return album.getTracks().stream().map(t -> {
            String title = t.getTitle() + " (" + t.getMix() + ")";
            return LevenshteinDistance.calculate(title, mp3.getTitle());
        }).collect(toList());
    }

    private void update(Mp3 mp3, BeatportTrack track, BeatportAlbum album) {
        String artist = getArtist(mp3, track);
        mp3.setArtist(artist);
        String title = getTitle(track) + " (" + track.getMix() + ")";
        mp3.setTitle(title);
        String trackNumber = getTrackString(track.getTrackNumber()) + "/" + getTrackString(album.getTracks().size());
        mp3.setTrack(trackNumber);
        mp3.setAlbum(album.getTitle());
        mp3.setYear(album.getReleaseDate());
        String albumArtist = Joiner.on(", ").join(album.getArtists());
        mp3.setAlbumArtist(albumArtist);
        String genre = Joiner.on(" / ").join(track.getGenres());
        mp3.setGenre(genre);
        String comment = album.getLabel() + " [" + album.getCatalog() + "]";
        mp3.setComment(comment);
        mp3.setBpm(track.getBpm());

        log.info("Artist: {}", artist);
        log.info("Title: {}", title);
        log.info("Track: {}", trackNumber);
        log.info("Album: {}", album.getTitle());
        log.info("Release date: {}", album.getReleaseDate());
        log.info("Album artist: {}", albumArtist);
        log.info("Genre: {}", genre);
        log.info("Comment: {}", comment);
        log.info("BPM: {}\n", track.getBpm());
        setAlbumArt(mp3, album);
    }

    static String getTitle(BeatportTrack track) {
        String title = track.getTitle();
        for (String artist : track.getArtists()) {
            if (title.contains(artist)) {
                int artistIndex = title.indexOf(artist);
                if (artistIndex > 0) {
                    return getWithoutArtist(title, artistIndex);
                }
            }
        }
        return title;
    }

    private static String getWithoutArtist(String title, int i) {
        String withoutArtist = title.substring(0, i - 1);
        for (Pattern p : RegexPattern.getAll()) {
            String pattern = p.pattern();
            pattern = pattern.substring(0, pattern.length() - 1);
            withoutArtist = withoutArtist.replaceAll(pattern + "$", "");
        }
        return withoutArtist;
    }

    private void setAlbumArt(Mp3 mp3, BeatportAlbum album) {
        try (InputStream is = new URL(album.getAlbumArtUrl()).openStream()) {
            byte[] imageBytes = IOUtils.toByteArray(is);
            mp3.setAlbumArtAndSaveTags(imageBytes);
        } catch (IOException e) {
            log.error("Could not load album art");
        }
    }

    private String getArtist(Mp3 mp3, BeatportTrack track) {
        String artist = mp3.getArtist();
        for (RegexPattern regexPattern : RegexPattern.values()) {
            for (Pattern pattern : regexPattern.getPatterns()) {
                if (regexPattern == RegexPattern.COMMA && track.getArtists().size() == 2) {
                    artist = pattern.matcher(artist).replaceAll(" & ");
                } else {
                    artist = pattern.matcher(artist).replaceAll(regexPattern.getReplaceWith());
                }
            }
        }
        return artist;
    }

    private String getTrackString(int num) {
        return num < 10 ? "0" + num : "" + num;
    }

    private String getTrackString(String num) {
        return num.length() < 2 ? "0" + num : num;
    }

}
