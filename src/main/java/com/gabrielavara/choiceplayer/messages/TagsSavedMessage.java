package com.gabrielavara.choiceplayer.messages;

import com.gabrielavara.choiceplayer.dto.Mp3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TagsSavedMessage {
    private final Mp3 mp3;
}
