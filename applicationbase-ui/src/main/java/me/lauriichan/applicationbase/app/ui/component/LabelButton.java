package me.lauriichan.applicationbase.app.ui.component;

import me.lauriichan.applicationbase.app.ui.component.renderer.TextRenderer;
import imgui.ImDrawList;

public class LabelButton extends ButtonComponent<LabelButton> {

    public final TextRenderer label = new TextRenderer();
    
    @Override
    protected void renderForeground(ImDrawList drawList, float x, float y, float gx, float gy, float width, float height, int layerOffset) {
        label.render(drawList, gx, gy, width, height, layerOffset);
    }
    
    @Override
    protected int foregroundLayerAmount() {
        return label.layerAmount();
    }

}
