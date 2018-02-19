package com.gabrielavara.choiceplayer.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

public class Mp3Test {
    private Mp3 mp3 = new Mp3();

    @Test
    public void shouldGetTrackAsInt() {
        // given
        mp3.setTrack("01/02");

        // when
        int track = mp3.extractTrackAsInt();

        assertEquals(1, track);
    }


    @Test
    public void shouldSearchForInfoIfTrackNotContainsPer() {
        // given
        Mp3 mp3 = mp3WithAlbumArt();
        mp3.setTrack("1");

        // when
        boolean shouldSearchForInfo = mp3.shouldSearchForInfo();

        // then
        assertTrue(shouldSearchForInfo);
    }

    @Test
    public void shouldSearchForInfoIfNoAlbumArt() {
        // given
        Mp3 mp3 = mp3WithoutAlbumArt();
        mp3.setTrack("01/02");

        // when
        boolean shouldSearchForInfo = mp3.shouldSearchForInfo();

        // then
        assertTrue(shouldSearchForInfo);
    }

    @Test
    public void shouldNotSearchForInfo() {
        // given
        Mp3 mp3 = mp3WithAlbumArt();
        mp3.setTrack("01/02");

        // when
        boolean shouldSearchForInfo = mp3.shouldSearchForInfo();

        // then
        assertFalse(shouldSearchForInfo);
    }

    private Mp3 mp3WithAlbumArt() {
        return getMp3(Optional.of(new byte[0]));
    }

    private Mp3 mp3WithoutAlbumArt() {
        return getMp3(Optional.empty());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Mp3 getMp3(final Optional<byte[]> albumArtBytes) {
        return new Mp3() {
            @Override
            public Optional<byte[]> getAlbumArt() {
                return albumArtBytes;
            }
        };
    }
}