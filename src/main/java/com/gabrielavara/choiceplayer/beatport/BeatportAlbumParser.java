package com.gabrielavara.choiceplayer.beatport;

import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.text.WordUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeatportAlbumParser extends BeatportParser<BeatportRelease, BeatportAlbum> {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.beatport.BeatportAlbumParser");

    private static final String ALBUM_TITLE_XPATH = "//div[@class='line release-detail']//h2";
    private static final String ALBUM_ART_XPATH = "//div[@class='line release-detail']//span[@class='artwork']/img";
    private static final String RELEASE_DATE_XPATH = "//div[@class='line release-detail']//table[@class='meta-data']//tr[1]/td[2]";
    private static final String LABEL_XPATH = "//div[@class='line release-detail']//table[@class='meta-data']//tr[2]/td[2]";
    private static final String CATALOG_XPATH = "//div[@class='line release-detail']//table[@class='meta-data']//tr[3]/td[2]";

    private static final String TRACK_COUNT_XPATH = "//table[@class='track-grid track-grid-release']//tr";
    private static final String NUMBER_XPATH = "//table[@class=''track-grid track-grid-release'']//tr[{0}]/td[@class=''playColumn'']/span[contains(@class, ''txt-grey'')]";
    private static final String TITLE_XPATH = "//table[@class=''track-grid track-grid-release'']//tr[{0}]/td[@class=''titleColumn'']/a/span[1]";
    private static final String MIX_XPATH = "//table[@class=''track-grid track-grid-release'']//tr[{0}]/td[@class=''titleColumn'']/a/span[2]";
    private static final String BPM_XPATH = "//table[@class=''track-grid track-grid-release'']//tr[{0}]/td[3]/span[1]";
    private static final String ARTISTS_XPATH = "//table[@class=''track-grid track-grid-release'']//tr[{0}]/td[@class=''titleColumn'']/span[@class=''artistList'']/a";
    private static final String GENRES_XPATH = "//table[@class=''track-grid track-grid-release'']//tr[{0}]//span[@class=''genreList'']/a";

    BeatportAlbumParser(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String getUrl(BeatportRelease beatportRelease) {
        log.info("Get album: {}", beatportRelease);
        return beatportRelease.getLink();
    }

    @Override
    protected BeatportAlbum parseDocument(WebDriver driver) {
        BeatportAlbum beatportAlbum = new BeatportAlbum();

        WebElement albumElement = driver.findElement(By.xpath(ALBUM_TITLE_XPATH));
        String album = WordUtils.capitalizeFully(albumElement.getText());
        beatportAlbum.setTitle(album);

        WebElement albumArtElement = driver.findElement(By.xpath(ALBUM_ART_XPATH));
        String albumArtUrl = albumArtElement.getAttribute("src").replace("212x212", "500x500");
        beatportAlbum.setAlbumArtUrl(albumArtUrl);

        WebElement releaseDateElement = driver.findElement(By.xpath(RELEASE_DATE_XPATH));
        String releaseDate = releaseDateElement.getText();
        beatportAlbum.setReleaseDate(releaseDate);

        WebElement labelElement = driver.findElement(By.xpath(LABEL_XPATH));
        String label = labelElement.getText();
        beatportAlbum.setLabel(label);

        WebElement catalogElement = driver.findElement(By.xpath(CATALOG_XPATH));
        String catalog = catalogElement.getText();
        beatportAlbum.setCatalog(catalog);

        Set<String> albumArtists = new LinkedHashSet<>();
        addTracks(driver, beatportAlbum, albumArtists);
        beatportAlbum.setArtists(new ArrayList<>(albumArtists));

        log.info("Album parsed: {}", beatportAlbum);
        return beatportAlbum;
    }

    private void addTracks(WebDriver driver, BeatportAlbum beatportAlbum, Set<String> albumArtists) {
        int trackCount = driver.findElements(By.xpath(TRACK_COUNT_XPATH)).size();
        for (int i = 0; i < trackCount; i++) {
            BeatportTrack beatportTrack = parseTrack(driver, i + 1, albumArtists);
            beatportAlbum.addTrack(beatportTrack);
        }
    }

    private BeatportTrack parseTrack(WebDriver driver, int i, Set<String> albumArtists) {
        WebElement numberElement = driver.findElement(By.xpath(format(NUMBER_XPATH, i)));
        String number = numberElement.getText();
        WebElement titleElement = driver.findElement(By.xpath(format(TITLE_XPATH, i)));
        String title = titleElement.getText();
        WebElement mixElement = driver.findElement(By.xpath(format(MIX_XPATH, i)));
        String mix = mixElement.getText();

        WebElement lengthAndBpmElement = driver.findElement(By.xpath(format(BPM_XPATH, i)));
        int bpm = getBpm(lengthAndBpmElement.getText());
        int length = getLength(lengthAndBpmElement.getText());

        List<WebElement> artistElements = driver.findElements(By.xpath(format(ARTISTS_XPATH, i)));
        List<String> artists = getTexts(artistElements);
        albumArtists.addAll(artists);
        List<WebElement> genreElements = driver.findElements(By.xpath(format(GENRES_XPATH, i)));
        List<String> genres = getTexts(genreElements);

        return new BeatportTrack(number, artists, title, mix, genres, bpm, length);
    }

    static int getBpm(String lengthAndBpm) {
        int i1 = lengthAndBpm.indexOf('/');
        int i2 = lengthAndBpm.indexOf("BPM");
        return Integer.parseInt(lengthAndBpm.substring(i1 + 2, i2 - 1));
    }

    static int getLength(String lengthAndBpm) {
        int index = lengthAndBpm.indexOf('/');
        String length = lengthAndBpm.substring(0, index - 1);
        String[] parts = length.split(":");
        int multiplier = 1;
        int sumSeconds = 0;
        for (int i = parts.length - 1; i >= 0; i--) {
            sumSeconds += multiplier * Integer.parseInt(parts[i]);
            multiplier *= 60;
        }
        return sumSeconds;
    }
}
