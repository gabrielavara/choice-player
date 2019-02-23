package com.gabrielavara.choiceplayer.messages;

import com.gabrielavara.choiceplayer.util.Opinion;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class PlaylistItemSelectedMessage {
    private final PlaylistItemView playlistItemView;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Opinion> opinion;
}
