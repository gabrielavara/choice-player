package com.gabrielavara.choiceplayer.messages;

import com.gabrielavara.choiceplayer.dto.Mp3;

import lombok.Getter;

@Getter
public class TagsSavedMessage {
    private Mp3 mp3;

    public TagsSavedMessage(Mp3 mp3) {
        this.mp3 = mp3;
    }
}
