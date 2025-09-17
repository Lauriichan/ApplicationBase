package me.lauriichan.applicationbase.app.ui.property;

public final class PropInt extends Property<PropInt> {

    public static class Builder {

        private int min = Integer.MIN_VALUE, max = Integer.MAX_VALUE, value = 0;

        public int min() {
            return min;
        }

        public Builder min(int min) {
            this.min = min;
            return this;
        }

        public int max() {
            return max;
        }

        public Builder max(int max) {
            this.max = max;
            return this;
        }

        public int value() {
            return value;
        }

        public Builder value(int value) {
            this.value = value;
            return this;
        }

        public PropInt create() {
            return new PropInt(min, max).set(value);
        }

    }

    public static PropInt create() {
        return new PropInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static Builder builder() {
        return new Builder();
    }

    private final int min, max;

    private volatile int value, start, end;

    private PropInt(int min, int max) {
        this.min = min > max ? max : min;
        this.max = min > max ? min : max;
    }

    public int get() {
        return value;
    }

    public PropInt set(int value) {
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
        value = clamp((int) (start * (1 - ratio) + end * ratio));
    }

    private int clamp(int value) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

}
