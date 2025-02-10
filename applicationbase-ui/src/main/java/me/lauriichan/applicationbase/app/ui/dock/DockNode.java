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
        return new ImGuiDock(buildNode());
    }

    private static record StackNode(float ratio, DockNode leftNode, DockNode rightNode) {}

    private ImGuiDockNode buildNode() {
        if (children.isEmpty()) {
            if (parent != null && (id == null || id.isBlank())) {
                throw new IllegalArgumentException("Child has null id");
            }
            return new ImGuiDockNode(parent == null ? "root" : id, 1f, 0, null, null);
        }
        if (children.size() == 1) {
            return children.get(0).weight(1f).buildNode();
        }
        ObjectArrayList<DockNode> childStack = new ObjectArrayList<>();
        float sum = 0f;
        for (DockNode child : children) {
            sum += child.weight();
            childStack.add(child);
        }
        ObjectArrayList<StackNode> outStack = new ObjectArrayList<>();
        while (!childStack.isEmpty()) {
            DockNode current = childStack.removeFirst();
            float weight = current.weight() / sum;
            if (childStack.size() == 1) {
                outStack.push(new StackNode(weight, current, childStack.removeFirst()));
                continue;
            }
            sum -= current.weight();
            outStack.push(new StackNode(weight, current, new DockNode(this).id("temp-" + current.id)));
        }
        ImGuiDockNode last = null;
        while (!outStack.isEmpty()) {
            StackNode stackNode = outStack.pop();
            String nodeId = id;
            if (!outStack.isEmpty()) {
                nodeId = outStack.peek(0).rightNode().id();
            }
            last = new ImGuiDockNode(nodeId, stackNode.ratio(), direction.numValue(), stackNode.leftNode().buildNode(),
                last != null ? last : stackNode.rightNode().buildNode());
        }
        return last;
    }

}
