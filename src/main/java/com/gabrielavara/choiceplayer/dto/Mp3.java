package com.gabrielavara.choiceplayer.dto;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gabrielavara.choiceplayer.beatport.BeatportSearchInput;
import com.gabrielavara.choiceplayer.messages.BeginToSaveTagsMessage;
import com.gabrielavara.choiceplayer.messages.TagsSavedMessage;
import com.gabrielavara.choiceplayer.messenger.Messenger;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(exclude = {"currentlyPlaying", "trackAsInt", "albumArtist", "comment", "genre", "bpm", "changed"})
@NoArgsConstructor
public class Mp3 implements BeatportSearchInput {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.api.controllers.PlaylistUtil");
    private static final String EMPTY = "";
    private static final String DEFAULT_TRACK = "1";

    @Setter
    private String artist;
    @Setter
    @JsonIgnore
    private String albumArtist = EMPTY;
    @Setter
    @JsonIgnore
    private String comment;
    @Setter
    @JsonIgnore
    private String genre;
    @Setter
    @JsonIgnore
    private int bpm;
    @Setter
    private String title;
    @Setter
    private String year = EMPTY;
    @Setter
    private String album;
    @Setter
    private String track;
    @JsonIgnore
    private int trackAsInt;
    private long length;
    private String filename;
    @Setter
    private boolean currentlyPlaying;
    @JsonIgnore
    private SimpleBooleanProperty changed = new SimpleBooleanProperty(false);

    public Mp3(Mp3File mp3) {
        artist = extractArtist(mp3);
        albumArtist = extractAlbumArtist(mp3);
        title = extractTitle(mp3);
        year = extractYear(mp3);
        album = extractAlbum(mp3);
        track = extractTrack(mp3);
        trackAsInt = extractTrackAsInt();
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

    private String extractAlbumArtist(Mp3File mp3) {
        if (mp3.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3.getId3v2Tag();
            return id3v2Tag.getAlbumArtist();
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

    int extractTrackAsInt() {
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

    @JsonIgnore
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

    public void setAlbumArtAndSaveTags(byte[] bytes) {
        if (isCurrentlyPlaying()) {
            Messenger.send(new BeginToSaveTagsMessage());
        }
        Path path = Paths.get(getFilename());
        try {
            Mp3File mp3File = new Mp3File(path);
            setId3v2Tag(bytes, mp3File);
            setId3v1Tag(mp3File);
            updateFile(mp3File);
            Platform.runLater(() -> changed.set(true));
            if (isCurrentlyPlaying()) {
                Messenger.send(new TagsSavedMessage(this));
            }
        } catch (IOException | UnsupportedTagException | InvalidDataException | NotSupportedException e) {
            log.error("Could not save mp3: {}", e.getMessage());
        }
    }

    private String getNewFileName(Mp3File mp3) {
        String id3v1Album = mp3.hasId3v1Tag() ? mp3.getId3v1Tag().getArtist() : EMPTY;
        String newArtist = mp3.hasId3v2Tag() ? mp3.getId3v2Tag().getArtist() : id3v1Album;
        String id3v1Title = mp3.hasId3v1Tag() ? mp3.getId3v1Tag().getTitle() : EMPTY;
        String newTitle = mp3.hasId3v2Tag() ? mp3.getId3v2Tag().getTitle() : id3v1Title;
        Path path = Paths.get(mp3.getFilename());
        String folder = mp3.getFilename().replace(path.getFileName().toString(), EMPTY);
        return folder + newArtist + " - " + newTitle + ".mp3";
    }

    private void updateFile(Mp3File mp3) throws IOException, NotSupportedException {
        String newFileName = getNewFileName(mp3);
        if (newFileName.equals(mp3.getFilename())) {
            replaceFile(mp3, newFileName);
        } else {
            saveAsNew(mp3, newFileName);
        }
    }

    private void replaceFile(Mp3File mp3, String newFileName) throws IOException, NotSupportedException {
        newFileName = newFileName.replace(".mp3", "-new.mp3");
        mp3.save(newFileName);
        Path from = Paths.get(newFileName);
        Path to = Paths.get(getFilename());
        FileTime creationTime = Files.getFileAttributeView(to, BasicFileAttributeView.class).readAttributes().creationTime();
        Files.move(from, to, REPLACE_EXISTING);
        Files.getFileAttributeView(to, BasicFileAttributeView.class).setTimes(FileTime.fromMillis(System.currentTimeMillis()), null, creationTime);
    }

    private void saveAsNew(Mp3File mp3, String newFileName) throws IOException, NotSupportedException {
        mp3.save(newFileName);
        Path oldPath = Paths.get(getFilename());
        Path newPath = Paths.get(newFileName);
        FileTime creationTime = Files.getFileAttributeView(oldPath, BasicFileAttributeView.class).readAttributes().creationTime();
        Files.getFileAttributeView(newPath, BasicFileAttributeView.class).setTimes(FileTime.fromMillis(System.currentTimeMillis()), null, creationTime);
        Files.delete(oldPath);
        filename = mp3.getFilename();
    }

    private void setId3v2Tag(byte[] bytes, Mp3File mp3) {
        ID3v2 id3v2Tag = mp3.hasId3v2Tag() ? mp3.getId3v2Tag() : new ID3v24Tag();
        id3v2Tag.clearAlbumImage();
        id3v2Tag.setAlbumImage(bytes, "image/jpeg");
        setCommonTags(id3v2Tag);
        id3v2Tag.setAlbumArtist(albumArtist);
        id3v2Tag.setComment(comment);
        if (id3v2Tag.getVersion().equals("4.0")) {
            id3v2Tag.setGenreDescription(genre);
        }
        id3v2Tag.setBPM(bpm);
        if (!mp3.hasId3v2Tag()) {
            mp3.setId3v2Tag(id3v2Tag);
        }
    }

    private void setId3v1Tag(Mp3File mp3) {
        ID3v1 id3v1Tag = mp3.hasId3v2Tag() ? mp3.getId3v1Tag() : new ID3v1Tag();
        setCommonTags(id3v1Tag);
        if (!mp3.hasId3v2Tag()) {
            mp3.setId3v1Tag(id3v1Tag);
        }
    }

    private void setCommonTags(ID3v1 tag) {
        tag.setArtist(artist);
        tag.setAlbum(album);
        tag.setTitle(title);
        tag.setTrack(track);
    }

    public boolean shouldSearchForInfo() {
        return !getAlbumArt().isPresent() || !track.contains("/") || albumArtist == null || albumArtist.equals(EMPTY) || year == null || year.length() <= 4;
    }
}
