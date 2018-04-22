package com.gabrielavara.choiceplayer.messages;

import com.gabrielavara.choiceplayer.controls.AnimationDirection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlaylistAnimatedMessage {
    private final AnimationDirection direction;
}
