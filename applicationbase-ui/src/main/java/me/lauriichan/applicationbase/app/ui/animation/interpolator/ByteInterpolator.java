package me.lauriichan.applicationbase.app.ui.animation.interpolator;

import me.lauriichan.applicationbase.app.ui.component.property.PropByte;

public final class ByteInterpolator implements IAnimationInterpolator<Byte> {

    private final PropByte value;

    public ByteInterpolator(final PropByte value) {
        this.value = value;
    }

    @Override
    public void manipulate(Byte start, Byte end, double progress) {
        value.set((byte) (start.byteValue() * (1 - progress) + end.byteValue() * progress));
    }

}
