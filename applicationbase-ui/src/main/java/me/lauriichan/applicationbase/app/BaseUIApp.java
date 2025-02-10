package me.lauriichan.applicationbase.app;

import java.io.File;

import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.internal.ImGui;
import imgui.internal.ImGuiContext;
import me.lauriichan.applicationbase.app.extension.IExtensionPool;
import me.lauriichan.applicationbase.app.ui.dock.DockNode;
import me.lauriichan.applicationbase.app.ui.dock.DockUIExtension;
import me.lauriichan.applicationbase.app.ui.dock.ImGuiDock;

public abstract class BaseUIApp extends BaseApp {

    public static final AppPhase PHASE_IMGUI_CONFIGURE_CORE = new AppPhase("imgui-configure", false);
    public static final AppPhase PHASE_IMGUI_CONFIGURE_APP = new AppPhase("imgui-configure", true);

    public static final AppPhase PHASE_IMGUI_SETUP_CORE = new AppPhase("imgui-setup", false);
    public static final AppPhase PHASE_IMGUI_SETUP_APP = new AppPhase("imgui-setup", true);

    public static final AppPhase PHASE_IMGUI_START_CORE = new AppPhase("imgui-start", false);
    public static final AppPhase PHASE_IMGUI_START_APP = new AppPhase("imgui-start", true);

    public static final AppPhase PHASE_IMGUI_UPDATE_PRE_CORE = new AppPhase("imgui-update-pre", false);
    public static final AppPhase PHASE_IMGUI_UPDATE_PRE_APP = new AppPhase("imgui-update-pre", true);

    public static final AppPhase PHASE_IMGUI_UPDATE_CORE = new AppPhase("imgui-update", false);
    public static final AppPhase PHASE_IMGUI_UPDATE_APP = new AppPhase("imgui-update", true);

    public static final AppPhase PHASE_IMGUI_UPDATE_POST_CORE = new AppPhase("imgui-update-post", false);
    public static final AppPhase PHASE_IMGUI_UPDATE_POST_APP = new AppPhase("imgui-update-post", true);

    public static final AppPhase PHASE_IMGUI_DISPOSE_APP = new AppPhase("imgui-dispose", true);
    public static final AppPhase PHASE_IMGUI_DISPOSE_CORE = new AppPhase("imgui-dispose", false);
    
    private volatile ImGuiHandle handle;
    
    private volatile IExtensionPool<DockUIExtension> dockUiPool;
    private volatile ImGuiDock imGuiDock;
    
    public BaseUIApp(File jarFile) {
        super(jarFile);
    }
    
    @Override
    protected void onCoreReady() throws Throwable {
        super.onCoreReady();
        logger().setDebug(true);
        dockUiPool = extension(DockUIExtension.class, true);
    }
    
    @Override
    protected void onCoreExecute() throws Throwable {
        super.onCoreExecute();
        DockNode node = ImGuiDock.newDock();
        createDock(node);
        imGuiDock = node.build();
    }
    
    @Override
    protected void onCorePostExecute() throws Throwable {
        (handle = new ImGuiHandle(this)).execute();
    }
    
    /*
     * Implementation
     */
    
    final void onImGuiConfigure(final ImGuiHandle.Config config) {
        try {
            onCoreImGuiConfigure(config);
        } catch (final Throwable throwable) {
            onAppError(PHASE_IMGUI_CONFIGURE_CORE, throwable);
        }
        try {
            onAppImGuiConfigure(config);
        } catch (final Throwable throwable) {
            onAppError(PHASE_IMGUI_CONFIGURE_APP, throwable);
        }
    }
    
    final void onGlfwSetup(final ImGuiHandle.Config config) {
        try {
            onCoreGlfwSetup(config);
        } catch (final Throwable throwable) {
            onAppError(PHASE_IMGUI_CONFIGURE_CORE, throwable);
        }
        try {
            onAppGlfwSetup(config);
        } catch (final Throwable throwable) {
            onAppError(PHASE_IMGUI_CONFIGURE_APP, throwable);
        }
    }
    
