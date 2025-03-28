package me.lauriichan.applicationbase.app.ui.animation;

import java.util.Objects;
import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import it.unimi.dsi.fastutil.objects.ReferenceLists;
import me.lauriichan.applicationbase.app.ui.animation.animator.IAnimationAnimator;
import me.lauriichan.applicationbase.app.ui.animation.function.IAnimationFunction;
import me.lauriichan.applicationbase.app.ui.animation.trigger.IAnimationTrigger;

public final class Animation {

    public static Animation.Builder builder() {
        return new Animation.Builder();
    }

    public static class Builder {

        private IAnimationTrigger trigger;
        private IAnimationFunction function;

        private final ReferenceArrayList<IAnimationAnimator> animators = new ReferenceArrayList<>();

        private boolean repeating, regressWhenInactive = true;

        private Consumer<Animation> onRestart;
        private Consumer<Animation> onActiveChanged;
        private Consumer<Animation> onDone;

        public IAnimationTrigger trigger() {
            return trigger;
        }

        public Builder trigger(IAnimationTrigger trigger) {
            this.trigger = trigger;
            return this;
        }

        public IAnimationFunction function() {
            return function;
        }

        public Builder function(IAnimationFunction function) {
            this.function = function;
            return this;
        }

        public Builder animator(IAnimationAnimator animator) {
            Objects.requireNonNull(animator);
            animators.add(animator);
            return this;
        }

        public Builder animators(IAnimationAnimator[] animators) {
            for (IAnimationAnimator animator : animators) {
                animator(animator);
            }
            return this;
        }

        public Builder clearAnimators() {
            animators.clear();
            return this;
        }

        public boolean repeating() {
            return repeating;
        }

        public Builder repeating(boolean repeating) {
            this.repeating = repeating;
            return this;
        }

        public boolean regressWhenInactive() {
            return regressWhenInactive;
        }

        public Builder regressWhenInactive(boolean regressWhenInactive) {
            this.regressWhenInactive = regressWhenInactive;
            return this;
        }

        public Consumer<Animation> onRestart() {
            return onRestart;
        }

        public Builder onRestart(Consumer<Animation> onRestart) {
            this.onRestart = onRestart;
            return this;
        }

        public Consumer<Animation> onActiveChanged() {
            return onActiveChanged;
        }

        public Builder onActiveChanged(Consumer<Animation> onActiveChanged) {
            this.onActiveChanged = onActiveChanged;
            return this;
        }

        public Consumer<Animation> onDone() {
            return onDone;
        }

        public Builder onDone(Consumer<Animation> onDone) {
            this.onDone = onDone;
            return this;
        }

        public Animation build() {
            if (animators.isEmpty()) {
                throw new IllegalArgumentException("No animators set");
            }
            return new Animation(trigger, function, animators, repeating, regressWhenInactive, onRestart, onActiveChanged, onDone);
        }

    }

    private final IAnimationTrigger trigger;
    private final IAnimationFunction function;

    private final ReferenceList<IAnimationAnimator> animators;

    private final boolean repeating, regressWhenInactive;
    
    private final Consumer<Animation> onRestart, onActiveChanged, onDone;

    private volatile boolean active = false, done = false;
    private volatile double progress = 0d, elapsed = 0d;

    private Animation(final IAnimationTrigger trigger, IAnimationFunction function, ReferenceArrayList<IAnimationAnimator> animators,
        boolean repeating, boolean regressWhenInactive, Consumer<Animation> onRestart, Consumer<Animation> onActiveChanged, Consumer<Animation> onDone) {
        this.trigger = Objects.requireNonNull(trigger);
        this.function = Objects.requireNonNull(function);
        this.animators = ReferenceLists.unmodifiable(new ReferenceArrayList<>(animators));
        this.repeating = repeating;
        this.regressWhenInactive = regressWhenInactive;
        this.onRestart = onRestart;
        this.onActiveChanged = onActiveChanged;
        this.onDone = onDone;
    }

    public final void trigger() {
        trigger(0f, 0f, 0f, 0f);
    }

    public final void trigger(float gx, float gy, float width, float height) {
        boolean active = trigger.isTriggered(gx, gy, width, height);
        if (this.active == active) {
            return;
        }
        this.active = active;
        if (onActiveChanged != null) {
            onActiveChanged.accept(this);
        }
        this.elapsed = 0d;
        this.done = false;
    }

    public final boolean isRepeating() {
        return repeating;
    }

    public final boolean isActive() {
        return active;
    }

    public final boolean isDone() {
        return done;
    }

    public final double progress() {
        return progress;
    }

    public final void update(double delta) {
        if (done) {
            return;
        }
        if (!active) {
            if (progress == 0d) {
                return;
            }
            if (!regressWhenInactive) {
                progress = 0d;
                return;
            }
        }
        if (active && progress == 1d) {
            if (!repeating) {
                done = true;
                if (onDone != null) {
                    onDone.accept(this);
                }
                return;
            }
            elapsed = 0d;
            if (onRestart != null) {
                onRestart.accept(this);
            }
        }
        progress = Math.max(Math.min(function.animate(active, elapsed), 1d), 0d);
        elapsed += delta;
        for (IAnimationAnimator animator : animators) {
            animator.animate(active, progress);
        }
    }

}
