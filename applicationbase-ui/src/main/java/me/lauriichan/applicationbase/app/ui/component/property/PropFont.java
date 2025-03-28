package me.lauriichan.applicationbase.app.ui.component.property;

import imgui.ImFont;

public final class PropFont {

    private volatile ImFont font;

    public ImFont get() {
        return font;
    }
    
    public void set(ImFont font) {
        this.font = font;
    }
    
}
