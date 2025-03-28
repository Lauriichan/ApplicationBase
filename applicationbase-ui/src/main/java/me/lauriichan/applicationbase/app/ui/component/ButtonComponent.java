package me.lauriichan.applicationbase.app.ui.component;

import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;

public abstract class ButtonComponent<T extends ButtonComponent<T>> extends Component<T> {

    private volatile Runnable action;

    public void action(Runnable action) {
        this.action = action;
    }

    public Runnable action() {
        return action;
    }
    
    @Override
    protected void componentAction(float gx, float gy, float width, float height) {
        if (action != null && ImGui.isMouseReleased(ImGuiMouseButton.Left) && ImGui.isMouseHoveringRect(gx, gy, gx + width, gy + height)) {
            // Execute action
            action.run();
        }
    }
    
}
