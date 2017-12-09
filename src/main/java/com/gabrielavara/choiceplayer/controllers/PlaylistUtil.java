package com.gabrielavara.choiceplayer.controllers;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.messages.TableItemSelectedMessage;
import com.gabrielavara.choiceplayer.util.Messenger;
import com.gabrielavara.choiceplayer.views.TableItem;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlaylistUtil {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.api.controllers.PlaylistUtil");

    private ObservableList<TableItem> mp3Files;

    PlaylistUtil(ObservableList<TableItem> mp3Files) {
        this.mp3Files = mp3Files;
    }

    public List<Mp3> getPlayList() {
        return mp3Files.stream().map(TableItem::getMp3).collect(Collectors.toList());
    }

    public Optional<Mp3> getCurrentlyPlaying() {
        return getCurrentlyPlayingTableItem().map(TableItem::getMp3);
    }

    Optional<TableItem> getCurrentlyPlayingTableItem() {
        List<TableItem> playing = mp3Files.stream().filter(s -> s.getMp3().isCurrentlyPlaying()).collect(Collectors.toList());
        return playing.size() == 1 ? Optional.of(playing.get(0)) : Optional.empty();
    }

    public void goToNextTrack() {
        getNextTableItem().ifPresent(this::select);
    }

    Optional<Mp3> getNextTrack() {
        return getNextTableItem().map(TableItem::getMp3);
    }

    Optional<TableItem> getNextTableItem() {
        OptionalInt first = IntStream.range(0, mp3Files.size()).filter(i -> mp3Files.get(i).getMp3().isCurrentlyPlaying()).findFirst();
        if (first.isPresent()) {
            int index = first.getAsInt();
            return mp3Files.size() > index + 1 ? Optional.of(mp3Files.get(index + 1)) : Optional.empty();
        }
        return Optional.of(mp3Files.get(0));
    }

    public void goToPreviousTrack() {
        getPreviousTableItem().ifPresent(this::select);
    }

    Optional<Mp3> getPreviousTrack() {
        return getPreviousTableItem().map(TableItem::getMp3);
    }

    private Optional<TableItem> getPreviousTableItem() {
        OptionalInt first = IntStream.range(0, mp3Files.size()).filter(i -> mp3Files.get(i).getMp3().isCurrentlyPlaying()).findFirst();
        if (first.isPresent()) {
            int index = first.getAsInt();
            return 0 <= index - 1 ? Optional.of(mp3Files.get(index - 1)) : Optional.empty();
        }
        return Optional.of(mp3Files.get(0));
    }

    void select(TableItem tableItem) {
        Messenger.send(new TableItemSelectedMessage(tableItem));
    }

    public Optional<byte[]> getCurrentlyPlayingAlbumArt() {
        log.info("getCurrentlyPlayingAlbumArt called");
        Optional<Mp3> currentlyPlaying = getCurrentlyPlaying();
        if (currentlyPlaying.isPresent()) {
            Mp3 mp3 = currentlyPlaying.get();
            Path path = Paths.get(mp3.getFilename());
            return getAlbumArtBytes(path);
        }
        return Optional.empty();
    }

    private Optional<byte[]> getAlbumArtBytes(Path path) {
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
}
