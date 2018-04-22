package com.gabrielavara.choiceplayer.playlist;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.gabrielavara.choiceplayer.controls.overlay.Action;
import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gabrielavara.choiceplayer.messages.ActionMessage;
import com.gabrielavara.choiceplayer.messages.PlaylistItemSelectedMessage;
import com.gabrielavara.choiceplayer.messenger.Messenger;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;

import javafx.collections.ObservableList;

public class PlaylistUtil {

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

    public OptionalInt getPlaylistIndex(Mp3 mp3) {
        return IntStream.range(0, playlistItems.size()).filter(i -> playlistItems.get(i).getMp3().equals(mp3)).findFirst();
    }

    public void goToNextTrack() {
        getNextPlaylistItemView().ifPresent(this::select);
        Messenger.send(new ActionMessage(Action.NEXT));
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
        Messenger.send(new ActionMessage(Action.PREVIOUS));
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
        Optional<Mp3> currentlyPlaying = getCurrentlyPlaying();
        return currentlyPlaying.flatMap(Mp3::getAlbumArt);
    }

}
