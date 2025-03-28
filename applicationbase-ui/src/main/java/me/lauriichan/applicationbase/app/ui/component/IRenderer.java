package me.lauriichan.applicationbase.app.ui.component;

import imgui.ImDrawList;

@FunctionalInterface
public interface IRenderer {

    void render(ImDrawList drawList, float x, float y, float width, float height, int layerOffset);

    default int layerAmount() {
        return 1;
    }

}
