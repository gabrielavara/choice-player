package com.gabrielavara.choiceplayer.messages;

import com.gabrielavara.choiceplayer.controls.overlay.Action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ActionMessage {
    private final Action action;
}
