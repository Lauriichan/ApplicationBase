package me.lauriichan.applicationbase.app.ui.property;

import me.lauriichan.applicationbase.app.ui.property.function.InterpolationFunction;

public abstract class Property<S extends Property<S>> {

    private volatile InterpolationFunction<?> interpolationFunction;

    private volatile double timeSinceLastChange = 0d;

    @SuppressWarnings("unchecked")
    private S self() {
        return (S) this;
    }

    public final boolean isImmediate() {
        return interpolationFunction == null || interpolationFunction.time() == 0d;
    }

    public InterpolationFunction<?> interpolationFunction() {
        return interpolationFunction;
    }

    public S interpolationFunction(InterpolationFunction<?> interpolationFunction) {
        this.interpolationFunction = interpolationFunction;
        return self();
    }

    protected final void notifyChanged() {
        timeSinceLastChange = 0d;
    }

    public final void update(double delta) {
        if (interpolationFunction == null || timeSinceLastChange > interpolationFunction.time()) {
            return;
        }
        timeSinceLastChange += delta;
        internalUpdate(interpolationFunction.ratio(delta));
    }

    protected abstract void internalUpdate(double ratio);

}
