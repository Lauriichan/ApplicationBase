package me.lauriichan.applicationbase.app.ui.property;

import imgui.ImFont;

public final class PropFont extends Property<PropFont> {

    private volatile ImFont font;

    public ImFont get() {
        return font;
    }

    public void set(ImFont font) {
        this.font = font;
    }

    @Override
    protected void internalUpdate(double ratio) {}

}
