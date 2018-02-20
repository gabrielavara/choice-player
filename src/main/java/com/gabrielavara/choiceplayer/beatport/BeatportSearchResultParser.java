package com.gabrielavara.choiceplayer.beatport;

import static java.text.MessageFormat.format;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class BeatportSearchResultParser extends BeatportParser<Mp3, BeatportReleases> {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.beatport.BeatportSearchResultParser");

    private static final String SEARCH_URL = BEATPORT_COM + "/search?query={0}";

    private static final String RELEASES_XPATH = "//div[@class='search-results']//ul[contains(@class,'tile-list-items')]";
    private static final String RELEASE_TITLES_XPATH = RELEASES_XPATH + "//a[@class='item-title']";
    private static final String RELEASE_ARTISTS_XPATH = RELEASES_XPATH.replaceAll("'", "''") + "//a[@class=''item-title'' and text() = ''{0}'']/following-sibling::span[@class=''item-list'']/a";

    @Override
    protected String getUrl(Mp3 mp3) {
        String queryString = null;
        try {
            String artist = sanitize(mp3.getArtist());
            String album = sanitize(getAlbumForSearch(mp3));
            String artistAndAlbum = artist + " " + album;
            log.info("Search on Beatport for: {}", artistAndAlbum);
            queryString = URLEncoder.encode(artistAndAlbum, UTF_8).toLowerCase();
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return format(SEARCH_URL, queryString);
    }

    String getAlbumForSearch(Mp3 mp3) {
        String album = mp3.getAlbum();
        int roundIndex = album.indexOf('(');
        int squareIndex = album.indexOf('[');
        int index = roundIndex > 0 && squareIndex > 0 ? Math.min(roundIndex, squareIndex) : Math.max(roundIndex, squareIndex);
        return index > 0 ? album.substring(0, index).trim() : album;
    }

    @Override
    protected BeatportReleases parseDocument(HtmlPage page) {
        List<BeatportRelease> results = new ArrayList<>();

        List<HtmlElement> titleElements = page.getByXPath(RELEASE_TITLES_XPATH);
        List<String> texts = getTexts(titleElements);

        List<List<String>> artists = texts.stream().map(t -> page.<HtmlElement>getByXPath(format(RELEASE_ARTISTS_XPATH, t)))
                .map(this::getTexts).collect(Collectors.toList());

        List<String> links = getHrefs(titleElements);

        for (int i = 0; i < texts.size(); i++) {
            BeatportRelease beatportRelease = new BeatportRelease(artists.get(i), texts.get(i), links.get(i));
            results.add(beatportRelease);
        }
        log.info("Releases found: {}", results);
        return new BeatportReleases(results);
    }

}
