package com.gabrielavara.choiceplayer.beatport;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.gabrielavara.choiceplayer.dto.Mp3;

public class BeatportReleaseSearcherTest {
    private static final String ARTIST = "Ilan Bluestone & Jason Ross";
    private static final String ALBUM = "Amun / Meta";
    private static final String LINK = "http://classic.beatport.com/release/amun-meta/1831545";

    private Mp3 mp3 = new Mp3();
    private BeatportReleaseSearcher beatportReleaseSearcher = new BeatportReleaseSearcher();

    @Test
    public void shouldSearch() {
        // given
        mp3.setArtist(ARTIST);
        mp3.setAlbum(ALBUM);

        // when
        BeatportReleases results = beatportReleaseSearcher.search(mp3);

        // then
        assertEquals(1, results.getReleases().size());
        BeatportRelease expected = new BeatportRelease(Arrays.asList("Ilan Bluestone", "Jason Ross"), ALBUM, LINK);
        assertEquals(expected, results.getReleases().get(0));
    }

    @Test
    public void shouldRemoveRoundBrackets() {
        // given
        mp3.setAlbum("Album (Round Brackets)");

        // when
        String albumForSearch = beatportReleaseSearcher.getAlbumForSearch(mp3);

        // then
        assertEquals("Album", albumForSearch);
    }

    @Test
    public void shouldRemoveSquareBrackets() {
        // given
        mp3.setAlbum("Album [Square Brackets]");

        // when
        String albumForSearch = beatportReleaseSearcher.getAlbumForSearch(mp3);

        // then
        assertEquals("Album", albumForSearch);
    }

    @Test
    public void shouldRemoveBrackets() {
        // given
        mp3.setAlbum("Album (Round Brackets) [Square Brackets]");

        // when
        String albumForSearch = beatportReleaseSearcher.getAlbumForSearch(mp3);

        // then
        assertEquals("Album", albumForSearch);
    }
}