    final void onImGuiSetup(final ImGuiContext context, final ImGuiHandle.Config config) {
        try {
            onCoreImGuiSetup(context, config);
        } catch (final Throwable throwable) {
            onAppError(PHASE_IMGUI_SETUP_CORE, throwable);
        }
        try {
            onAppImGuiSetup(context, config);
        } catch (final Throwable throwable) {
            onAppError(PHASE_IMGUI_SETUP_APP, throwable);
        }
    }
    
    final void onImGuiStart() {
        try {
            onCoreImGuiStart(handle.handle());
        } catch (final Throwable throwable) {
            onAppError(PHASE_IMGUI_START_CORE, throwable);
        }
        try {
            onAppImGuiStart(handle.handle());
        } catch (final Throwable throwable) {
            onAppError(PHASE_IMGUI_START_APP, throwable);
        }
    }
    
    final void onPreUpdate() {
        try {
            onCorePreUpdate();
        } catch (final Throwable throwable) {
            onAppError(PHASE_IMGUI_UPDATE_PRE_CORE, throwable);
        }
        try {
            onAppPreUpdate();
        } catch (final Throwable throwable) {
            onAppError(PHASE_IMGUI_UPDATE_PRE_APP, throwable);
        }
    }
    
    final void onUpdate() {
        try {
            onCoreUpdate();
        } catch (final Throwable throwable) {
            onAppError(PHASE_IMGUI_UPDATE_CORE, throwable);
        }
        try {
            onAppUpdate();
        } catch (final Throwable throwable) {
            onAppError(PHASE_IMGUI_UPDATE_APP, throwable);
        }
    }
    
    final void onPostUpdate() {
        try {
            onCorePostUpdate();
        } catch (final Throwable throwable) {
            onAppError(PHASE_IMGUI_UPDATE_POST_CORE, throwable);
        }
        try {
            onAppPostUpdate();
        } catch (final Throwable throwable) {
            onAppError(PHASE_IMGUI_UPDATE_POST_APP, throwable);
        }
    
    }
    
    final void onDispose() {
        try {
            onAppDispose();
        } catch (final Throwable throwable) {
            onAppError(PHASE_IMGUI_DISPOSE_APP, throwable);
        }
        try {
            onCoreDispose();
        } catch (final Throwable throwable) {
            onAppError(PHASE_IMGUI_DISPOSE_CORE, throwable);
        }
    }
    
    /*
     * Core
     */

    protected void onCoreImGuiConfigure(final ImGuiHandle.Config config) throws Throwable {}

    protected void onCoreGlfwSetup(final ImGuiHandle.Config config) throws Throwable {}

    protected void onCoreImGuiSetup(final ImGuiContext context, final ImGuiHandle.Config config) throws Throwable {
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
    }
    
    protected void onCoreImGuiStart(long windowHandle) throws Throwable {}

    protected void onCorePreUpdate() throws Throwable {}

    protected void onCoreUpdate() throws Throwable {
        imGuiDock.render(this);
        dockUiPool().callInstances(dock -> dock.render(handle.handle()));
    }

    protected void onCorePostUpdate() throws Throwable {}

    protected void onCoreDispose() throws Throwable {}

    /*
     * App Abstraction
     */

    protected void onAppImGuiConfigure(final ImGuiHandle.Config config) throws Throwable {}

    protected void onAppGlfwSetup(final ImGuiHandle.Config config) throws Throwable {}

    protected void onAppImGuiSetup(final ImGuiContext context, final ImGuiHandle.Config config) throws Throwable {}
    
    protected void onAppImGuiStart(long windowHandle) throws Throwable {}

    protected void onAppPreUpdate() throws Throwable {}

    protected void onAppUpdate() throws Throwable {}

    protected void onAppPostUpdate() throws Throwable {}

    protected void onAppDispose() throws Throwable {}
    
    /*
     * Any abstraction 
     */
    
    protected void createDock(DockNode node) {}
    
    /*
     * Getter
     */
    
    public final ImGuiHandle handle() {
        return handle;
    }
    
    public final IExtensionPool<DockUIExtension> dockUiPool() {
        return dockUiPool;
    }

}
