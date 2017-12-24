package com.gabrielavara.choiceplayer.messages;

import java.util.Optional;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import lombok.Getter;

public class SelectionChangedMessage {
    @Getter
    private Mp3 newValue;
    private Mp3 oldValue;

    public SelectionChangedMessage(Mp3 newValue, Mp3 oldValue) {
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    public Optional<Mp3> getOldValue() {
        return Optional.ofNullable(oldValue);
    }
}
