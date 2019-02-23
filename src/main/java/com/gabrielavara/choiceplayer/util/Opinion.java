package com.gabrielavara.choiceplayer.util;

import com.gabrielavara.choiceplayer.controls.actionicon.Action;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Opinion {
    LIKE(Action.LIKE), DISLIKE(Action.DISLIKE);

    private final Action action;
}
