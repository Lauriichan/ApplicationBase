package me.lauriichan.applicationbase.app.ui.component;

import java.util.function.Consumer;

import me.lauriichan.applicationbase.app.ui.animation.Animation;
import me.lauriichan.applicationbase.app.ui.component.property.PropFloat;
import me.lauriichan.applicationbase.app.ui.component.property.PropInt;
import me.lauriichan.applicationbase.app.ui.component.property.PropPadding;
import me.lauriichan.applicationbase.app.ui.component.renderer.DelegateRenderer;
import imgui.ImDrawList;
import imgui.ImGui;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;

@SuppressWarnings("unchecked")
public abstract class Component<T extends Component<T>> {

    public static final int GRAB_WIDTH = 0x1;
    public static final int GRAB_HEIGHT = 0x2;
    public static final int MIRROR_WIDTH = 0x4;
    public static final int MIRROR_HEIGHT = 0x8;
    public static final int ALIGN_RIGHT = 0x10;
    public static final int ALIGN_BOTTOM = 0x20;

    public final PropInt flags = new PropInt(GRAB_WIDTH | GRAB_HEIGHT);
    
    public final PropPadding padding = new PropPadding();

    public final PropFloat width = new PropFloat(0f, 0f, Float.MAX_VALUE);
    public final PropFloat height = new PropFloat(0f, 0f, Float.MAX_VALUE);

    public final DelegateRenderer background = new DelegateRenderer();

    private final String hashId = Integer.toHexString(hashCode());

    private final ObjectList<Animation> animations = ObjectLists.synchronize(new ObjectArrayList<>(4));

    public T setup(Consumer<T> setup) {
        setup.accept((T) this);
        return (T) this;
    }

    public final void addAnimation(Animation animation) {
        if (animations.contains(animation)) {
            throw new IllegalArgumentException("Animation already set");
        }
        animations.add(animation);
    }

    public final void removeAnimation(Animation animation) {
        animations.remove(animation);
    }

    public final void clearAnimations() {
        animations.clear();
    }

    public final String hashId() {
        return hashId;
    }

    public final void render(float x, float y, float gx, float gy, float width, float height) {
        for (Animation animation : animations) {
            animation.trigger(gx, gy, width, height);
        }
        componentAction(gx, gy, width, height);
        ImDrawList drawList = ImGui.getForegroundDrawList();
        int bgLayers = Math.max(backgroundLayerAmount(), 1);
        drawList.channelsSplit(bgLayers + Math.max(foregroundLayerAmount(), 1));
        drawList.channelsSetCurrent(0);
        renderBackground(drawList, x, y, gx, gy, width, height, 0);
        drawList.channelsSetCurrent(bgLayers);
        renderForeground(drawList, x, y, gx, gy, width, height, bgLayers);
        drawList.channelsMerge();
    }

    protected void componentAction(float gx, float gy, float width, float height) {}

    protected void renderBackground(ImDrawList drawList, float x, float y, float gx, float gy, float width, float height, int layerOffset) {
        background.render(drawList, gx, gy, width, height, layerOffset);
    }

    protected int backgroundLayerAmount() {
        return background.layerAmount();
    }

    protected void renderForeground(ImDrawList drawList, float x, float y, float gx, float gy, float width, float height, int layerOffset) {}

    protected int foregroundLayerAmount() {
        return 1;
    }

    public final void update(double delta) {
        onUpdate(delta);
        for (Animation animation : animations) {
            animation.update(delta);
        }
    }

    protected void onUpdate(double delta) {}

}