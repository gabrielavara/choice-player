package com.gabrielavara.choiceplayer.playlist;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

class PlaylistCacheTestUtil {
    private static final String OLDER_MP3 = "src/test/resources/mp3/testOlder.mp3";
    private static final String PLAYLIST_CACHE = "playlistCache.json";

    static List<PlaylistItemView> createPlaylist() throws IOException, UnsupportedTagException, InvalidDataException {
        List<PlaylistItemView> playlist = new ArrayList<>();
        playlist.add(new PlaylistItemView(1, new Mp3(new Mp3File(Paths.get(OLDER_MP3)))));
        return playlist;
    }

    static void deleteCacheFile() throws IOException {
        Path cacheFilePath = Paths.get(PLAYLIST_CACHE);
        if (cacheFilePath.toFile().exists()) {
            Files.delete(cacheFilePath);
        }
    }
}
