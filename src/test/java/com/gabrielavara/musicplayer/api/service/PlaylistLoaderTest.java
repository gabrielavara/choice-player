package com.gabrielavara.musicplayer.api.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

public class PlaylistLoaderTest {
    private final Path path = Paths.get("src/test/resources/mp3folder");

    @Test
    public void testLoad() throws IOException {
        // given
        PlaylistLoader playlistLoader = new PlaylistLoader();

        // when
        List<Mp3> mp3Files = playlistLoader.load(path);

        // then
        assertEquals(mp3Files.size(), 2);
        assertEquals(mp3Files.get(0).getArtist(), "Me");
        assertEquals(mp3Files.get(0).getTitle(), "Test Older");
        assertEquals(mp3Files.get(1).getArtist(), "Me");
        assertEquals(mp3Files.get(1).getTitle(), "Test Newer");
    }
}