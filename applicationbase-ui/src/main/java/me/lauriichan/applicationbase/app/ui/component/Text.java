package me.lauriichan.applicationbase.app.ui.component;

import imgui.ImDrawList;
import me.lauriichan.applicationbase.app.ui.component.renderer.TextRenderer;

public class Text extends Component<Text> {

    public final TextRenderer text = new TextRenderer();

    @Override
    protected void renderForeground(ImDrawList drawList, float x, float y, float gx, float gy, float width, float height, int layerOffset) {
        text.render(drawList, gx, gy, width, height, layerOffset);
    }

    @Override
    protected int foregroundLayerAmount() {
        return text.layerAmount();
    }

}
