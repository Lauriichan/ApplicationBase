package me.lauriichan.applicationbase.app.ui.animation.function;

public interface IAnimationFunction {

    public static EaseAnimationFunction ease() {
        return new EaseAnimationFunction();
    }

    public static FadeAnimationFunction fade() {
        return new FadeAnimationFunction();
    }

    double animate(boolean active, double elapsed);

}
