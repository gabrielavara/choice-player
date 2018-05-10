package com.gabrielavara.choiceplayer.playlist;

import static com.gabrielavara.choiceplayer.controls.AnimationDirection.OUT;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
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
import com.gabrielavara.choiceplayer.messages.PlaylistLoadedMessage;
import com.gabrielavara.choiceplayer.messenger.Messenger;
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

    private static final String FOLDER = "folder";

    @Mock
    private JFXListView<PlaylistItemView> playlistViewMock;
    @Mock
    private PlaylistAnimator playlistAnimatorMock;
    @Mock
    private PlaylistLoader playlistLoaderMock;

    private ObservableList<PlaylistItemView> playlistItemViews = FXCollections.observableArrayList();
    private Playlist playlist;
    private int playlistLoadedMessageSent;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        playlist = new Playlist(playlistViewMock, playlistItemViews, playlistAnimatorMock) {
            @Override
            PlaylistLoader createPlaylistLoader() {
                return playlistLoaderMock;
            }

            @Override
            Path getFolder() {
                return Paths.get(FOLDER);
            }
        };
        Messenger.register(PlaylistLoadedMessage.class, this::playlistLoaded);
    }

    @Test
    public void testGetCell() {
        // given
        PlaylistItemView playlistItemView = new PlaylistItemView();

        //when
        playlist.getCell(playlistItemView);

        //then
        verify(playlistAnimatorMock).getCell(playlistItemView);
    }

    @Test
    public void testGetCellAfter() {
        // given
        PlaylistItemView playlistItemView = new PlaylistItemView();

        //when
        playlist.getCellsAfter(playlistItemView);

        //then
        verify(playlistAnimatorMock).getCellsAfter(playlistItemView);
    }

    @Test
    public void changeTheme() {
        //when
        playlist.changeTheme();

        //then
        verify(playlistAnimatorMock).changeTheme();
    }

    @Test
    public void testLoadWithCacheNotEquals() throws InvalidDataException, IOException, UnsupportedTagException {
        //given
        PlaylistItemView itemView = createPlaylistItemView(createMp3());
        PlaylistCache.save(PlaylistCacheTestUtil.createPlaylist());
        List<Mp3> mp3List = singletonList(createMp3());
        when(playlistLoaderMock.load(Paths.get(FOLDER))).thenReturn(mp3List);
        mockAnimateOut();

        //when
        playlist.load();
        Awaitility.with().pollInterval(250, MILLISECONDS).await()
                .atMost(2, SECONDS).until(containsPlaylist(itemView));

        //then
        verify(playlistLoaderMock).load(Paths.get(FOLDER));
        verify(playlistAnimatorMock).showItems(Optional.empty(), true);
        verify(playlistAnimatorMock).showItems(Optional.empty(), false);
        assertEquals(0, playlistLoadedMessageSent);

        // tear down
        PlaylistCacheTestUtil.deleteCacheFile();
    }

    @Test
    public void testLoadWithCacheEquals() throws IOException {
        // given
        PlaylistItemView itemView = createPlaylistItemView(createMp3());
        PlaylistCache.save(singletonList(itemView));
        List<Mp3> mp3List = singletonList(createMp3());
        when(playlistLoaderMock.load(Paths.get(FOLDER))).thenReturn(mp3List);
        mockAnimateOut();

        // when
        playlist.load();
        Awaitility.with().pollInterval(250, MILLISECONDS).await()
                .atMost(2, SECONDS).until(containsPlaylist(itemView));

        // then
        verify(playlistLoaderMock).load(Paths.get(FOLDER));
        verify(playlistAnimatorMock).showItems(Optional.empty(), true);
        verify(playlistAnimatorMock, never()).showItems(Optional.empty(), false);
        assertEquals(1, playlistLoadedMessageSent);

        // tear down
        PlaylistCacheTestUtil.deleteCacheFile();
    }

    @Test
    public void testReloadCacheEquals() throws IOException {
        // given
        PlaylistItemView itemView = createPlaylistItemView(createMp3());
        PlaylistCache.save(singletonList(itemView));
        List<Mp3> mp3List = singletonList(createMp3());
        when(playlistLoaderMock.load(Paths.get(FOLDER))).thenReturn(mp3List);
        mockAnimateOut();

        // when
        playlist.reload();
        Awaitility.with().pollInterval(250, MILLISECONDS).await()
                .atMost(2, SECONDS).until(containsPlaylist(itemView));

        // then
        verify(playlistLoaderMock).load(Paths.get(FOLDER));
        verify(playlistAnimatorMock).showItems(Optional.empty(), true);
        verify(playlistAnimatorMock, never()).showItems(Optional.empty(), false);
        assertEquals(1, playlistLoadedMessageSent);

        // tear down
        PlaylistCacheTestUtil.deleteCacheFile();
    }

    @Test
    public void testReloadWithoutCache() throws IOException {
        // given
        PlaylistItemView itemView = createPlaylistItemView(createMp3());
        PlaylistCache.save(singletonList(itemView));
        List<Mp3> mp3List = singletonList(createMp3());
        when(playlistLoaderMock.load(Paths.get(FOLDER))).thenReturn(mp3List);
        mockAnimateOut();

        // when
        playlist.reloadWithoutCache();
        Awaitility.with().pollInterval(250, MILLISECONDS).await()
                .atMost(2, SECONDS).until(containsPlaylist(itemView));

        // then
        verify(playlistLoaderMock).load(Paths.get(FOLDER));
        verify(playlistAnimatorMock, never()).showItems(Optional.empty(), true);
        verify(playlistAnimatorMock).showItems(Optional.empty(), false);
        assertEquals(0, playlistLoadedMessageSent);

        // tear down
        PlaylistCacheTestUtil.deleteCacheFile();
    }

    private void playlistLoaded(PlaylistLoadedMessage m) {
        playlistLoadedMessageSent++;
    }

    @SuppressWarnings("unchecked")
    private void mockAnimateOut() {
        doAnswer(invocation -> {
            EventHandler<ActionEvent> eventHandler = (EventHandler<ActionEvent>) invocation.getArgument(1);
            eventHandler.handle(null);
            return null;
        }).when(playlistAnimatorMock).animateItems(eq(OUT), any(), eq(Optional.empty()), eq(false));

        doAnswer(invocation -> {
            EventHandler<ActionEvent> eventHandler = (EventHandler<ActionEvent>) invocation.getArgument(0);
            eventHandler.handle(null);
            return null;
        }).when(playlistAnimatorMock).animateOutItems(any(), eq(Optional.empty()));
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
}