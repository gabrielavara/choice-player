package com.gabrielavara.choiceplayer.controls.bigalbumart;

public enum AnimationDirection {
    OUT, IN;

    AnimationDirection getInverse() {
        if (this == AnimationDirection.OUT) {
            return IN;
        } else {
            return OUT;
        }
    }
}
