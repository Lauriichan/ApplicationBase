package me.lauriichan.applicationbase.app.ui.component.renderer;

import me.lauriichan.applicationbase.app.ui.DefaultConstants;
import me.lauriichan.applicationbase.app.ui.component.IRenderer;
import me.lauriichan.applicationbase.app.ui.component.property.PropBool;
import me.lauriichan.applicationbase.app.ui.component.property.PropFloat;
import me.lauriichan.applicationbase.app.ui.component.property.PropFont;
import me.lauriichan.applicationbase.app.ui.component.property.PropInt;
import me.lauriichan.applicationbase.app.ui.component.property.PropPadding;
import me.lauriichan.applicationbase.app.ui.component.property.PropString;

import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import imgui.ImVec2;
import me.lauriichan.applicationbase.app.util.color.SimpleColor;

public class TextRenderer implements IRenderer {

    public static final int ALIGN_HORIZONTAL_RIGHT = 0x1;
    public static final int ALIGN_HORIZONTAL_CENTER = 0x2;

    public static final int ALIGN_VERTICAL_BOTTOM = 0x4;
    public static final int ALIGN_VERTICAL_CENTER = 0x8;

    public final SimpleColor color = DefaultConstants.TEXT_COLOR.duplicate();

    public final PropFont font = new PropFont();
    public final PropFloat fontSize = new PropFloat(16f, 0f, Float.MAX_VALUE);

    public final PropString text = new PropString("Text");

    public final PropPadding padding = new PropPadding();
    public final PropInt alignment = new PropInt(ALIGN_HORIZONTAL_CENTER | ALIGN_VERTICAL_CENTER);

    public final PropBool fineClip = new PropBool(false);

    public final DelegateRenderer background = new DelegateRenderer();

    @Override
    public void render(ImDrawList drawList, float x, float y, float width, float height, int layerOffset) {
        ImFont renderFont = font.get();
        if (renderFont == null) {
            renderFont = ImGui.getFont();
        }
        x += padding.left();
        y += padding.top();
        width -= padding.right() + padding.left();
        height -= padding.bottom() + padding.top();
        float posX = x, posY = y;
        ImVec2 renderedSize = renderFont.calcTextSizeA(fontSize.get(), Float.MAX_VALUE, width, text.get());
        if (alignment.flag(ALIGN_HORIZONTAL_CENTER)) {
            posX = x + (width - renderedSize.x) / 2;
        } else if (alignment.flag(ALIGN_HORIZONTAL_RIGHT)) {
            posX = x + width - renderedSize.x;
        }
        if (alignment.flag(ALIGN_VERTICAL_CENTER)) {
            posY = y + (height - renderedSize.y) / 2;
        } else if (alignment.flag(ALIGN_VERTICAL_BOTTOM)) {
            posY = y + height - renderedSize.y;
        }
        background.render(drawList, posX, posY, renderedSize.x, renderedSize.y, layerOffset);
        renderFont.renderText(drawList, fontSize.get(), posX, posY, color.asABGR(), x, y, x + width, y + height, text.get(), null,
            fineClip.get());
    }

}
