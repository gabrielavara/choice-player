package com.gabrielavara.choiceplayer.messages;

import com.gabrielavara.choiceplayer.controls.AnimationDirection;

import lombok.Getter;

@Getter
public class PlaylistAnimatedMessage {
    private AnimationDirection direction;

    public PlaylistAnimatedMessage(AnimationDirection direction) {
        this.direction = direction;
    }
}
