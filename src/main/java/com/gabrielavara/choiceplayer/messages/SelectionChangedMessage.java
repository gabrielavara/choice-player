package com.gabrielavara.choiceplayer.messages;

import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gabrielavara.choiceplayer.util.Opinion;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class SelectionChangedMessage {
    @Getter
    private final Mp3 newValue;
    private final Mp3 oldValue;
    private final PlaylistItemView oldPlaylistItemView;
    @Getter
    private final boolean play;
    @Getter
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Opinion> opinion;

    public Optional<Mp3> getOldValue() {
        return Optional.ofNullable(oldValue);
    }

    public Optional<PlaylistItemView> getOldPlaylistItemView() {
        return Optional.ofNullable(oldPlaylistItemView);
    }
}
