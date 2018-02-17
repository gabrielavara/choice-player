package com.gabrielavara.choiceplayer.playlist;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.gabrielavara.choiceplayer.api.service.PlaylistTestInitializer;
import com.gabrielavara.choiceplayer.dto.Mp3;

public class PlaylistLoaderTest extends PlaylistTestInitializer {
    private final static Path testResourcesFolder = Paths.get("src/test/resources/mp3");

    @Test
    public void testLoad() {
        // given
        PlaylistLoader playlistLoader = new PlaylistLoader();

        // when
        List<Mp3> mp3Files = playlistLoader.load(testResourcesFolder);

        // then
        assertEquals(4, mp3Files.size());
        assertEquals("Test Artist", mp3Files.get(0).getArtist());
        assertEquals("Test Older Title", mp3Files.get(0).getTitle());
        assertEquals("1", mp3Files.get(0).getTrack());

        assertEquals("Artist 2", mp3Files.get(1).getArtist());
        assertEquals("Track 1", mp3Files.get(1).getTitle());
        assertEquals("1", mp3Files.get(1).getTrack());

        assertEquals("Artist 1", mp3Files.get(2).getArtist());
        assertEquals("Track 2", mp3Files.get(2).getTitle());
        assertEquals("2", mp3Files.get(2).getTrack());

        assertEquals("The Super Artist Test", mp3Files.get(3).getArtist());
        assertEquals("Test Newer Title", mp3Files.get(3).getTitle());
        assertEquals("1", mp3Files.get(3).getTrack());
    }
}