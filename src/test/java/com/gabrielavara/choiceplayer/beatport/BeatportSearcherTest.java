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
        mp3.setAlbum("Another Chance");
        mp3.setArtist("Above & Beyond pres. Oceanlab");

        // when
        Optional<BeatportAlbum> album = BeatportSearcher.search(mp3);

        // then
        assertTrue(album.isPresent());
        assertEquals(getExpectedAlbum(), album.get());
    }

    private BeatportAlbum getExpectedAlbum() {
        BeatportAlbum expected = new BeatportAlbum();
        expected.setTitle("Another Chance");
        List<String> expectedArtists = asList("OceanLab", "Above & Beyond");
        expected.setArtists(expectedArtists);
        BeatportTrack track1 = new BeatportTrack("1", expectedArtists, "Another Chance", "Original Mix");
        BeatportTrack track2 = new BeatportTrack("2", expectedArtists, "Another Chance", "Above & Beyond Club Mix");
        BeatportTrack track3 = new BeatportTrack("3", expectedArtists, "Another Chance", "Above & Beyond Club Edit");
        expected.setTracks(asList(track1, track2, track3));
        return expected;
    }

}