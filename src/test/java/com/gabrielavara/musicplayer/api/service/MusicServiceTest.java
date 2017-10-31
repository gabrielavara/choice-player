package com.gabrielavara.musicplayer.api.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gabrielavara.musicplayer.controllers.PlayerController;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class MusicServiceTest {
    @Mock
    private PlayerController playerController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCurrentlyPlayingAlbumArtExisting() throws Exception {
        // given
        mockCurrentlyPlaying("testNewer.mp3");
        MusicService musicService = new MusicService();
        musicService.setPlayerController(playerController);

        // when
        Optional<byte[]> currentlyPlayingAlbumArt = musicService.getCurrentlyPlayingAlbumArt();

        // then
        assertEquals(true, currentlyPlayingAlbumArt.isPresent());
    }

    @Test
    public void testGetCurrentlyPlayingAlbumArtNonExisting() throws Exception {
        // given
        mockCurrentlyPlaying("testOlder.mp3");
        MusicService musicService = new MusicService();
        musicService.setPlayerController(playerController);

        // when
        Optional<byte[]> currentlyPlayingAlbumArt = musicService.getCurrentlyPlayingAlbumArt();

        // then
        assertEquals(false, currentlyPlayingAlbumArt.isPresent());
    }

    private void mockCurrentlyPlaying(final String mp3FileName)
                    throws IOException, UnsupportedTagException, InvalidDataException {
        Mp3File mp3File = new Mp3File(Paths.get("src/test/resources/mp3folder/" + mp3FileName));
        Mp3 mp3 = new Mp3(mp3File);
        when(playerController.getCurrentlyPlaying()).thenReturn(Optional.of(mp3));
    }
}