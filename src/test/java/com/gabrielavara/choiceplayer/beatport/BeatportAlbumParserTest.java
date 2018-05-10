package com.gabrielavara.choiceplayer.beatport;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class BeatportAlbumParserTest {
    private BeatportAlbumParser albumParser = new BeatportAlbumParser(WebDriverProvider.getWebDriver());

    @Test
    public void shouldParse() {
        // given
        List<String> artists = asList("A.M.R", "Stefanie Pereira", "Hanski", "Airsoul", "R3dub");
        String album = "Hear Them Sing";
        String link = "http://classic.beatport.com/release/hear-them-sing/1825115";
        BeatportRelease release = new BeatportRelease(artists, album, link);

        // when
        BeatportAlbum beatportAlbum = albumParser.parse(release);

        // then
        assertEquals(artists, beatportAlbum.getArtists());
        assertEquals(album, beatportAlbum.getTitle());
        assertEquals("http://geo-media.beatport.com/image_size/500x500/23a3bd9e-1324-446b-9cd9-cb6b5267eeeb.jpg", beatportAlbum.getAlbumArtUrl());
        assertEquals(10, beatportAlbum.getTracks().size());
        assertEquals("2016-09-05", beatportAlbum.getReleaseDate());
        assertEquals("Alter Ego Progressive", beatportAlbum.getLabel());
        assertEquals("AEP227", beatportAlbum.getCatalog());

        assertEquals("1", beatportAlbum.getTracks().get(0).getTrackNumber());
        assertEquals("Hear Them Sing", beatportAlbum.getTracks().get(0).getTitle());
        assertEquals("Original Mix", beatportAlbum.getTracks().get(0).getMix());
        assertEquals(asList("A.M.R", "Stefanie Pereira"), beatportAlbum.getTracks().get(0).getArtists());
        assertEquals(singletonList("Trance"), beatportAlbum.getTracks().get(0).getGenres());
        assertEquals(132, beatportAlbum.getTracks().get(0).getBpm());
    }

    @Test
    public void testGetBpm() {
        // when
        int bpm = BeatportAlbumParser.getBpm("6:44 / 128 BPM");

        // then
        assertEquals(128, bpm);
    }

    @Test
    public void testGetLength() {
        // when
        int length = BeatportAlbumParser.getLength("6:44 / 128 BPM");

        // then
        assertEquals(6 * 60 + 44, length);
    }
}