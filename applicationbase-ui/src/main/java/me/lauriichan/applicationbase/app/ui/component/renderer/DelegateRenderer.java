package me.lauriichan.applicationbase.app.ui.component.renderer;

import imgui.ImDrawList;
import me.lauriichan.applicationbase.app.ui.component.IRenderer;

public class DelegateRenderer implements IRenderer {

    private IRenderer renderer = null;

    public DelegateRenderer() {}

    public DelegateRenderer(IRenderer renderer) {
        this.renderer = renderer;
    }

    public boolean has() {
        return renderer != null;
    }

    public IRenderer get() {
        return renderer;
    }

    public <R> R get(Class<R> type) {
        return type.cast(renderer);
    }

    public void set(IRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public int layerAmount() {
        return renderer == null ? 0 : renderer.layerAmount();
    }

    @Override
    public void render(ImDrawList drawList, float x, float y, float width, float height, int layerOffset) {
        if (renderer == null) {
            return;
        }
        renderer.render(drawList, x, y, width, height, layerOffset);
    }

}
