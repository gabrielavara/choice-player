package com.gabrielavara.choiceplayer.util;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.api.service.PlaylistLoader;
import com.gabrielavara.choiceplayer.views.TableItem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlaylistUtilTest {
    private ObservableList<Mp3> mp3List;
    private PlaylistUtil playlistUtil;
    private ObservableList<TableItem> mp3Files = FXCollections.observableArrayList();

    @Before
    public void setup() {
        List<Mp3> files = new PlaylistLoader().load(Paths.get("src/test/resources/mp3"));
        mp3List = FXCollections.observableList(files);
        List<TableItem> tableItems = IntStream.range(0, files.size()).mapToObj(index -> new TableItem(index + 1, files.get(index)))
                .collect(Collectors.toList());
        mp3Files.addAll(tableItems);
        playlistUtil = new PlaylistUtil(mp3Files);
    }

    @Test
    public void testGetNextTrackOnStartup() {
        // when
        Optional<Mp3> nextTrack = playlistUtil.getNextTrack();

        // then
        assertEquals(true, nextTrack.isPresent());
        assertEquals(mp3List.get(0), nextTrack.get());
    }

    @Test
    public void testGetNextTrack() {
        // given
        mp3Files.get(0).getMp3().setCurrentlyPlaying(true);

        // when
        Optional<Mp3> nextTrack = playlistUtil.getNextTrack();

        // then
        assertEquals(true, nextTrack.isPresent());
        assertEquals(mp3List.get(1), nextTrack.get());
    }

    @Test
    public void testGetNextTrackOnEndOfPlaylist() {
        // given
        mp3Files.get(3).getMp3().setCurrentlyPlaying(true);

        // when
        Optional<Mp3> nextTrack = playlistUtil.getNextTrack();

        // then
        assertEquals(false, nextTrack.isPresent());
    }

    @Test
    public void testGetPreviousTrackOnStartup() {
        // when
        Optional<Mp3> previousTrack = playlistUtil.getPreviousTrack();

        // then
        assertEquals(true, previousTrack.isPresent());
        assertEquals(mp3List.get(0), previousTrack.get());
    }

    @Test
    public void testGetPreviousTrack() {
        // given
        mp3Files.get(1).getMp3().setCurrentlyPlaying(true);

        // when
        Optional<Mp3> previousTrack = playlistUtil.getPreviousTrack();

        // then
        assertEquals(true, previousTrack.isPresent());
        assertEquals(mp3List.get(0), previousTrack.get());
    }

    @Test
    public void testGetPreviousTrackOnStartOfPlaylist() {
        // given
        mp3Files.get(0).getMp3().setCurrentlyPlaying(true);

        // when
        Optional<Mp3> previousTrack = playlistUtil.getPreviousTrack();

        // then
        assertEquals(false, previousTrack.isPresent());
    }

    @Test
    public void testGetCurrentlyPlayingAlbumArtNotExisting() {
        // given
        mp3Files.get(0).getMp3().setCurrentlyPlaying(true);

        // when
        Optional<byte[]> currentlyPlayingAlbumArt = playlistUtil.getCurrentlyPlayingAlbumArt();

        // then
        assertEquals(false, currentlyPlayingAlbumArt.isPresent());
    }

    @Test
    public void testGetCurrentlyPlayingAlbumArtExisting() {
        // given
        mp3Files.get(3).getMp3().setCurrentlyPlaying(true);

        // when
        Optional<byte[]> currentlyPlayingAlbumArt = playlistUtil.getCurrentlyPlayingAlbumArt();

        // then
        assertEquals(true, currentlyPlayingAlbumArt.isPresent());
    }
}