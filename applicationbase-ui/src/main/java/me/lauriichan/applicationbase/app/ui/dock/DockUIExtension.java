package me.lauriichan.applicationbase.app.ui.dock;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import me.lauriichan.applicationbase.app.extension.ExtensionPoint;
import me.lauriichan.applicationbase.app.extension.IExtension;

@ExtensionPoint
public abstract class DockUIExtension implements IExtension {

    private final String title;
    private final String dockId;

    public DockUIExtension(String title, String dockId) {
        this.title = title;
        this.dockId = dockId;
    }

    public final String title() {
        return title;
    }

    public final String dockId() {
        return dockId;
    }

    public final void render() {
        if (ImGui.begin(title, windowFlags())) {
            renderContent();
        }
        ImGui.end();
    }

    protected int windowFlags() {
        return ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse;
    }

    protected abstract void renderContent();

}
