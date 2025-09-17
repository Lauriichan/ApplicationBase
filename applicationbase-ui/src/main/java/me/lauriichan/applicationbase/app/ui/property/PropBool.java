package me.lauriichan.applicationbase.app.ui.property;

public final class PropBool extends Property<PropBool> {

    private volatile boolean value, start, end;

    public boolean get() {
        return value;
    }

    public PropBool set(boolean value) {
        if (this.value == value) {
            return this;
        }
        this.start = this.value;
        this.end = value;
        notifyChanged();
        if (isImmediate()) {
            this.value = value;
            return this;
        }
        return this;
    }

    @Override
    protected void internalUpdate(double ratio) {
        if (ratio >= 0.5d) {
            value = end;
        } else {
            value = start;
        }
    }

}
