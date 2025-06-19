package me.lauriichan.applicationbase.app.ui.component;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import me.lauriichan.applicationbase.app.ui.component.property.PropBool;
import me.lauriichan.applicationbase.app.ui.component.renderer.DelegateRenderer;

public class ToggleButtonComponent extends Component<ToggleButtonComponent> {

    public final PropBool enabled = new PropBool(false);

    public final DelegateRenderer disabledRenderer = new DelegateRenderer();
    public final DelegateRenderer enabledRenderer = new DelegateRenderer();

    private volatile boolean renderEnabled;

    @Override
    protected void componentAction(float gx, float gy, float width, float height) {
        if (ImGui.isMouseReleased(ImGuiMouseButton.Left) && ImGui.isMouseHoveringRect(gx, gy, gx + width, gy + height)) {
            // Execute action
            enabled.set(!enabled.get());
        }
    }

    @Override
    protected void renderForeground(ImDrawList drawList, float x, float y, float gx, float gy, float width, float height, int layerOffset) {
        if (renderEnabled) {
            enabledRenderer.render(drawList, gx, gy, width, height, layerOffset);
        } else {
            disabledRenderer.render(drawList, gx, gy, width, height, layerOffset);
        }
    }

    @Override
    protected int foregroundLayerAmount() {
        if (renderEnabled = enabled.get()) {
            return enabledRenderer.layerAmount();
        } else {
            return disabledRenderer.layerAmount();
        }
    }

}
