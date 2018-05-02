package com.gabrielavara.choiceplayer.controls.actionicon;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

public enum Action {
    LIKE, DISLIKE, PREVIOUS, NEXT, REWIND, FAST_FORWARD, PAUSE, PLAY;

    public MaterialDesignIcon getIcon() {
        switch (this) {
            case LIKE:
                return MaterialDesignIcon.THUMB_UP;
            case DISLIKE:
                return MaterialDesignIcon.THUMB_DOWN;
            case PREVIOUS:
                return MaterialDesignIcon.SKIP_PREVIOUS;
            case NEXT:
                return MaterialDesignIcon.SKIP_NEXT;
            case REWIND:
                return MaterialDesignIcon.REWIND;
            case FAST_FORWARD:
                return MaterialDesignIcon.FAST_FORWARD;
            case PAUSE:
                return MaterialDesignIcon.PAUSE;
            case PLAY:
                return MaterialDesignIcon.PLAY;
        }
        return MaterialDesignIcon.PLAY;
    }
}
