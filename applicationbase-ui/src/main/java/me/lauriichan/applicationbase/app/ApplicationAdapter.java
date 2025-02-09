package me.lauriichan.applicationbase.app;

import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.internal.ImGuiContext;

final class ApplicationAdapter extends Application {

    private final BaseUIApp app;

    public ApplicationAdapter(BaseUIApp app) {
        this.app = app;
    }

    @Override
    protected void configure(Configuration config) {
        app.onImGuiConfigure(config);
    }

    @Override
    protected void initImGui(Configuration config) {
        ImGuiContext context = ImGui.createContext();
        app.onImGuiSetup(context, config);
    }

    @Override
    protected void preRun() {
        app.onImGuiStart();
    }

    @Override
    protected void preProcess() {
        app.onPreUpdate();
    }

    @Override
    public void process() {
        app.onUpdate();
    }

    @Override
    protected void postProcess() {
        app.onPostUpdate();
    }

    @Override
    protected void postRun() {
        app.shutdown();
    }

    @Override
    protected void dispose() {
        super.dispose();
        app.onDispose();
    }

}
