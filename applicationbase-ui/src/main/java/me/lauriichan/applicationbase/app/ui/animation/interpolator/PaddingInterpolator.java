package me.lauriichan.applicationbase.app.ui.animation.interpolator;

import me.lauriichan.applicationbase.app.ui.component.property.PropPadding;

public final class PaddingInterpolator implements IAnimationInterpolator<Float> {

    private final PropPadding value;

    public PaddingInterpolator(final PropPadding value) {
        this.value = value;
    }

    @Override
    public void manipulate(Float start, Float end, double progress) {
        value.set((float) (start.floatValue() * (1 - progress) + end.floatValue() * progress));
    }

}
