package com.gabrielavara.choiceplayer.controls.overlay;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

public enum Action {
    LIKE, DISLIKE, PREVIOUS, NEXT, PAUSE, PLAY;

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
            case PAUSE:
                return MaterialDesignIcon.PAUSE;
            case PLAY:
                return MaterialDesignIcon.PLAY;
        }
        return MaterialDesignIcon.PLAY;
    }
}