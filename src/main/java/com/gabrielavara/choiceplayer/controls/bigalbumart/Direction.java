package com.gabrielavara.choiceplayer.controls.bigalbumart;

public enum Direction {
    FORWARD, BACKWARD;

    public Direction getInverse() {
        return this == Direction.FORWARD ? BACKWARD : FORWARD;
    }
}
