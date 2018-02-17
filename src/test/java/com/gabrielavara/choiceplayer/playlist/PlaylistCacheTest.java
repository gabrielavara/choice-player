package com.gabrielavara.choiceplayer.playlist;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class PlaylistCacheTest {
    private static final String OLDER_MP3 = "src/test/resources/mp3/testOlder.mp3";
    private static final String PLAYLIST_CACHE = "playlistCache.json";

    @Test
    public void testSaveAndLoad() throws InvalidDataException, IOException, UnsupportedTagException {
        // given
        PlaylistCache.save(createPlaylist());

        // when
        List<PlaylistItemView> playlist = PlaylistCache.load();

        // then
        assertEquals(createPlaylist(), playlist);

        // tear down
        deleteCacheFile();
    }

    private List<PlaylistItemView> createPlaylist() throws IOException, UnsupportedTagException, InvalidDataException {
        List<PlaylistItemView> playlist = new ArrayList<>();
        playlist.add(new PlaylistItemView(1, new Mp3(new Mp3File(Paths.get(OLDER_MP3)))));
        return playlist;
    }

    private void deleteCacheFile() throws IOException {
        Path cacheFilePath = Paths.get(PLAYLIST_CACHE);
        if (cacheFilePath.toFile().exists()) {
            Files.delete(cacheFilePath);
        }
    }
}