package me.lauriichan.applicationbase.app.ui.property;

import me.lauriichan.applicationbase.app.util.color.SimpleColor;
import me.lauriichan.applicationbase.app.util.color.SimpleColor.ColorType;

public final class PropColor extends Property<PropColor> {

    private final SimpleColor value, start = new SimpleColor(ColorType.OKLAB), end = new SimpleColor(ColorType.OKLAB);

    public PropColor(ColorType type) {
        this.value = new SimpleColor(type);
    }

    public SimpleColor get() {
        return value;
    }

    public PropColor set(SimpleColor color) {
        if (this.value.equals(color)) {
            return this;
        }
        this.start.set(this.value);
        this.end.set(color);
        notifyChanged();
        if (isImmediate()) {
            this.value.set(color);
            return this;
        }
        return this;
    }

    @Override
    protected void internalUpdate(double ratio) {}

}
