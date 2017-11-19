package com.gabrielavara.choiceplayer.controllers;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import javafx.collections.ObservableList;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class PlayerControllerTest {
    private PlayerController playerController = new PlayerController();
    private ObservableList<Mp3> mp3Files;

    @Before
    public void setUp() throws Exception {
        playerController.loadMp3Files();
        mp3Files = playerController.getMp3Files();
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
        playerController.getMp3Files().get(0).setCurrentlyPlaying(true);

        //when
        Optional<Mp3> nextTrack = playerController.getNextTrack();

        //then
        assertEquals(true, nextTrack.isPresent());
        assertEquals(mp3Files.get(1), nextTrack.get());
    }

    @Test
    public void getNextTrackOnEndOfPlaylist() throws Exception {
        //given
        playerController.getMp3Files().get(1).setCurrentlyPlaying(true);

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
        playerController.getMp3Files().get(1).setCurrentlyPlaying(true);

        //when
        Optional<Mp3> previousTrack = playerController.getPreviousTrack();

        //then
        assertEquals(true, previousTrack.isPresent());
        assertEquals(mp3Files.get(0), previousTrack.get());
    }

    @Test
    public void getPreviousTrackOnStartOfPlaylist() throws Exception {
        //given
        playerController.getMp3Files().get(0).setCurrentlyPlaying(true);

        //when
        Optional<Mp3> previousTrack = playerController.getPreviousTrack();

        //then
        assertEquals(false, previousTrack.isPresent());
    }

}