package me.lauriichan.applicationbase.app.ui.component;

import imgui.ImDrawList;
import me.lauriichan.applicationbase.app.ui.component.renderer.ImageRenderer;

public class Image extends Component<Image> {

    public final ImageRenderer image = new ImageRenderer();

    @Override
    protected void renderForeground(ImDrawList drawList, float x, float y, float gx, float gy, float width, float height, int layerOffset) {
        image.render(drawList, gx, gy, width, height, layerOffset);
    }

    @Override
    protected int foregroundLayerAmount() {
        return image.layerAmount();
    }

}
