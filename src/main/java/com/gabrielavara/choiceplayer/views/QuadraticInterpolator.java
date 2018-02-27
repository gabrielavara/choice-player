package com.gabrielavara.choiceplayer.views;

public class QuadraticInterpolator extends EasingInterpolator {

    public static final QuadraticInterpolator QUADRATIC_EASE_OUT = new QuadraticInterpolator(EasingMode.EASE_OUT);
    public static final QuadraticInterpolator QUADRATIC_EASE_IN = new QuadraticInterpolator(EasingMode.EASE_IN);
    public static final QuadraticInterpolator QUADRATIC_EASE_BOTH = new QuadraticInterpolator(EasingMode.EASE_BOTH);

    private QuadraticInterpolator(EasingMode easingMode) {
        super(easingMode);
    }

    @Override
    protected double baseCurve(double v) {
        return Math.pow(v, 3);
    }
}
