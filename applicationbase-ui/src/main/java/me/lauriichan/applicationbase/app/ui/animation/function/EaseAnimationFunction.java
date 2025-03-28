package me.lauriichan.applicationbase.app.ui.animation.function;

import java.util.concurrent.TimeUnit;

public final class EaseAnimationFunction implements IAnimationFunction {

    private volatile double easeIn = 0d;
    private volatile double easeOut = 0d;

    public double easeIn() {
        return easeIn;
    }

    public EaseAnimationFunction easeIn(double easeIn) {
        this.easeIn = easeIn;
        return this;
    }

    public EaseAnimationFunction easeIn(long easeIn, TimeUnit unit) {
        this.easeOut = easeIn / (double) unit.convert(1, TimeUnit.SECONDS);
        return this;
    }

    public double easeOut() {
        return easeOut;
    }

    public EaseAnimationFunction easeOut(double easeOut) {
        this.easeOut = easeOut;
        return this;
    }

    public EaseAnimationFunction easeOut(long easeOut, TimeUnit unit) {
        this.easeOut = easeOut / (double) unit.convert(1, TimeUnit.SECONDS);
        return this;
    }

    @Override
    public double animate(boolean active, double elapsed) {
        if (active) {
            if (easeIn == 0d) {
                return 1d;
            }
            return ease(elapsed / easeIn);
        }
        if (easeOut == 0d) {
            return 0d;
        }
        return ease((easeOut - elapsed) / easeOut);
    }

    private double ease(double progress) {
        if (progress > 1d) {
            return 1d;
        } else if (progress < 0d) {
            return 0d;
        }
        return -(Math.cos(Math.PI * progress) - 1) / 2d;
    }

}
