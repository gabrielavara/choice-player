package com.gabrielavara.choiceplayer.api.service;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

@Getter
@ToString
@EqualsAndHashCode
public class Mp3 {
    private static final String EMPTY = "";
    public static final String DEFAULT_TRACK = "1";
    private final String artist;
    private final String title;
    private final String year;
    private final String album;
    private final String track;
    private final long length;
    private final String filename;
    @Setter
    private boolean currentlyPlaying;

    Mp3(Mp3File mp3) {
        artist = extractArtist(mp3);
        title = extractTitle(mp3);
        year = extractYear(mp3);
        album = extractAlbum(mp3);
        track = extractTrack(mp3);
        length = mp3.getLengthInMilliseconds();
        filename = mp3.getFilename();
    }

    private String extractArtist(Mp3File mp3) {
        if (mp3.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3.getId3v2Tag();
            return id3v2Tag.getArtist();
        }
        if (mp3.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3.getId3v1Tag();
            return id3v1Tag.getArtist();
        }
        return EMPTY;
    }

    private String extractTitle(Mp3File mp3) {
        if (mp3.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3.getId3v2Tag();
            return id3v2Tag.getTitle();
        }
        if (mp3.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3.getId3v1Tag();
            return id3v1Tag.getTitle();
        }
        return EMPTY;
    }

    private String extractYear(Mp3File mp3) {
        if (mp3.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3.getId3v2Tag();
            return id3v2Tag.getYear();
        }
        if (mp3.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3.getId3v1Tag();
            return id3v1Tag.getYear();
        }
        return EMPTY;
    }

    private String extractAlbum(Mp3File mp3) {
        if (mp3.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3.getId3v2Tag();
            return Optional.ofNullable(id3v2Tag.getAlbum()).orElse(EMPTY);
        }
        if (mp3.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3.getId3v1Tag();
            return Optional.ofNullable(id3v1Tag.getAlbum()).orElse(EMPTY);
        }
        return EMPTY;
    }

    private String extractTrack(Mp3File mp3) {
        if (mp3.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3.getId3v2Tag();
            return Optional.ofNullable(id3v2Tag.getTrack()).orElse(DEFAULT_TRACK);
        }
        if (mp3.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3.getId3v1Tag();
            return Optional.ofNullable(id3v1Tag.getTrack()).orElse(DEFAULT_TRACK);
        }
        return DEFAULT_TRACK;
    }
}
