package me.lauriichan.applicationbase.app.ui.property;

import java.util.Objects;

public final class PropString extends Property<PropString> {

    private volatile String value = "";

    public String get() {
        return value;
    }

    public PropString set(String value) {
        this.value = Objects.requireNonNull(value);
        return this;
    }

    @Override
    protected void internalUpdate(double ratio) {}

}
