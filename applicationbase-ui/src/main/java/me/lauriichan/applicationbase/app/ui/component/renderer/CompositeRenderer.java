package me.lauriichan.applicationbase.app.ui.component.renderer;

import java.util.Objects;

import imgui.ImDrawList;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import it.unimi.dsi.fastutil.objects.ReferenceLists;
import me.lauriichan.applicationbase.app.ui.component.IRenderer;

public final class CompositeRenderer implements IRenderer {

    public static CompositeRenderer of(IRenderer... renderers) {
        final ReferenceArrayList<IRenderer> list = new ReferenceArrayList<>();
        for (IRenderer renderer : renderers) {
            if (renderer == null) {
                continue;
            }
            list.add(renderer);
        }
        return new CompositeRenderer(list);
    }

    public static CompositeRenderer.Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final ReferenceArrayList<IRenderer> renderers = new ReferenceArrayList<>();

        private Builder() {}

        public Builder add(IRenderer renderer) {
            renderers.add(Objects.requireNonNull(renderer));
            return this;
        }

        public CompositeRenderer build() {
            return new CompositeRenderer(renderers);
        }

    }

    private final ReferenceList<IRenderer> renderers;
    private final int layerAmount;

    private CompositeRenderer(ReferenceArrayList<IRenderer> renderers) {
        this.renderers = ReferenceLists.unmodifiable(renderers);
        this.layerAmount = renderers.stream().mapToInt(IRenderer::layerAmount).sum();
    }

    @Override
    public void render(ImDrawList drawList, float x, float y, float width, float height, int layerOffset) {
        int currentLayer = layerOffset;
        for (IRenderer renderer : renderers) {
            drawList.channelsSetCurrent(currentLayer);
            renderer.render(drawList, x, y, width, height, currentLayer);
            currentLayer += renderer.layerAmount();
        }
    }

    @Override
    public int layerAmount() {
        return layerAmount;
    }

}
