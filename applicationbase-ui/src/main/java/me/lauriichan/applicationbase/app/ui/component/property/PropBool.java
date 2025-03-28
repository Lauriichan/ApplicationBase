package me.lauriichan.applicationbase.app.ui.component.property;

public final class PropBool {

    private volatile boolean value = false;

    public PropBool() {}

    public PropBool(boolean value) {
        this.value = value;
    }

    public boolean get() {
        return value;
    }

    public void set(boolean value) {
        this.value = value;
    }

}
