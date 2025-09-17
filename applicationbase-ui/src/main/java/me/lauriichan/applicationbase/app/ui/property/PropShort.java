package me.lauriichan.applicationbase.app.ui.property;

public final class PropShort extends Property<PropShort> {

    public static class Builder {

        private short min = Short.MIN_VALUE, max = Short.MAX_VALUE, value = 0;

        public short min() {
            return min;
        }

        public Builder min(short min) {
            this.min = min;
            return this;
        }

        public short max() {
            return max;
        }

        public Builder max(short max) {
            this.max = max;
            return this;
        }

        public short value() {
            return value;
        }

        public Builder value(short value) {
            this.value = value;
            return this;
        }

        public PropShort create() {
            return new PropShort(min, max).set(value);
        }

    }

    public static PropShort create() {
        return new PropShort(Short.MIN_VALUE, Short.MAX_VALUE);
    }

    public static Builder builder() {
        return new Builder();
    }

    private final short min, max;

    private volatile short value, start, end;

    private PropShort(short min, short max) {
        this.min = min > max ? max : min;
        this.max = min > max ? min : max;
    }

    public short get() {
        return value;
    }

    public PropShort set(short value) {
        value = clamp(value);
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
        value = clamp((short) (start * (1 - ratio) + end * ratio));
    }

    private short clamp(short value) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

}
