package me.lauriichan.applicationbase.app.ui.property.function;

import java.util.concurrent.TimeUnit;

public abstract class InterpolationFunction<F extends InterpolationFunction<F>> {

    protected volatile double time = 1d;

    @SuppressWarnings("unchecked")
    private F self() {
        return (F) this;
    }

    public final double time() {
        return time;
    }

    public final F time(double time) {
        this.time = Math.max(time, 0d);
        return self();
    }

    public final F time(long time, TimeUnit unit) {
        return time(time / (double) unit.convert(1, TimeUnit.SECONDS));
    }

    public final double ratio(double timeElapsed) {
        return calculate(Math.min(Math.max(timeElapsed / time, 0d), 1d));
    }

    protected abstract double calculate(double progress);

}
