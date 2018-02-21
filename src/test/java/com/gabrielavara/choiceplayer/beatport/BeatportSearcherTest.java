package com.gabrielavara.choiceplayer.beatport;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.gabrielavara.choiceplayer.dto.Mp3;

public class BeatportSearcherTest {
    @Test
    public void testSearch() {
        // given
        Mp3 mp3 = new Mp3();
        mp3.setAlbum("Amun / Meta");
        mp3.setArtist("Ilan Bluestone & Jason Ross");

        // when
        Optional<BeatportAlbum> album = BeatportSearcher.search(mp3);

        // then
        assertTrue(album.isPresent());
        assertEquals(getExpectedAlbum(), album.get());
    }

    private BeatportAlbum getExpectedAlbum() {
        BeatportAlbum expected = new BeatportAlbum();
        expected.setTitle("Amun / Meta");
        List<String> expectedArtists = asList("Ilan Bluestone", "Jason Ross");
        expected.setArtists(expectedArtists);
        BeatportTrack track1 = new BeatportTrack("1", expectedArtists, "Amun", "Original Mix");
        BeatportTrack track2 = new BeatportTrack("2", expectedArtists, "Meta", "Original Mix");
        expected.setTracks(asList(track1, track2));
        return expected;
    }

}