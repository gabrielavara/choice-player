package com.gabrielavara.choiceplayer.messages;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import lombok.Getter;

public class SelectionChangedMessage {
    @Getter
    private Mp3 mp3;

    public SelectionChangedMessage(Mp3 mp3) {
        this.mp3 = mp3;
    }
}
