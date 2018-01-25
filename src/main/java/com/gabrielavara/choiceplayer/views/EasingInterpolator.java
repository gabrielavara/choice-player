package com.gabrielavara.choiceplayer.views;

import javafx.animation.Interpolator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class EasingInterpolator extends Interpolator {

    private ObjectProperty<EasingMode> easingMode = new SimpleObjectProperty<>(EasingMode.EASE_OUT);

    EasingInterpolator(EasingMode easingMode) {
        this.easingMode.set(easingMode);
    }

    public ObjectProperty<EasingMode> easingModeProperty() {
        return easingMode;
    }

    public EasingMode getEasingMode() {
        return easingMode.get();
    }

    public void setEasingMode(EasingMode easingMode) {
        this.easingMode.set(easingMode);
    }

    protected abstract double baseCurve(final double v);

    @Override
    protected final double curve(final double v) {
        switch (easingMode.get()) {
            case EASE_IN:
                return baseCurve(v);
            case EASE_OUT:
                return 1 - baseCurve(1 - v);
            case EASE_BOTH:
                if (v <= 0.5) {
                    return baseCurve(2 * v) / 2;
                } else {
                    return (2 - baseCurve(2 * (1 - v))) / 2;
                }
            default:
                return baseCurve(v);
        }
    }
}
