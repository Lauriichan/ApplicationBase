package me.lauriichan.applicationbase.app.ui.component;

import imgui.ImDrawList;
import imgui.ImGui;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import me.lauriichan.applicationbase.app.ui.component.property.PropFloat;
import me.lauriichan.applicationbase.app.ui.component.property.PropPadding;
import me.lauriichan.applicationbase.app.ui.component.renderer.BoxRenderer;

public class ScrollListContainer extends Component<ScrollListContainer> {

    public final PropPadding itemPadding = new PropPadding(4f, 4f, 2f, 2f);
    public final PropFloat scroll = new PropFloat(0f, 0f, Float.MAX_VALUE);

    public final PropPadding scrollBarPadding = new PropPadding(4f);
    public final PropFloat scrollBarWidth = new PropFloat(4f, 2f, Float.MAX_VALUE);

    public final BoxRenderer scrollBarThumb = new BoxRenderer();
    public final BoxRenderer scrollBarBar = new BoxRenderer();

    private final ReferenceArrayList<Component<?>> components = new ReferenceArrayList<>();
    private volatile float maxScroll = 0f;

    public void add(Component<?> component) {
        if (components.contains(component)) {
            throw new IllegalArgumentException("Already known component");
        }
        components.add(component);
    }

    @Override
    protected void componentAction(float gx, float gy, float width, float height) {
        float scrollBarSize = scrollBarPadding.left() + scrollBarPadding.right() + scrollBarWidth.get();
        if (ImGui.isMouseHoveringRect(gx + width - scrollBarSize, gy, gx + width, gy + height)) {
            scroll.set(scroll.get() + ImGui.getIO().getMouseWheel());
        }
    }

    @Override
    protected void renderForeground(ImDrawList drawList, float x, float y, float gx, float gy, float width, float height, int layerOffset) {
        if (maxScroll == 0f) {
            return;
        }
        float actualWidth = scrollBarWidth.get() - scrollBarPadding.left() + scrollBarPadding.right();
        float actualHeight = height - scrollBarPadding.top() + scrollBarPadding.bottom();
        float scrollBarX = x + (width - scrollBarPadding.left() + scrollBarPadding.right() + scrollBarWidth.get());
        float thumbHeight = Math.max(height / maxScroll, scrollBarWidth.get());
        scrollBarBar.render(drawList, scrollBarX, y, actualWidth, actualHeight, layerOffset);
        scrollBarThumb.render(drawList, scrollBarX, y + (scroll.get() / maxScroll) * (height - thumbHeight), actualWidth, thumbHeight,
            layerOffset + 1);
    }

    @Override
    protected int foregroundLayerAmount() {
        return 2;
    }

    @Override
    protected void renderExtras(float x, float y, float gx, float gy, float width, float height) {
        float scrollBarSize = scrollBarPadding.left() + scrollBarPadding.right() + scrollBarWidth.get();
        width -= scrollBarSize;
        float compY = 0, compAddY, compTop, compLeft, compWidth, compHeight;
        for (Component<?> component : components) {
            compWidth = component.flags.flag(Component.GRAB_WIDTH) ? width : component.width.get();
            compHeight = component.flags.flag(Component.GRAB_HEIGHT) ? height : component.height.get();
            if (component.flags.flag(Component.MIRROR_HEIGHT)) {
                if (component.flags.flag(Component.MIRROR_WIDTH)) {
                    throw new IllegalStateException("width and height can't be both same as other.");
                }
                if (!component.flags.flag(Component.GRAB_HEIGHT)) {
                    compWidth = compHeight;
                }
            }
            if (component.flags.flag(Component.MIRROR_WIDTH) && !component.flags.flag(Component.GRAB_WIDTH)) {
                compHeight = compWidth;
            }
            compAddY = compHeight;
            compWidth -= (compLeft = component.padding.left() + itemPadding.left()) + component.padding.right() + itemPadding.right();
            compHeight -= (compTop = component.padding.top() + itemPadding.top()) + component.padding.bottom() + itemPadding.bottom();
            ImGui.setCursorPos(x + compLeft, y + compY + compTop - scroll.get());
            ImGui.setNextItemWidth(compWidth);
            ImGui.pushClipRect(gx + compLeft, gy + compTop, gx + compLeft + scrollBarSize + compWidth,
                gy + compTop + compHeight, true);
            if (ImGui.beginChild(component.hashId())) {
                component.render(x + compLeft, y + compY + compTop - scroll.get(), gx + compLeft + scrollBarSize,
                    gy + compTop, compWidth, compHeight);
            }
            ImGui.endChild();
            ImGui.popClipRect();
            compY += compAddY;
        }
        ImGui.setCursorPos(x, y);
        maxScroll = Math.max(compY - height, 0f);
        if (scroll.get() > maxScroll) {
            scroll.set(maxScroll);
        }
    }

}
