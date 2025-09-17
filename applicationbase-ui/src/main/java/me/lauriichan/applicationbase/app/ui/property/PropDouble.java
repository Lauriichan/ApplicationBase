package me.lauriichan.applicationbase.app.ui.property;

public final class PropDouble extends Property<PropDouble> {

    public static class Builder {

        private double min = Double.MIN_VALUE, max = Double.MAX_VALUE, value = 0f;

        public double min() {
            return min;
        }

        public Builder min(double min) {
            this.min = min;
            return this;
        }

        public double max() {
            return max;
        }

        public Builder max(double max) {
            this.max = max;
            return this;
        }

        public double value() {
            return value;
        }

        public Builder value(double value) {
            this.value = value;
            return this;
        }

        public PropDouble build() {
            return new PropDouble(min, max).set(value);
        }

    }

    public static PropDouble create() {
        return new PropDouble(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public static Builder builder() {
        return new Builder();
    }

    private final double min, max;

    private volatile double value, start, end;

    private PropDouble(double min, double max) {
        this.min = min > max ? max : min;
        this.max = min > max ? min : max;
    }

    public double get() {
        return value;
    }

    public PropDouble set(double value) {
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
        value = clamp(start * (1 - ratio) + end * ratio);
    }

    private double clamp(double value) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

}
