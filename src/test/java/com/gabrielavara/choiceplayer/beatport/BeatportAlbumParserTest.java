package com.gabrielavara.choiceplayer.beatport;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class BeatportAlbumParserTest {
    private BeatportAlbumParser trackSearcher = new BeatportAlbumParser();

    @Test
    public void shouldParse() {
        // given
        List<String> artists = asList("A.M.R", "Stefanie Pereira", "Hanski", "Airsoul", "R3dub");
        String album = "Hear Them Sing";
        String link = "http://classic.beatport.com/release/hear-them-sing/1825115";
        BeatportRelease release = new BeatportRelease(artists, album, link);

        // when
        BeatportAlbum beatportAlbum = trackSearcher.search(release);

        // then
        assertEquals(artists, beatportAlbum.getArtists());
        assertEquals(album, beatportAlbum.getTitle());
        assertEquals(10, beatportAlbum.getTracks().size());

        assertEquals("1", beatportAlbum.getTracks().get(0).getTrackNumber());
        assertEquals("Hear Them Sing", beatportAlbum.getTracks().get(0).getTitle());
        assertEquals("Original Mix", beatportAlbum.getTracks().get(0).getMix());
        assertEquals(asList("A.M.R", "Stefanie Pereira"), beatportAlbum.getTracks().get(0).getArtists());
    }
}