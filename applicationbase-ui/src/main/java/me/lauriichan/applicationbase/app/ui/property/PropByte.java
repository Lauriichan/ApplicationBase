package me.lauriichan.applicationbase.app.ui.property;

public final class PropByte extends Property<PropByte> {

    public static class Builder {

        private byte min = Byte.MIN_VALUE, max = Byte.MAX_VALUE, value = 0;

        public byte min() {
            return min;
        }

        public Builder min(byte min) {
            this.min = min;
            return this;
        }

        public byte max() {
            return max;
        }

        public Builder max(byte max) {
            this.max = max;
            return this;
        }

        public byte value() {
            return value;
        }

        public Builder value(byte value) {
            this.value = value;
            return this;
        }

        public PropByte create() {
            return new PropByte(min, max).set(value);
        }

    }

    public static PropByte create() {
        return new PropByte(Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    public static Builder builder() {
        return new Builder();
    }

    private final byte min, max;

    private volatile byte value, start, end;

    private PropByte(byte min, byte max) {
        this.min = min > max ? max : min;
        this.max = min > max ? min : max;
    }

    public byte get() {
        return value;
    }

    public PropByte set(byte value) {
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
        value = clamp((byte) (start * (1 - ratio) + end * ratio));
    }

    private byte clamp(byte value) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

}
