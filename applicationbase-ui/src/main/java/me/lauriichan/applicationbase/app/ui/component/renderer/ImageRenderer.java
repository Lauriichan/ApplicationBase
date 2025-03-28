package me.lauriichan.applicationbase.app.ui.component.renderer;

import imgui.ImDrawList;
import me.lauriichan.applicationbase.app.ui.component.IRenderer;
import me.lauriichan.applicationbase.app.ui.component.property.PropInt;
import me.lauriichan.applicationbase.app.ui.component.property.PropPadding;
import me.lauriichan.applicationbase.app.util.color.SimpleColor;

public class ImageRenderer implements IRenderer {
    
    public final SimpleColor tintColor = SimpleColor.sRGB(1d, 1d, 1d, 1d);
    
    public final PropPadding padding = new PropPadding();
    
    public final PropInt texture = new PropInt();

    @Override
    public void render(ImDrawList drawList, float x, float y, float width, float height, int layerOffset) {
        x += padding.left();
        y += padding.top();
        width -= padding.right() + padding.left();
        height -= padding.bottom() + padding.top();
        drawList.addImage(texture.get(), x, y, x + width, y + height, 0f, 0f, 1f, 1f, tintColor.asABGR());
    }

}
