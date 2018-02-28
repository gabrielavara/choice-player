package com.gabrielavara.choiceplayer.beatport;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toList;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.dto.Mp3;
import com.google.common.base.Joiner;

public class BeatportSearchResultParser extends BeatportParser<Mp3, BeatportReleases> {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.beatport.BeatportSearchResultParser");

    private static final String SEARCH_URL = BEATPORT_COM + "/search?query={0}&facets[0]=fieldType%3Arelease&perPage=10";

    private static final String RELEASES_XPATH = "//div[@class='search-component']//ul[contains(@class,'tile-list-items')]";
    private static final String RELEASE_TITLES_XPATH = RELEASES_XPATH + "//a[@class='item-title']";
    private static final String RELEASE_ARTISTS_XPATH = RELEASES_XPATH.replaceAll("'", "''") + "//a[@class=''item-title'' and text()={0}]/following-sibling::span[@class=''item-list'']/a";

    BeatportSearchResultParser(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String getUrl(Mp3 mp3) {
        String queryString = null;
        try {
            String artist = sanitizeArtist(mp3.getArtist());
            String album = getAlbumForSearch(mp3);
            String artistAndAlbum = artist + " " + album;
            log.info("Search on Beatport for: {}", artistAndAlbum);
            queryString = URLEncoder.encode(artistAndAlbum, UTF_8).toLowerCase();
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        String url = format(SEARCH_URL, queryString);
        log.info("Search url: {}", url);
        return url;
    }

    private static String sanitizeArtist(String artist) {
        String result = artist;
        for (Pattern pattern : RegexPattern.getAll()) {
            result = pattern.matcher(result).replaceAll(" ");
        }
        return result;
    }

    static String getAlbumForSearch(Mp3 mp3) {
        String album = mp3.getAlbum();
        album = album.replaceAll("_", " ");
        int roundIndex = album.indexOf('(');
        int squareIndex = album.indexOf('[');
        int index = roundIndex > 0 && squareIndex > 0 ? Math.min(roundIndex, squareIndex) : Math.max(roundIndex, squareIndex);
        String result = index > 0 ? album.substring(0, index) : album;
        return result.replaceAll("-", " ").trim();
    }

    @Override
    protected BeatportReleases parseDocument(WebDriver driver) {
        List<BeatportRelease> results = new ArrayList<>();

        List<WebElement> titleElements = driver.findElements(By.xpath(RELEASE_TITLES_XPATH));
        List<String> texts = getTexts(titleElements);

        List<List<String>> artists = texts.stream().map(t -> driver.findElements(By.xpath(getArtistsXpath(t))).stream().limit(5).collect(toList()))
                .map(this::getTexts).collect(toList());

        List<String> links = getHrefs(titleElements);

        for (int i = 0; i < texts.size(); i++) {
            BeatportRelease beatportRelease = new BeatportRelease(artists.get(i), texts.get(i), links.get(i));
            results.add(beatportRelease);
        }
        log.info("Releases found: {}", results);
        return new BeatportReleases(results);
    }

    private String getArtistsXpath(String t) {
        String[] split = t.split("\"");
        if (split.length == 1) {
            String s = "\"" + split[0] + "\"";
            return format(RELEASE_ARTISTS_XPATH, s);
        } else {
            String join = Joiner.on("','\"','").join(split);
            String s = "concat('" + join + "')";
            return format(RELEASE_ARTISTS_XPATH, s);
        }
    }

}
