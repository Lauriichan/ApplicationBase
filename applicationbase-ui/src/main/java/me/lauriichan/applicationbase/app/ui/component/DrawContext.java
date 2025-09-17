package me.lauriichan.applicationbase.app.ui.component;

import me.lauriichan.applicationbase.app.ui.DefaultConstants;
import me.lauriichan.applicationbase.app.ui.component.property.PropFloat;
import me.lauriichan.applicationbase.app.ui.component.property.PropInt;

import imgui.ImGui;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

public final class DrawContext {

    public static DrawContext vertical() {
        return new DrawContext(true);
    }

    public static DrawContext horizontal() {
        return new DrawContext(false);
    }

    private static class CalculatedComponent {

        private Component<?> component;
        private float width, height;
        private PropInt flags;

    }

    private final ReferenceArrayList<CalculatedComponent> components = new ReferenceArrayList<>();
    private final boolean vertical;

    public final PropFloat itemSpacing = new PropFloat(DefaultConstants.ITEM_SPACING.get(), 0f, Float.MAX_VALUE);

    private DrawContext(final boolean vertical) {
        this.vertical = vertical;
    }

    public final float itemSpacing() {
        return itemSpacing.get();
    }

    public final DrawContext itemSpacing(float itemSpacing) {
        this.itemSpacing.set(itemSpacing);
        return this;
    }

    public final void addToContext(Component<?> component) {
        float width = component.width.get(), height = component.height.get();
        PropInt flags = component.flags;
        if (flags.flag(Component.MIRROR_HEIGHT)) {
            if (flags.flag(Component.MIRROR_WIDTH)) {
                throw new IllegalStateException("width and height can't be both same as other.");
            }
            if (!flags.flag(Component.GRAB_HEIGHT)) {
                width = height;
            }
        }
        if (flags.flag(Component.MIRROR_WIDTH) && !flags.flag(Component.GRAB_WIDTH)) {
            height = width;
        }
        CalculatedComponent calc = new CalculatedComponent();
        calc.component = component;
        calc.width = width;
        calc.height = height;
        calc.flags = flags;
        components.add(calc);
    }

    public final void render(float xOffset, float yOffset, float maxX, float maxY) {
        try {
            internalRender(xOffset, yOffset, maxX, maxY);
        } finally {
            components.clear();
        }
    }

    public final void render(float maxX, float maxY) {
        try {
            internalRender(0, 0, maxX, maxY);
        } finally {
            components.clear();
        }
    }

    private void internalRender(float xOffset, float yOffset, float maxX, float maxY) {
        float itemSpacing = this.itemSpacing.get();
        float winX = ImGui.getWindowPosX() + xOffset, winY = ImGui.getWindowPosY() + yOffset;
        float reservedHeight = vertical ? (components.size() - 1) * itemSpacing : 0f;
        float reservedWidth = vertical ? 0f : (components.size() - 1) * itemSpacing;
        float grabHeight = 0f;
        float grabWidth = 0f;
        for (CalculatedComponent calc : components) {
            if (calc.flags.flag(Component.GRAB_WIDTH)) {
                grabWidth += 1f;
            } else if (calc.width >= 0f) {
                reservedWidth += calc.width;
            }
            if (calc.flags.flag(Component.GRAB_HEIGHT)) {
                grabHeight += 1f;
            } else if (calc.height >= 0f) {
                reservedWidth += calc.width;
            }
        }
        if (vertical) {
            if (grabHeight != 0f) {
                grabHeight = (maxY - reservedHeight) / grabHeight;
            }
            grabWidth = maxX - reservedWidth;
        } else {
            if (grabWidth != 0f) {
                grabWidth = (maxX - reservedWidth) / grabWidth;
            }
            grabHeight = maxY - reservedHeight;
        }
        float min = 0f, max = vertical ? maxY : maxX;
        float x, y;
        for (CalculatedComponent calc : components) {
            if (calc.flags.flag(Component.GRAB_WIDTH)) {
                calc.width = grabWidth;
                if (calc.flags.flag(Component.MIRROR_WIDTH)) {
                    calc.height = grabWidth;
                }
            }
            if (calc.flags.flag(Component.GRAB_HEIGHT)) {
                calc.height = grabHeight;
                if (calc.flags.flag(Component.MIRROR_HEIGHT)) {
                    calc.width = grabHeight;
                }
            }
            calc.width -= calc.component.padding.left() + calc.component.padding.right();
            calc.height -= calc.component.padding.top() + calc.component.padding.bottom();
            if (vertical) {
                if (calc.flags.flag(Component.ALIGN_BOTTOM)) {
                    y = max - calc.component.padding.bottom() - calc.height;
                    max -= calc.height + itemSpacing;
                } else {
                    y = min + calc.component.padding.top();
                    min += calc.height + itemSpacing;
                }
                if (calc.flags.flag(Component.ALIGN_RIGHT)) {
                    x = maxX - calc.component.padding.right() - calc.width;
                } else {
                    x = calc.component.padding.left();
                }
            } else {
                if (calc.flags.flag(Component.ALIGN_RIGHT)) {
                    x = max - calc.component.padding.right() - calc.width;
                    max -= calc.width + itemSpacing;
                } else {
                    x = min + calc.component.padding.left();
                    min += calc.width + itemSpacing;
                }
                if (calc.flags.flag(Component.ALIGN_BOTTOM)) {
                    y = maxY - calc.component.padding.bottom() - calc.height;
                } else {
                    y = calc.component.padding.top();
                }
            }
            ImGui.setCursorPos(x, y);
            ImGui.setNextItemWidth(calc.width);
            if (ImGui.beginChild(calc.component.hashId())) {
                calc.component.render(x, y, winX + x, winY + y, calc.width, calc.height);
            }
            ImGui.endChild();
            ImGui.setCursorPos(0, 0);
        }
    }

}
