package com.gabrielavara.choiceplayer.controllers;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.views.TableItem;

import javafx.collections.ObservableList;

public class PlaylistUtil {
    private ObservableList<TableItem> mp3Files;

    public PlaylistUtil(ObservableList<TableItem> mp3Files) {
        this.mp3Files = mp3Files;
    }

    public Optional<Mp3> getCurrentlyPlaying() {
        return getCurrentlyPlayingTableItem().map(TableItem::getMp3);
    }

    Optional<TableItem> getCurrentlyPlayingTableItem() {
        List<TableItem> playing = mp3Files.stream().filter(s -> s.getMp3().isCurrentlyPlaying()).collect(Collectors.toList());
        return playing.size() == 1 ? Optional.of(playing.get(0)) : Optional.empty();
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

    Optional<Mp3> getPreviousTrack() {
        return getPreviousTableItem().map(TableItem::getMp3);
    }

    Optional<TableItem> getPreviousTableItem() {
        OptionalInt first = IntStream.range(0, mp3Files.size()).filter(i -> mp3Files.get(i).getMp3().isCurrentlyPlaying()).findFirst();
        if (first.isPresent()) {
            int index = first.getAsInt();
            return 0 <= index - 1 ? Optional.of(mp3Files.get(index - 1)) : Optional.empty();
        }
        return Optional.of(mp3Files.get(0));
    }
}