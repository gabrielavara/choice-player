package com.gabrielavara.choiceplayer.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.messages.PlaylistItemSelectedMessage;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaylistUtil {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.api.controllers.PlaylistUtil");

    private ObservableList<PlaylistItemView> playlistItems;

    public PlaylistUtil(ObservableList<PlaylistItemView> playlistItems) {
        this.playlistItems = playlistItems;
    }

    public List<Mp3> getPlayList() {
        return playlistItems.stream().map(PlaylistItemView::getMp3).collect(Collectors.toList());
    }

    public Optional<Mp3> getCurrentlyPlaying() {
        return getCurrentlyPlayingPlaylistItemView().map(PlaylistItemView::getMp3);
    }

    public Optional<PlaylistItemView> getCurrentlyPlayingPlaylistItemView() {
        return playlistItems.stream().filter(s -> s.getMp3().isCurrentlyPlaying()).findFirst();
    }

    public Optional<PlaylistItemView> getPlaylistItemView(Mp3 mp3) {
        return playlistItems.stream().filter(s -> mp3.equals(s.getMp3())).findFirst();
    }

    public void goToNextTrack() {
        getNextPlaylistItemView().ifPresent(this::select);
    }

    Optional<Mp3> getNextTrack() {
        return getNextPlaylistItemView().map(PlaylistItemView::getMp3);
    }

    public Optional<PlaylistItemView> getNextPlaylistItemView() {
        OptionalInt first = IntStream.range(0, playlistItems.size()).filter(i -> playlistItems.get(i).getMp3().isCurrentlyPlaying()).findFirst();
        if (first.isPresent()) {
            int index = first.getAsInt();
            return playlistItems.size() > index + 1 ? Optional.of(playlistItems.get(index + 1)) : Optional.empty();
        }
        return Optional.of(playlistItems.get(0));
    }

    public void goToPreviousTrack() {
        getPreviousPlaylistItemView().ifPresent(this::select);
    }

    Optional<Mp3> getPreviousTrack() {
        return getPreviousPlaylistItemView().map(PlaylistItemView::getMp3);
    }

    private Optional<PlaylistItemView> getPreviousPlaylistItemView() {
        OptionalInt first = IntStream.range(0, playlistItems.size()).filter(i -> playlistItems.get(i).getMp3().isCurrentlyPlaying()).findFirst();
        if (first.isPresent()) {
            int index = first.getAsInt();
            return 0 <= index - 1 ? Optional.of(playlistItems.get(index - 1)) : Optional.empty();
        }
        return Optional.of(playlistItems.get(0));
    }

    public void select(PlaylistItemView item) {
        Messenger.send(new PlaylistItemSelectedMessage(item));
    }

    public Optional<byte[]> getCurrentlyPlayingAlbumArt() {
        log.info("getCurrentlyPlayingAlbumArt called");
        Optional<Mp3> currentlyPlaying = getCurrentlyPlaying();
        return currentlyPlaying.flatMap(PlaylistUtil::getAlbumArt);
    }

    public static Optional<byte[]> getAlbumArt(Mp3 mp3) {
        Path path = Paths.get(mp3.getFilename());
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
}
