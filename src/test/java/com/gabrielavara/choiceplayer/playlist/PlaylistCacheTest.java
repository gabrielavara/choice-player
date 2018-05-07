package com.gabrielavara.choiceplayer.playlist;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

public class PlaylistCacheTest {

    @Test
    public void testSaveAndLoad() throws InvalidDataException, IOException, UnsupportedTagException {
        // given
        PlaylistCache.save(PlaylistCacheTestUtil.createPlaylist());

        // when
        List<PlaylistItemView> playlist = PlaylistCache.load();

        // then
        assertEquals(PlaylistCacheTestUtil.createPlaylist(), playlist);

        // tear down
        PlaylistCacheTestUtil.deleteCacheFile();
    }

}