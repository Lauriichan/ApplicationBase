package me.lauriichan.applicationbase.app.ui.component;

import imgui.ImDrawList;
import me.lauriichan.applicationbase.app.ui.component.renderer.DelegateRenderer;

public final class CustomComponent extends Component<CustomComponent> {

    public final DelegateRenderer foreground = new DelegateRenderer();

    @Override
    protected void renderForeground(ImDrawList drawList, float x, float y, float gx, float gy, float width, float height, int layerOffset) {
        foreground.render(drawList, gx, gy, width, height, layerOffset);
    }

    @Override
    protected int foregroundLayerAmount() {
        return foreground.layerAmount();
    }

}
