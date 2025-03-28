package me.lauriichan.applicationbase.app.ui.component.renderer;

import me.lauriichan.applicationbase.app.ui.DefaultConstants;
import me.lauriichan.applicationbase.app.ui.component.IRenderer;
import me.lauriichan.applicationbase.app.ui.component.property.PropFloat;
import me.lauriichan.applicationbase.app.ui.component.property.PropPadding;
import imgui.ImDrawList;
import me.lauriichan.applicationbase.app.util.color.SimpleColor;

public class BoxRenderer implements IRenderer {

    public final SimpleColor color = DefaultConstants.WINDOW_BACKGROUND_COLOR.duplicate();

    public final PropPadding padding = new PropPadding();

    public final PropFloat rounding = new PropFloat(5f, 0f, Float.MAX_VALUE);

    @Override
    public void render(ImDrawList drawList, float x, float y, float width, float height, int layerOffset) {
        drawList.addRectFilled(x + padding.left(), y + padding.top(), x + width - padding.right(),
            y + height - padding.bottom(), color.asABGR(), rounding.get());
    }

}
