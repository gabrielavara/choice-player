package com.gabrielavara.choiceplayer.messages;

import com.gabrielavara.choiceplayer.views.PlaylistItemView;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlaylistItemSelectedMessage {
    private final PlaylistItemView playlistItemView;
}
