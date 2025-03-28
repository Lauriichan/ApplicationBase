package me.lauriichan.applicationbase.app.ui.animation.interpolator;

import me.lauriichan.applicationbase.app.util.color.SimpleColor;

public final class ColorInterpolator implements IAnimationInterpolator<SimpleColor> {

    private final SimpleColor value;
    
    public ColorInterpolator(final SimpleColor value) {
        this.value = value;
    }
    
    @Override
    public void manipulate(SimpleColor start, SimpleColor target, double progress) {
        value.interpolate(start, target, progress);
    }

}
