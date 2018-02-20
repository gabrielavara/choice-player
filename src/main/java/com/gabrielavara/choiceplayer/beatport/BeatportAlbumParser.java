package com.gabrielavara.choiceplayer.beatport;

import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class BeatportAlbumParser extends BeatportParser<BeatportRelease, BeatportAlbum> {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.beatport.BeatportAlbumParser");

    private static final String ALBUM_TITLE_XPATH = "//div[@class='line release-detail']//h2";

    private static final String TRACK_COUNT_XPATH = "count(//table[@class='track-grid track-grid-release']//tr)";
    private static final String NUMBER_XPATH = "//table[@class=''track-grid track-grid-release'']//tr[{0}]/td[@class=''playColumn'']/span[contains(@class, ''txt-grey'')]";
    private static final String TITLE_XPATH = "//table[@class=''track-grid track-grid-release'']//tr[{0}]/td[@class=''titleColumn'']/a/span[1]";
    private static final String MIX_XPATH = "//table[@class=''track-grid track-grid-release'']//tr[{0}]/td[@class=''titleColumn'']/a/span[2]";
    private static final String ARTISTS_XPATH = "//table[@class=''track-grid track-grid-release'']//tr[{0}]/td[@class=''titleColumn'']/span[@class=''artistList'']/a";

    @Override
    protected String getUrl(BeatportRelease beatportRelease) {
        log.info("Get album: {}", beatportRelease);
        return beatportRelease.getLink();
    }

    @Override
    protected BeatportAlbum parseDocument(HtmlPage page) {
        BeatportAlbum beatportAlbum = new BeatportAlbum();

        HtmlElement albumElement = page.getFirstByXPath(ALBUM_TITLE_XPATH);
        String album = WordUtils.capitalizeFully(getText(albumElement));
        Set<String> albumArtists = new LinkedHashSet<>();

        beatportAlbum.setTitle(album);

        int trackCount = page.<Double>getFirstByXPath(TRACK_COUNT_XPATH).intValue();

        for (int i = 0; i < trackCount; i++) {
            BeatportTrack beatportTrack = parseTrack(page, i + 1, albumArtists);
            beatportAlbum.addTrack(beatportTrack);
        }
        beatportAlbum.setArtists(new ArrayList<>(albumArtists));
        log.info("Album parsed: {}", beatportAlbum);
        return beatportAlbum;
    }

    private BeatportTrack parseTrack(HtmlPage page, int i, Set<String> albumArtists) {
        HtmlElement numberElement = page.getFirstByXPath(format(NUMBER_XPATH, i));
        String number = getText(numberElement);
        HtmlElement titleElement = page.getFirstByXPath(format(TITLE_XPATH, i));
        String title = getText(titleElement);
        HtmlElement mixElement = page.getFirstByXPath(format(MIX_XPATH, i));
        String mix = getText(mixElement);
        List<HtmlElement> artistElements = page.getByXPath(format(ARTISTS_XPATH, i));
        List<String> artists = getTexts(artistElements);
        albumArtists.addAll(artists);

        return new BeatportTrack(number, artists, title, mix);
    }
}
