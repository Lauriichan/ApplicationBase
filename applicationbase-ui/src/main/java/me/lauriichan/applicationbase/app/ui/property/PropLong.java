package me.lauriichan.applicationbase.app.ui.property;

public final class PropLong extends Property<PropLong> {

    public static class Builder {

        private Long min = Long.MIN_VALUE, max = Long.MAX_VALUE, value = 0L;

        public Long min() {
            return min;
        }

        public Builder min(Long min) {
            this.min = min;
            return this;
        }

        public Long max() {
            return max;
        }

        public Builder max(Long max) {
            this.max = max;
            return this;
        }

        public Long value() {
            return value;
        }

        public Builder value(Long value) {
            this.value = value;
            return this;
        }

        public PropLong create() {
            return new PropLong(min, max).set(value);
        }

    }

    public static PropLong create() {
        return new PropLong(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public static Builder builder() {
        return new Builder();
    }

    private final long min, max;

    private volatile long value, start, end;

    private PropLong(long min, long max) {
        this.min = min > max ? max : min;
        this.max = min > max ? min : max;
    }

    public long get() {
        return value;
    }

    public PropLong set(long value) {
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
        value = clamp((long) (start * (1 - ratio) + end * ratio));
    }

    private long clamp(long value) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

}
