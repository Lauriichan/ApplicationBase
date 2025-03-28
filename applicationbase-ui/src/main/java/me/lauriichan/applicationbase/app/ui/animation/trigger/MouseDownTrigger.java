package me.lauriichan.applicationbase.app.ui.animation.trigger;

import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;

public final class MouseDownTrigger implements IAnimationTrigger {

    public static final MouseDownTrigger LEFT = new MouseDownTrigger(ImGuiMouseButton.Left);
    public static final MouseDownTrigger MIDDLE = new MouseDownTrigger(ImGuiMouseButton.Middle);
    public static final MouseDownTrigger RIGHT = new MouseDownTrigger(ImGuiMouseButton.Right);

    private final int button;

    private MouseDownTrigger(int button) {
        this.button = button;
    }

    @Override
    public boolean isTriggered(float gx, float gy, float width, float height) {
        return ImGui.isMouseDown(button) && ImGui.isMouseHoveringRect(gx, gy, gx + width, gy + height);
    }

}
