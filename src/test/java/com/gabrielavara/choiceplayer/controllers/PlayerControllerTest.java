package com.gabrielavara.choiceplayer.controllers;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.views.TableItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class PlayerControllerTest {
    private PlayerController playerController = new PlayerController();
    private ObservableList<Mp3> mp3Files;

    @Before
    public void setUp() throws Exception {
        playerController.loadMp3Files();
        List<Mp3> mp3s = playerController.getMp3Files().stream().map(TableItem::getMp3).collect(Collectors.toList());
        mp3Files = FXCollections.observableList(mp3s);
    }

    @Test
    public void getNextTrackOnStartup() throws Exception {
        //when
        Optional<Mp3> nextTrack = playerController.getNextTrack();

        //then
        assertEquals(true, nextTrack.isPresent());
        assertEquals(mp3Files.get(0), nextTrack.get());
    }

    @Test
    public void getNextTrack() throws Exception {
        //given
        playerController.getMp3Files().get(0).getMp3().setCurrentlyPlaying(true);

        //when
        Optional<Mp3> nextTrack = playerController.getNextTrack();

        //then
        assertEquals(true, nextTrack.isPresent());
        assertEquals(mp3Files.get(1), nextTrack.get());
    }

    @Test
    public void getNextTrackOnEndOfPlaylist() throws Exception {
        //given
        playerController.getMp3Files().get(3).getMp3().setCurrentlyPlaying(true);

        //when
        Optional<Mp3> nextTrack = playerController.getNextTrack();

        //then
        assertEquals(false, nextTrack.isPresent());
    }

    @Test
    public void getPreviousTrackOnStartup() throws Exception {
        //when
        Optional<Mp3> previousTrack = playerController.getPreviousTrack();

        //then
        assertEquals(true, previousTrack.isPresent());
        assertEquals(mp3Files.get(0), previousTrack.get());
    }

    @Test
    public void getPreviousTrack() throws Exception {
        //given
        playerController.getMp3Files().get(1).getMp3().setCurrentlyPlaying(true);

        //when
        Optional<Mp3> previousTrack = playerController.getPreviousTrack();

        //then
        assertEquals(true, previousTrack.isPresent());
        assertEquals(mp3Files.get(0), previousTrack.get());
    }

    @Test
    public void getPreviousTrackOnStartOfPlaylist() throws Exception {
        //given
        playerController.getMp3Files().get(0).getMp3().setCurrentlyPlaying(true);

        //when
        Optional<Mp3> previousTrack = playerController.getPreviousTrack();

        //then
        assertEquals(false, previousTrack.isPresent());
    }

}