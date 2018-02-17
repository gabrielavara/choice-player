package com.gabrielavara.choiceplayer.dto;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(exclude = "currentlyPlaying")
@NoArgsConstructor
public class Mp3 {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.api.controllers.PlaylistUtil");
    private static final String EMPTY = "";
    private static final String DEFAULT_TRACK = "1";

    private String artist;
    private String title;
    private String year;
    private String album;
    private String track;
    private int trackAsInt;
    private long length;
    private String filename;
    @Setter
    private boolean currentlyPlaying;

    public Mp3(Mp3File mp3) {
        artist = extractArtist(mp3);
        title = extractTitle(mp3);
        year = extractYear(mp3);
        album = extractAlbum(mp3);
        track = extractTrack(mp3);
        trackAsInt = getTrackAsInt(track);
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

    private int getTrackAsInt(String track) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < track.length(); i++) {
            char c = track.charAt(i);
            if (c >= 48 && c <= 57) {
                sb.append(c);
            } else {
                break;
            }
        }
        return Integer.valueOf(sb.toString());
    }

    public Optional<byte[]> getAlbumArt() {
        Path path = Paths.get(getFilename());
        return getAlbumArtBytes(path);
    }

    private static Optional<byte[]> getAlbumArtBytes(Path path) {
        try {
            Mp3File mp3File = new Mp3File(path);
            if (mp3File.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                return Optional.ofNullable(id3v2Tag.getAlbumImage());
            }
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            log.error("Could not load mp3: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public boolean shouldSearchForInfo() {
        return !getAlbumArt().isPresent() || track.equals(DEFAULT_TRACK) || !track.startsWith("0");
    }
}
