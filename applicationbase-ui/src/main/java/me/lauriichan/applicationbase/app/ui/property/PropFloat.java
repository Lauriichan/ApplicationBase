package me.lauriichan.applicationbase.app.ui.property;

public final class PropFloat extends Property<PropFloat> {

    public static class Builder {

        private float min = Float.MIN_VALUE, max = Float.MAX_VALUE, value = 0f;

        public float min() {
            return min;
        }

        public Builder min(float min) {
            this.min = min;
            return this;
        }

        public float max() {
            return max;
        }

        public Builder max(float max) {
            this.max = max;
            return this;
        }

        public float value() {
            return value;
        }

        public Builder value(float value) {
            this.value = value;
            return this;
        }

        public PropFloat create() {
            return new PropFloat(min, max).set(value);
        }

    }

    public static PropFloat create() {
        return new PropFloat(Float.MIN_VALUE, Float.MAX_VALUE);
    }

    public static Builder builder() {
        return new Builder();
    }

    private final float min, max;

    private volatile float value, start, end;

    private PropFloat(float min, float max) {
        this.min = min > max ? max : min;
        this.max = min > max ? min : max;
    }

    public float get() {
        return value;
    }

    public PropFloat set(float value) {
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
        value = clamp((float) (start * (1 - ratio) + end * ratio));
    }

    private float clamp(float value) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

}
