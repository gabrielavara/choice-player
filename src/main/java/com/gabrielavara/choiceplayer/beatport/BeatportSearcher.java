package com.gabrielavara.choiceplayer.beatport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.dto.Mp3;

public class BeatportSearcher {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.beatport.BeatportSearcher");
    private static final int MAX_DISTANCE = 15;

    private WebDriver driver;

    static {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver-2.35.exe");
    }

    BeatportSearcher() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        options.addArguments("window-size=1200x600");
        driver = new ChromeDriver(options);
    }

    public Optional<BeatportAlbum> search(Mp3 mp3) {
        Optional<BeatportRelease> beatportRelease = getBestBeatportRelease(mp3);
        if (beatportRelease.isPresent()) {
            BeatportAlbum beatportAlbum = new BeatportAlbumParser(driver).parse(beatportRelease.get());
            return Optional.of(beatportAlbum);
        } else {
            log.info("Not found\n");
            return Optional.empty();
        }
    }

    private Optional<BeatportRelease> getBestBeatportRelease(Mp3 mp3) {
        BeatportReleases releases = new BeatportSearchResultParser(driver).parse(mp3);
        List<Integer> albumDistances = new ArrayList<>();
        String album = BeatportSearchResultParser.getAlbumForSearch(mp3);
        releases.getReleases().forEach(r -> albumDistances.add(LevenshteinDistance.calculate(album, r.getAlbum())));
        Optional<Integer> min = albumDistances.stream().min(Comparator.comparingInt(i -> i));

        if (min.isPresent()) {
            int index = albumDistances.indexOf(min.get());
            log.info("Best release index: {}", index);
            BeatportRelease release = releases.getReleases().get(index);
            return min.get() < MAX_DISTANCE ? Optional.of(release) : Optional.empty();
        } else {
            log.info("Use first release");
            BeatportRelease release = releases.getReleases().get(0);
            return albumDistances.get(0) < MAX_DISTANCE ? Optional.of(release) : Optional.empty();
        }
    }
}
