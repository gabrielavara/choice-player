package com.gabrielavara.choiceplayer.api.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class CacheTest {

    private static final String OLDER_MP3 = "src/test/resources/mp3/testOlder.mp3";
    private static final String PLAYLIST_CACHE = "playlistCache.json";

    private Cache cache = new Cache();

    @Test
    public void testPutAndGet() throws InvalidDataException, IOException, UnsupportedTagException {
        // given
        cache.put(OLDER_MP3, new Mp3(new Mp3File(Paths.get(OLDER_MP3))));

        // when
        Mp3 mp3 = cache.get(OLDER_MP3);

        // then
        Mp3 expected = new Mp3(new Mp3File(Paths.get(OLDER_MP3)));
        assertEquals(expected, mp3);
    }

    @Test
    public void testSaveAndLoad() throws InvalidDataException, IOException, UnsupportedTagException {
        // given
        cache.put(OLDER_MP3, new Mp3(new Mp3File(Paths.get(OLDER_MP3))));
        cache.save();
        cache.load();

        // when
        Mp3 mp3 = cache.get(OLDER_MP3);

        // then
        Mp3 expected = new Mp3(new Mp3File(Paths.get(OLDER_MP3)));
        assertEquals(expected, mp3);

        // tear down
        deleteCacheFile();
    }

    private void deleteCacheFile() throws IOException {
        Path cacheFilePath = Paths.get(PLAYLIST_CACHE);
        if (cacheFilePath.toFile().exists()) {
            Files.delete(cacheFilePath);
        }
    }
}