package com.gabrielavara.choiceplayer.beatport;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.gabrielavara.choiceplayer.dto.Mp3;

public class BeatportSearcherTest {
    private BeatportSearcher beatportSearcher = new BeatportSearcher();

    @Test
    public void testSearch() {
        // given
        Mp3 mp3 = new Mp3();
        mp3.setAlbum("Amun / Meta");
        mp3.setArtist("Ilan Bluestone & Jason Ross");

        // when
        Optional<BeatportAlbum> album = beatportSearcher.search(mp3);

        // then
        assertTrue(album.isPresent());
        assertEquals(getExpectedAlbum(), album.get());
    }

    private BeatportAlbum getExpectedAlbum() {
        BeatportAlbum expected = new BeatportAlbum();
        expected.setTitle("Amun / Meta");
        List<String> expectedArtists = asList("Ilan Bluestone", "Jason Ross");
        expected.setArtists(expectedArtists);
        expected.setAlbumArtUrl("http://geo-media.beatport.com/image_size/500x500/623b61e9-ad08-4576-a5b1-767794242b92.jpg");
        expected.setReleaseDate("2016-08-19");
        expected.setLabel("Anjunabeats");
        expected.setCatalog("ANJ395D");

        BeatportTrack track1 = new BeatportTrack("1", expectedArtists, "Amun", "Original Mix", singletonList("Trance"), 128, 404);
        BeatportTrack track2 = new BeatportTrack("2", expectedArtists, "Meta", "Original Mix", singletonList("Trance"), 128, 417);
        expected.setTracks(asList(track1, track2));

        return expected;
    }

}