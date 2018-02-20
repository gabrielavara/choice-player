package com.gabrielavara.choiceplayer.beatport;

import static java.util.Comparator.comparingInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.dto.Mp3;
import com.google.common.base.Joiner;

public class BeatportSearcher {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.beatport.BeatportSearcher");
    private static final int MAX_DISTANCE = 10;

    public Optional<BeatportAlbum> search(Mp3 mp3) {
        Optional<BeatportRelease> beatportRelease = getBestBeatportRelease(mp3);
        if (beatportRelease.isPresent()) {
            BeatportAlbum beatportAlbum = new BeatportAlbumParser().parse(beatportRelease.get());
            return Optional.of(beatportAlbum);
        } else {
            return Optional.empty();
        }
    }

    private Optional<BeatportRelease> getBestBeatportRelease(Mp3 mp3) {
        List<BeatportReleaseDistance> distances = new ArrayList<>();

        BeatportReleases releases = new BeatportSearchResultParser().parse(mp3);

        releases.getReleases().forEach(r -> {
            int albumDistance = LevenshteinDistance.calculate(mp3.getAlbum(), r.getAlbum());
            int artistDistance = LevenshteinDistance.calculate(sanitizeArtist(mp3.getArtist()), joinArtists(r.getArtists()));
            distances.add(new BeatportReleaseDistance(albumDistance, artistDistance));
        });

        Optional<BeatportReleaseDistance> min = distances.stream().min(comparingInt(BeatportReleaseDistance::getDistanceSum));
        if (min.isPresent()) {
            return getBestRelease(distances, releases, min.get());
        } else {
            log.info("No minimum present");
            return Optional.empty();
        }
    }

    private Optional<BeatportRelease> getBestRelease(List<BeatportReleaseDistance> distances, BeatportReleases releases, BeatportReleaseDistance minDistance) {
        if (minDistance.getDistanceSum() < MAX_DISTANCE) {
            int minIndex = distances.indexOf(minDistance);
            BeatportRelease release = releases.getReleases().get(minIndex);
            log.info("Minimum found: {}", release);
            return Optional.of(release);
        } else {
            log.info("Minimum too far");
            return Optional.empty();
        }
    }

    private String sanitizeArtist(String artist) {
        String result = artist.replaceAll(" feat. ", " ");
        result = result.replaceAll(" ft. ", " ");
        result = result.replaceAll(" with ", " ");
        result = result.replaceAll(" & ", " ");
        return result;
    }

    private String joinArtists(List<String> artists) {
        return Joiner.on(" ").join(artists);
    }
}
