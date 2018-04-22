package com.gabrielavara.choiceplayer.messages;

import java.util.Optional;

import com.gabrielavara.choiceplayer.dto.Mp3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SelectionChangedMessage {
    @Getter
    private final Mp3 newValue;
    private final Mp3 oldValue;
    @Getter
    private final boolean play;

    public Optional<Mp3> getOldValue() {
        return Optional.ofNullable(oldValue);
    }
}
