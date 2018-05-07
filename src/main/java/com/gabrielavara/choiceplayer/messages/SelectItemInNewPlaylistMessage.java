package com.gabrielavara.choiceplayer.messages;

import com.gabrielavara.choiceplayer.views.PlaylistItemView;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SelectItemInNewPlaylistMessage {
    private final PlaylistItemView selected;
}
