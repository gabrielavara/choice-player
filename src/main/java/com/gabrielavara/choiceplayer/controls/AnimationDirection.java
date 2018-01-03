package com.gabrielavara.choiceplayer.controls;

public enum AnimationDirection {
    OUT, IN;

    public AnimationDirection getInverse() {
        if (this == AnimationDirection.OUT) {
            return IN;
        } else {
            return OUT;
        }
    }
}
