package me.lauriichan.applicationbase.app.ui.animation.interpolator;

import me.lauriichan.applicationbase.app.ui.component.property.PropLong;

public final class LongInterpolator implements IAnimationInterpolator<Long> {

    private final PropLong value;

    public LongInterpolator(final PropLong value) {
        this.value = value;
    }

    @Override
    public void manipulate(Long start, Long end, double progress) {
        value.set((long) (start.longValue() * (1 - progress) + end.longValue() * progress));
    }

}
