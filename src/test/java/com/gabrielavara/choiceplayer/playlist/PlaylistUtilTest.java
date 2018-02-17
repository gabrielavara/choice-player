package com.gabrielavara.choiceplayer.playlist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import com.gabrielavara.choiceplayer.api.service.PlaylistTestInitializer;
import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlaylistUtilTest extends PlaylistTestInitializer {
    private ObservableList<Mp3> mp3List;
    private PlaylistUtil playlistUtil;
    private ObservableList<PlaylistItemView> playlistItemViews = FXCollections.observableArrayList();

    @Override
    @Before
    public void setup() throws IOException {
        super.setup();
        List<Mp3> files = new PlaylistLoader().load(Paths.get("src/test/resources/mp3"));
        mp3List = FXCollections.observableList(files);
        List<PlaylistItemView> items = IntStream.range(0, files.size()).mapToObj(index -> new PlaylistItemView(index + 1, files.get(index)))
                .collect(Collectors.toList());
        playlistItemViews.addAll(items);
        playlistUtil = new PlaylistUtil(playlistItemViews);
    }

    @Test
    public void testGetNextTrackOnStartup() {
        // when
        Optional<Mp3> nextTrack = playlistUtil.getNextTrack();

        // then
        assertTrue(nextTrack.isPresent());
        nextTrack.ifPresent(t -> assertEquals(mp3List.get(0), t));
    }

    @Test
    public void testGetNextTrack() {
        // given
        playlistItemViews.get(0).getMp3().setCurrentlyPlaying(true);

        // when
        Optional<Mp3> nextTrack = playlistUtil.getNextTrack();

        // then
        assertTrue(nextTrack.isPresent());
        nextTrack.ifPresent(t -> assertEquals(mp3List.get(1), t));
    }

    @Test
    public void testGetNextTrackOnEndOfPlaylist() {
        // given
        playlistItemViews.get(3).getMp3().setCurrentlyPlaying(true);

        // when
        Optional<Mp3> nextTrack = playlistUtil.getNextTrack();

        // then
        assertFalse(nextTrack.isPresent());
    }

    @Test
    public void testGetPreviousTrackOnStartup() {
        // when
        Optional<Mp3> previousTrack = playlistUtil.getPreviousTrack();

        // then
        assertTrue(previousTrack.isPresent());
        previousTrack.ifPresent(t -> assertEquals(mp3List.get(0), t));
    }

    @Test
    public void testGetPreviousTrack() {
        // given
        playlistItemViews.get(1).getMp3().setCurrentlyPlaying(true);

        // when
        Optional<Mp3> previousTrack = playlistUtil.getPreviousTrack();

        // then
        assertTrue(previousTrack.isPresent());
        previousTrack.ifPresent(t -> assertEquals(mp3List.get(0), t));
    }

    @Test
    public void testGetPreviousTrackOnStartOfPlaylist() {
        // given
        playlistItemViews.get(0).getMp3().setCurrentlyPlaying(true);

        // when
        Optional<Mp3> previousTrack = playlistUtil.getPreviousTrack();

        // then
        assertFalse(previousTrack.isPresent());
    }

    @Test
    public void testGetCurrentlyPlayingAlbumArtNotExisting() {
        // given
        playlistItemViews.get(0).getMp3().setCurrentlyPlaying(true);

        // when
        Optional<byte[]> currentlyPlayingAlbumArt = playlistUtil.getCurrentlyPlayingAlbumArt();

        // then
        assertFalse(currentlyPlayingAlbumArt.isPresent());
    }

    @Test
    public void testGetCurrentlyPlayingAlbumArtExisting() {
        // given
        playlistItemViews.get(3).getMp3().setCurrentlyPlaying(true);

        // when
        Optional<byte[]> currentlyPlayingAlbumArt = playlistUtil.getCurrentlyPlayingAlbumArt();

        // then
        assertTrue(currentlyPlayingAlbumArt.isPresent());
    }
}