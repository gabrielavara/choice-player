package com.gabrielavara.choiceplayer.messages;

import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import lombok.Getter;

public class PlaylistItemSelectedMessage {
    @Getter
    private PlaylistItemView playlistItemView;

    public PlaylistItemSelectedMessage(PlaylistItemView playlistItemView) {
        this.playlistItemView = playlistItemView;
    }
}
