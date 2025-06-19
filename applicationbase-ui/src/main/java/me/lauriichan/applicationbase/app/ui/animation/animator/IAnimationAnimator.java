package me.lauriichan.applicationbase.app.ui.animation.animator;

public interface IAnimationAnimator {

    public static <T> InterpolationAnimator.Builder<T> interpolation() {
        return InterpolationAnimator.builder();
    }

    void animate(boolean regressing, double progress);

}
