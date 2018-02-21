package com.gabrielavara.choiceplayer.beatport;

import static java.util.Comparator.comparingInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.dto.Mp3;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

public class BeatportSearcher {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.beatport.BeatportSearcher");
    private static final int MAX_DISTANCE = 10;

    private BeatportSearcher() {
    }

    public static Optional<BeatportAlbum> search(Mp3 mp3) {
        Optional<BeatportRelease> beatportRelease = getBestBeatportRelease(mp3);
        if (beatportRelease.isPresent()) {
            BeatportAlbum beatportAlbum = new BeatportAlbumParser().parse(beatportRelease.get());
            return Optional.of(beatportAlbum);
        } else {
            return Optional.empty();
        }
    }

    private static Optional<BeatportRelease> getBestBeatportRelease(Mp3 mp3) {
        List<BeatportReleaseDistance> distances = new ArrayList<>();

        BeatportReleases releases = new BeatportSearchResultParser().parse(mp3);

        releases.getReleases().forEach(r -> {
            int albumDistance = LevenshteinDistance.calculate(mp3.getAlbum(), r.getAlbum());
            List<String> sanitizedArtists = r.getArtists().stream().map(BeatportSearcher::sanitizeArtist).collect(Collectors.toList());
            Optional<Integer> minArtistDistance = Collections2.permutations(sanitizedArtists).stream()
                    .map(artists -> LevenshteinDistance.calculate(sanitizeArtist(mp3.getArtist()), joinArtists(artists)))
                    .min(comparingInt(i -> i));
            minArtistDistance.ifPresent(artistDistance -> distances.add(new BeatportReleaseDistance(albumDistance, artistDistance)));
        });

        Optional<BeatportReleaseDistance> min = distances.stream().min(comparingInt(BeatportReleaseDistance::getDistanceSum));
        if (min.isPresent()) {
            return getBestRelease(distances, releases, min.get());
        } else {
            log.info("No minimum present");
            return Optional.empty();
        }
    }

    private static Optional<BeatportRelease> getBestRelease(List<BeatportReleaseDistance> distances, BeatportReleases releases, BeatportReleaseDistance minDistance) {
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

    private static String sanitizeArtist(String artist) {
        String result = artist.replaceAll(" [fF]eat.? ", " ");
        result = result.replaceAll(" [fF]t.? ", " ");
        result = result.replaceAll(" [wW]ith ", " ");
        result = result.replaceAll(" [pP]res.? ", " ");
        result = result.replaceAll(" [aA]nd ", " ");
        result = result.replaceAll(" & ", " ");
        result = result.replaceAll(", ", " ");
        return result;
    }

    private static String joinArtists(List<String> artists) {
        return Joiner.on(" ").join(artists);
    }
}
