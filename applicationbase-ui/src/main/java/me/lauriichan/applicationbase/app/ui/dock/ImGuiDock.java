package me.lauriichan.applicationbase.app.ui.dock;

import java.util.function.Function;

import imgui.internal.ImGui;
import imgui.internal.flag.ImGuiDockNodeFlags;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.type.ImInt;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lauriichan.applicationbase.app.ui.BaseUIApp;

public final class ImGuiDock {

    public static DockNode newDock() {
        return new DockNode();
    }

    static final class ImGuiDockNode {

        private final String dockId;

        private final float ratio;
        private final int splitDirection;
        private final ImGuiDockNode first, second;

        private final ImInt id = new ImInt();

        public ImGuiDockNode(final String dockId, final float ratio, final int splitDirection, final ImGuiDockNode first,
            final ImGuiDockNode second) {
            this.dockId = dockId;
            this.ratio = ratio;
            this.splitDirection = splitDirection;
            if (first == null && second != first) {
                throw new IllegalArgumentException("Either both nodes need to be defined or none");
            }
            this.first = first;
            this.second = second;
        }

    }

    private static final int DOCK_NODE_FLAGS = ImGuiDockNodeFlags.NoResize | ImGuiDockNodeFlags.NoCloseButton;
    private static final int DOCK_NODE_HIDDEN_FLAGS = DOCK_NODE_FLAGS | ImGuiDockNodeFlags.NoTabBar;

    private volatile ImGuiDockNode root;

    ImGuiDock(ImGuiDockNode root) {
        this.root = root;
    }

    public void render(BaseUIApp app) {
        if (root != null) {
            setup(app);
        }
        ImGui.dockSpaceOverViewport(ImGui.getMainViewport());
    }

    private void setup(BaseUIApp app) {
        ImGuiDockNode node = root;
        root = null;

        ImGuiViewport view = ImGui.getMainViewport();
        int dockId = ImGui.dockSpaceOverViewport(view);
        ImGui.dockBuilderRemoveNode(dockId);
        ImGui.dockBuilderAddNode(dockId);
        ImVec2 size = view.getSize();
        ImGui.dockBuilderSetNodeSize(dockId, size.x, size.y);
        node.id.set(dockId);

        Object2ObjectArrayMap<String, ObjectArrayList<String>> map = new Object2ObjectArrayMap<>();
        Function<String, ObjectArrayList<String>> builder = (ignore) -> new ObjectArrayList<String>();
        app.dockUiPool().callInstances(extension -> {
            ObjectArrayList<String> list = map.computeIfAbsent(extension.dockId(), builder);
            if (list.contains(extension.title())) {
                throw new IllegalArgumentException("Duplicated title: " + extension.title());
            }
            list.add(extension.title());
            System.out.println("Adding '" + extension.title() + "' to '" + extension.dockId() + "'");
        });

        if (node.first != null) {
            ObjectArrayList<ImGuiDockNode> queue = new ObjectArrayList<>();
            ObjectArrayList<ImGuiDockNode> windowDockQueue = new ObjectArrayList<>();
            queue.add(node);
            while (!queue.isEmpty()) {
                ImGuiDockNode next = queue.pop();
                if (next.first == null || next.second == null) {
                    continue;
                }
                System.out.println("Splitting '" + next.dockId + "': " + next.ratio);
                ImGui.dockBuilderSplitNode(next.id.get(), next.splitDirection, next.ratio, next.first.id, next.second.id);
                if (next.first.first != null) {
                    queue.push(next.first);
                } else {
                    windowDockQueue.push(next.first);
                }
                if (next.second.first != null) {
                    queue.push(next.second);
                } else {
                    windowDockQueue.push(next.second);
                }
            }
            ImGuiDockNode dock;
            while (!windowDockQueue.isEmpty()) {
                dock = windowDockQueue.pop();
                System.out.println("Docking: " + dock.dockId);
                dock(dock.id.get(), map.get(dock.dockId));
            }
        } else {
            dock(dockId, map.get("root"));
        }

        ImGui.dockBuilderFinish(dockId);
    }

    private void dock(int id, ObjectArrayList<String> list) {
        ImGui.dockBuilderGetNode(id).setLocalFlags(list == null || list.size() <= 1 ? DOCK_NODE_HIDDEN_FLAGS : DOCK_NODE_FLAGS);
        if (list != null) {
            for (String window : list) {
                System.out.println("Docking '" + window + "' to '" + id + "'");
                ImGui.dockBuilderDockWindow(window, id);
            }
        }
    }

}
