package com.gabrielavara.choiceplayer.playlist;

import static com.gabrielavara.choiceplayer.controls.AnimationDirection.OUT;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import com.jfoenix.controls.JFXListView;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import de.saxsys.javafx.test.JfxRunner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

@RunWith(JfxRunner.class)
public class PlaylistTest {

    private static final String CACHE_JSON = "cache.json";

    @Mock
    private JFXListView<PlaylistItemView> playlistView;
    @Mock
    private PlaylistAnimator playlistAnimator;
    @Mock
    private PlaylistLoader playlistLoader;

    private ObservableList<PlaylistItemView> playlistItemViews = FXCollections.observableArrayList();
    private Playlist playlist;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        playlist = new Playlist(playlistView, playlistItemViews, playlistAnimator) {
            @Override
            PlaylistLoader createPlaylistLoader() {
                return playlistLoader;
            }

            @Override
            Path getCacheFile() {
                return Paths.get(CACHE_JSON);
            }
        };
    }

    @Test
    public void testItemsSet() {
        verify(playlistView).setItems(playlistItemViews);
    }

    @Test
    public void testGetCell() {
        // given
        PlaylistItemView playlistItemView = new PlaylistItemView();

        //when
        playlist.getCell(playlistItemView);

        //then
        verify(playlistAnimator).getCell(playlistItemView);
    }

    @Test
    public void testGetCellAfter() {
        // given
        PlaylistItemView playlistItemView = new PlaylistItemView();

        //when
        playlist.getCellsAfter(playlistItemView);

        //then
        verify(playlistAnimator).getCellsAfter(playlistItemView);
    }

    @Test
    public void changeTheme() {
        //when
        playlist.changeTheme();

        //then
        verify(playlistAnimator).changeTheme();
    }

    @Test
    public void testLoadWithCacheNotEquals() throws InvalidDataException, IOException, UnsupportedTagException {
        //given
        PlaylistCache.save(PlaylistCacheTestUtil.createPlaylist());
        List<Mp3> mp3List = singletonList(createMp3());
        PlaylistItemView itemView = createPlaylistItemView(createMp3());
        when(playlistLoader.load(any())).thenReturn(mp3List);
        mockAnimateOut();

        //when
        playlist.load();
        Awaitility.with().pollInterval(250, MILLISECONDS).await()
                .atMost(5, SECONDS).until(containsPlaylist(itemView));

        //then
        verify(playlistLoader).load(Paths.get(CACHE_JSON));
        verify(playlistAnimator).showItems(Optional.empty(), true);
        verify(playlistAnimator).showItems(Optional.empty(), false);

        // tear down
        PlaylistCacheTestUtil.deleteCacheFile();
    }

    @SuppressWarnings("unchecked")
    private void mockAnimateOut() {
        doAnswer(invocation -> {
            EventHandler<ActionEvent> argument = (EventHandler<ActionEvent>) invocation.getArgument(1);
            argument.handle(null);
            return null;
        }).when(playlistAnimator).animateItems(eq(OUT), any(), eq(Optional.empty()), eq(false));
    }

    private Callable<Boolean> containsPlaylist(PlaylistItemView playlistItemView) {
        return () -> playlistItemViews.contains(playlistItemView);
    }

    private PlaylistItemView createPlaylistItemView(Mp3 mp3) {
        mp3.setCurrentlyPlaying(true);
        return new PlaylistItemView(1, mp3);
    }

    private Mp3 createMp3() {
        Mp3 mp3 = new Mp3();
        mp3.setArtist("artist");
        mp3.setAlbum("album");
        mp3.setTitle("title");
        mp3.setYear("2017");
        mp3.setTrack("1");
        return mp3;
    }

    @Test
    public void testReload() {
    }

    @Test
    public void testReloadWithoutCache() {
    }
}