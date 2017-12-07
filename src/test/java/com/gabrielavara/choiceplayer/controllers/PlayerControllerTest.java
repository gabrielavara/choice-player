package com.gabrielavara.choiceplayer.controllers;

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

public class PlayerControllerTest {
    private PlayerController playerController = new PlayerController();
    private ObservableList<Mp3> mp3Files;

    @Before
    public void setUp() {
        List<Mp3> files = new PlaylistLoader().load(Paths.get("src/test/resources/mp3folder"));
        List<TableItem> tableItems = IntStream.range(0, files.size()).mapToObj(index -> new TableItem(index + 1, files.get(index)))
                        .collect(Collectors.toList());

        playerController.getMp3Files().addAll(tableItems);

        List<Mp3> mp3s = playerController.getMp3Files().stream().map(TableItem::getMp3).collect(Collectors.toList());
        mp3Files = FXCollections.observableList(mp3s);
    }

    @Test
    public void getNextTrackOnStartup() {
        //when
        Optional<Mp3> nextTrack = playerController.getPlaylistUtil().getNextTrack();

        //then
        assertEquals(true, nextTrack.isPresent());
        assertEquals(mp3Files.get(0), nextTrack.get());
    }

    @Test
    public void getNextTrack() {
        //given
        playerController.getMp3Files().get(0).getMp3().setCurrentlyPlaying(true);

        //when
        Optional<Mp3> nextTrack = playerController.getPlaylistUtil().getNextTrack();

        //then
        assertEquals(true, nextTrack.isPresent());
        assertEquals(mp3Files.get(1), nextTrack.get());
    }

    @Test
    public void getNextTrackOnEndOfPlaylist() {
        //given
        playerController.getMp3Files().get(3).getMp3().setCurrentlyPlaying(true);

        //when
        Optional<Mp3> nextTrack = playerController.getPlaylistUtil().getNextTrack();

        //then
        assertEquals(false, nextTrack.isPresent());
    }

    @Test
    public void getPreviousTrackOnStartup() {
        //when
        Optional<Mp3> previousTrack = playerController.getPlaylistUtil().getPreviousTrack();

        //then
        assertEquals(true, previousTrack.isPresent());
        assertEquals(mp3Files.get(0), previousTrack.get());
    }

    @Test
    public void getPreviousTrack() {
        //given
        playerController.getMp3Files().get(1).getMp3().setCurrentlyPlaying(true);

        //when
        Optional<Mp3> previousTrack = playerController.getPlaylistUtil().getPreviousTrack();

        //then
        assertEquals(true, previousTrack.isPresent());
        assertEquals(mp3Files.get(0), previousTrack.get());
    }

    @Test
    public void getPreviousTrackOnStartOfPlaylist() {
        //given
        playerController.getMp3Files().get(0).getMp3().setCurrentlyPlaying(true);

        //when
        Optional<Mp3> previousTrack = playerController.getPlaylistUtil().getPreviousTrack();

        //then
        assertEquals(false, previousTrack.isPresent());
    }

}