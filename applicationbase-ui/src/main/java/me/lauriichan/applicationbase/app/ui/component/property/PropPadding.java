package me.lauriichan.applicationbase.app.ui.component.property;

public final class PropPadding {

    public final PropFloat left = new PropFloat(0f, 0f, Float.MAX_VALUE);
    public final PropFloat right = new PropFloat(0f, 0f, Float.MAX_VALUE);
    public final PropFloat top = new PropFloat(0f, 0f, Float.MAX_VALUE);
    public final PropFloat bottom = new PropFloat(0f, 0f, Float.MAX_VALUE);
    
    public PropPadding() {}
    
    public PropPadding(float value) {
        left.set(value);
        right.set(value);
        top.set(value);
        bottom.set(value);
    }
    
    public PropPadding(float left, float right, float top, float bottom) {
        this.left.set(left);
        this.right.set(right);
        this.top.set(top);
        this.bottom.set(bottom);
    }

    public PropPadding set(float value) {
        left.set(value);
        right.set(value);
        top.set(value);
        bottom.set(value);
        return this;
    }

    public float left() {
        return left.get();
    }

    public PropPadding left(float value) {
        left.set(value);
        return this;
    }

    public float right() {
        return right.get();
    }

    public PropPadding right(float value) {
        right.set(value);
        return this;
    }

    public float top() {
        return top.get();
    }

    public PropPadding top(float value) {
        top.set(value);
        return this;
    }

    public float bottom() {
        return bottom.get();
    }

    public PropPadding bottom(float value) {
        bottom.set(value);
        return this;
    }

}
