package com.gabrielavara.musicplayer.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.mpatric.mp3agic.Mp3File;

public class PlaylistLoaderTest {
    private final Path path = Paths.get("src/test/resources/mp3folder");

    @Test
    public void testLoad() throws IOException {
        // given
        PlaylistLoader playlistLoader = new PlaylistLoader();

        // when
        List<Mp3File> mp3Files = playlistLoader.load(path);

        // then
        assertEquals(mp3Files.size(), 1);
        assertEquals(mp3Files.get(0).getId3v1Tag().getArtist(), "Me");
        assertEquals(mp3Files.get(0).getId3v1Tag().getTrack(), "1");
    }
}