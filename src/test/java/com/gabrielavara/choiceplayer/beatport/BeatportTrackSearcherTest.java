package com.gabrielavara.choiceplayer.beatport;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class BeatportTrackSearcherTest {
    private BeatportTrackSearcher trackSearcher = new BeatportTrackSearcher();

    @Test
    public void shouldSearch() {
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
    }
}