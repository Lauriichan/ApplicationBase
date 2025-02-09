package me.lauriichan.applicationbase.app.ui.dock;

import java.util.Objects;

import imgui.flag.ImGuiDir;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lauriichan.applicationbase.app.ui.dock.ImGuiDock.ImGuiDockNode;

public final class DockNode {

    public static enum SplitDirection {
        HORIZONTAL(ImGuiDir.Left),
        VERTICAL(ImGuiDir.Up);

        private final int numValue;

        private SplitDirection(int numValue) {
            this.numValue = numValue;
        }

        public int numValue() {
            return numValue;
        }
    }

    private final DockNode parent;
    private final ObjectArrayList<DockNode> children = new ObjectArrayList<>();

    private String id;
    private float weight = 1f;
    private SplitDirection direction = SplitDirection.HORIZONTAL;

    DockNode() {
        this.parent = null;
        this.id = "root";
    }

    private DockNode(DockNode parent) {
        this.parent = parent;
    }

    public DockNode newChild() {
        DockNode node = new DockNode(this);
        children.add(node);
        return node;
    }

    public String id() {
        return id;
    }

    public DockNode id(String id) {
        this.id = id;
        return this;
    }

    public SplitDirection direction() {
        return direction;
    }

    public DockNode direction(SplitDirection direction) {
        this.direction = Objects.requireNonNull(direction);
        return this;
    }

    public float weight() {
        return weight;
    }

    public DockNode weight(float weight) {
        this.weight = weight;
        return this;
    }

    public DockNode parent() {
        return parent;
    }

    public ImGuiDock build() {
        if (parent != null) {
            return parent.build();
        }
        return new ImGuiDock(buildNode(1f));
    }

    private ImGuiDockNode buildNode(float total) {
        switch (children.size()) {
        case 0:
            if (parent != null && (id == null || id.isBlank())) {
                throw new IllegalArgumentException("Child has null id");
            }
            return new ImGuiDockNode(parent == null ? "root" : id, weight / total, 0, null, null);
        case 1:
            return children.get(0).buildNode(1f);
        case 2:
            float sum = children.get(0).weight() + children.get(1).weight();
            return new ImGuiDockNode(id, weight / total, direction.numValue(), children.get(0).buildNode(sum),
                children.get(1).buildNode(sum));
        }
        float sum = 0f;
        for (DockNode child : children) {
            sum += child.weight();
        }
        ObjectArrayList<DockNode> stack = new ObjectArrayList<>();
        float temp;
        for (int i = 0; i < children.size(); i++) {
            DockNode node = children.get(i);
            temp = node.weight();
            node.weight(temp / sum);
            sum -= temp;
            stack.push(node);
            if (sum == 0f) {
                break;
            }
            stack.push(new DockNode(this).weight(1f - node.weight()).id("temp-" + node.id));
        }
        DockNode first = stack.remove(0);
        DockNode node = stack.pop();
        ImGuiDockNode other = node.buildNode(1f);
        while (!stack.isEmpty()) {
            node = stack.pop();
            other = new ImGuiDockNode(node.id, node.weight(), direction.numValue(), node.buildNode(1f), other);
        }
        return new ImGuiDockNode(id, first.weight(), direction.numValue(), first.buildNode(1f), other);
    }

}
