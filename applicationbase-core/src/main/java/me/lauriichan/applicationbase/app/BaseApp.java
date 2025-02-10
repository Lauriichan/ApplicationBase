package me.lauriichan.applicationbase.app;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lauriichan.applicationbase.app.ExtensionPoolImpl.ConditionMapImpl;
import me.lauriichan.applicationbase.app.config.ConfigManager;
import me.lauriichan.applicationbase.app.config.ConfigMigrator;
import me.lauriichan.applicationbase.app.config.ConfigWrapper;
import me.lauriichan.applicationbase.app.config.startup.CompositeProperty;
import me.lauriichan.applicationbase.app.config.startup.IPropertyIO;
import me.lauriichan.applicationbase.app.config.startup.Property;
import me.lauriichan.applicationbase.app.config.startup.StartupConfig;
import me.lauriichan.applicationbase.app.config.startup.ValueProperty;
import me.lauriichan.applicationbase.app.data.DataManager;
import me.lauriichan.applicationbase.app.data.DataMigrator;
import me.lauriichan.applicationbase.app.extension.IConditionMap;
import me.lauriichan.applicationbase.app.extension.IExtension;
import me.lauriichan.applicationbase.app.extension.IExtensionPool;
import me.lauriichan.applicationbase.app.io.IOManager;
import me.lauriichan.applicationbase.app.resource.ResourceManager;
import me.lauriichan.applicationbase.app.resource.source.FileDataSource;
import me.lauriichan.applicationbase.app.resource.source.IDataSource;
import me.lauriichan.applicationbase.app.resource.source.PathDataSource;
import me.lauriichan.applicationbase.app.signal.ISignalHandlerExtension;
import me.lauriichan.applicationbase.app.signal.SignalManager;
import me.lauriichan.applicationbase.app.translation.ITranslationExtension;
import me.lauriichan.applicationbase.app.translation.provider.SimpleMessageProviderFactory;
import me.lauriichan.applicationbase.app.util.instance.SharedInstances;
import me.lauriichan.applicationbase.app.util.instance.SimpleInstanceInvoker;
import me.lauriichan.applicationbase.app.util.logger.LoggerState;
import me.lauriichan.applicationbase.app.util.logger.SysOutSimpleLogger;
import me.lauriichan.laylib.localization.MessageManager;
import me.lauriichan.laylib.localization.source.AnnotationMessageSource;
import me.lauriichan.laylib.localization.source.EnumMessageSource;
import me.lauriichan.laylib.localization.source.IMessageDefinition;
import me.lauriichan.laylib.logger.ISimpleLogger;

public abstract class BaseApp {

    private static final AtomicReference<BaseApp> APP = new AtomicReference<>();

    public static File getJarFile(Class<?> clazz) throws URISyntaxException {
        return new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI());
    }

    public static BaseApp get() {
        return APP.get();
    }

    public static record AppPhase(String name, boolean isApp) {}

    public static final AppPhase PHASE_PRELOAD_APP = new AppPhase("preload", true);

    public static final AppPhase PHASE_LOAD_CORE = new AppPhase("load", false);
    public static final AppPhase PHASE_LOAD_APP = new AppPhase("load", true);

    public static final AppPhase PHASE_READY_CORE = new AppPhase("ready", false);
    public static final AppPhase PHASE_READY_APP = new AppPhase("ready", true);

    public static final AppPhase PHASE_EXECUTE_CORE = new AppPhase("execute", false);
    public static final AppPhase PHASE_EXECUTE_APP = new AppPhase("execute", true);
    public static final AppPhase PHASE_EXECUTE_POST_CORE = new AppPhase("execute-post", false);

    public static final AppPhase PHASE_SHUTDOWN_CORE = new AppPhase("shutdown", false);
    public static final AppPhase PHASE_SHUTDOWN_APP = new AppPhase("shutdown", true);
    public static final AppPhase PHASE_SHUTDOWN_POST_CORE = new AppPhase("shutdown-post", false);

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private final ISimpleLogger logger;

    private final Path dataRoot;
    private final Path jarRoot;

    private final ResourceManager resourceManager;

    private final SimpleInstanceInvoker invoker;
    private final SharedInstances<IExtension> sharedExtensions;

    private final SignalManager signalManager;
    private final MessageManager messageManager;

    private final ConfigWrapper<StartupConfig> startupConfig;

    private volatile ConditionMapImpl conditionMap;

    private volatile IOManager ioManager;

    private volatile ConfigMigrator configMigrator;
    private volatile ConfigManager configManager;

    private volatile DataMigrator dataMigrator;
    private volatile DataManager dataManager;

    public BaseApp(File jarFile) {
        APP.set(this);
        // Create logger
        this.logger = createLogger();
        // Create jar and data roots
        this.jarRoot = createJarRoot(jarFile);
        this.dataRoot = createDataRoot();
        // Create substantial utility instances
        this.resourceManager = new ResourceManager(this);
        this.invoker = new SimpleInstanceInvoker();
        this.sharedExtensions = new SharedInstances<>(invoker);
        this.signalManager = new SignalManager(logger);
        this.messageManager = new MessageManager();
        // Setup invoker
        invoker.addExtra(this);
        invoker.addExtra(logger);
        invoker.addExtra(resourceManager);
        // Setup resource manager
        resourceManager.setDefault("jar");
        resourceManager.register("jar", (app, path) -> new PathDataSource(app.jarRoot().resolve(path)));
        resourceManager.register("data", (app, path) -> new PathDataSource(app.dataRoot().resolve(path)));
        resourceManager.register("fs", (app, path) -> new FileDataSource(new File(path)));
        // Load properties
        startupConfig = ConfigWrapper.single(this, new StartupConfig(this::onProperties));
        startupConfig.reload(true, false);
    }

    protected void start() {
        // Start application
        onLoad();
        onReady();
        onExecute();
    }

    protected ISimpleLogger createLogger() {
        return SysOutSimpleLogger.INSTANCE;
    }

    private final Path createJarRoot(File jarFile) {
        String filePath = jarFile.getAbsolutePath();
        URI uri = null;
        Path path = null;
        try {
            if (!filePath.endsWith("/")) {
                uri = new URI(("jar:file:/" + filePath.replace('\\', '/').replace(" ", "%20") + "!/").replace("//", "/"));
            } else {
                path = Paths.get(filePath.substring(1));
            }
        } catch (final URISyntaxException e) {
            throw new IllegalStateException("Failed to build resource uri", e);
        }
        if (uri != null) {
            try {
                FileSystems.getFileSystem(uri).close();
            } catch (final Exception exp) {
                if (!(exp instanceof NullPointerException || exp instanceof FileSystemNotFoundException)) {
                    logger.warning("Something went wrong while closing the file system", exp);
                }
            }
        }
        if (path == null) {
            try {
                path = FileSystems.newFileSystem(uri, Collections.emptyMap()).getPath("/");
            } catch (final IOException e) {
                throw new IllegalStateException("Unable to resolve jar root!", e);
            }
        }
        return path;
    }

    protected final Path createAppDataPath(String appDataName) {
        return Paths.get(System.getenv("APPDATA"), appDataName);
    }

    protected abstract Path createDataRoot();

    /*
     * Stages
     */

    /**
     * Setup properties
     */
    private final void onProperties(ObjectArrayList<Property> properties) {
        CompositeProperty composite = new CompositeProperty("system",
            "Properties for everything related to the system used by the application");
        properties.add(composite);
        onCoreProperties(composite);
        composite = new CompositeProperty("application", "Properties for everything related to the application");
        properties.add(composite);
        onAppProperties(composite);
    }

    /**
     * Setup application
     */
    private final void onLoad() {
        try {
            onAppPreload();
        } catch (final Throwable throwable) {
            onAppError(PHASE_PRELOAD_APP, throwable);
        }
        try {
            onCoreLoad();
        } catch (final Throwable throwable) {
            onAppError(PHASE_LOAD_CORE, throwable);
        }
        try {
            onAppLoad();
        } catch (final Throwable throwable) {
            onAppError(PHASE_LOAD_APP, throwable);
        }
    }

    /**
     * Get everything ready for execution
     */
    private final void onReady() {
        try {
            onCoreReady();
        } catch (final Throwable throwable) {
            onAppError(PHASE_READY_CORE, throwable);
        }
        try {
            onAppReady();
        } catch (final Throwable throwable) {
            onAppError(PHASE_READY_APP, throwable);
        }
    }

    /**
     * Execute application
     */
    private final void onExecute() {
        try {
            onCoreExecute();
        } catch (final Throwable throwable) {
            onAppError(PHASE_EXECUTE_CORE, throwable);
        }
        try {
            onAppExecute();
        } catch (final Throwable throwable) {
            onAppError(PHASE_EXECUTE_APP, throwable);
        }
        try {
            onCorePostExecute();
        } catch (final Throwable throwable) {
            onAppError(PHASE_EXECUTE_POST_CORE, throwable);
        }
    }

    /**
     * Shuts down application
     */
    public final void shutdown() {
        if (!shutdown.compareAndSet(false, true)) {
            return;
        }
        try {
            onCoreShutdown();
        } catch (final Throwable throwable) {
            onAppError(PHASE_SHUTDOWN_CORE, throwable);
        }
        try {
            onAppShutdown();
        } catch (final Throwable throwable) {
            onAppError(PHASE_SHUTDOWN_APP, throwable);
        }
        try {
            onCorePostShutdown();
        } catch (final Throwable throwable) {
            onAppError(PHASE_SHUTDOWN_POST_CORE, throwable);
        }
    }

    /*
     * Utilities
     */

    public final IDataSource resource(final String path) {
        return resourceManager.resolve(path);
    }

    public final <E extends IExtension> IExtensionPool<E> extension(final Class<E> type, final boolean instantiate) {
        return new ExtensionPoolImpl<>(this, type, instantiate);
    }

    public final <E extends IExtension> IExtensionPool<E> extension(final Class<? extends IExtension> extensionType, final Class<E> type,
        final boolean instantiate) {
        return new ExtensionPoolImpl<>(this, extensionType, type, instantiate);
    }

    /*
     * Core
     */

    protected void onCoreProperties(CompositeProperty properties) {
        properties.add(new ValueProperty<>("logger.state", "Sets the state of the logger (normal, debug, everything)",
            IPropertyIO.ofEnum(LoggerState.class), LoggerState.NORMAL, state -> {
                ISimpleLogger logger = logger();
                switch (state) {
                case NORMAL:
                default:
                    logger.setDebug(false);
                    logger.setTracking(false);
                    break;
                case DEBUG:
                    logger.setDebug(true);
                    logger.setTracking(false);
                    break;
                case EVERYTHING:
                    logger.setDebug(true);
                    logger.setTracking(true);
                    break;
                }
            }));
    }

    protected void onCoreLoad() throws Throwable {
        setupConditionMap();
        // Setup config
        configMigrator = new ConfigMigrator(this);
        configManager = new ConfigManager(this);
        // Setup data
        dataMigrator = new DataMigrator(this);
        dataManager = new DataManager(this);
    }

    protected void onCoreReady() throws Throwable {
        registerTranslations();
        // Load configurations
        configManager.reload();
        // Load data
        dataManager.reload();
        // Register signals
        registerSignals();
    }

    protected void onCoreExecute() throws Throwable {}

    protected void onCorePostExecute() throws Throwable {}

    protected void onCoreShutdown() throws Throwable {}

    protected void onCorePostShutdown() throws Throwable {}

    /*
     * Calls to Application
     */

    private final void setupConditionMap() {
        conditionMap = new ConditionMapImpl();
        onConditionMapSetup(conditionMap);
        conditionMap.lock();
    }

    @SuppressWarnings({
        "rawtypes",
        "unchecked"
    })
    private final void registerTranslations() {
        final IExtensionPool<ITranslationExtension> pool = extension(ITranslationExtension.class, false);
        final SimpleMessageProviderFactory factory = new SimpleMessageProviderFactory();
        pool.callClasses(extension -> {
            if (extension.isEnum()) {
                if (!IMessageDefinition.class.isAssignableFrom(extension)) {
                    return;
                }
                messageManager.register(new EnumMessageSource((Class) extension, factory));
                return;
            }
            messageManager.register(new AnnotationMessageSource(extension, factory));
        });
    }

    private final void registerSignals() {
        final IExtensionPool<ISignalHandlerExtension> pool = extension(ISignalHandlerExtension.class, true);
        pool.callInstances(extension -> {
            try {
                signalManager.register(extension);
            } catch (UnsupportedOperationException exp) {
                logger.error("Failed to register signal handler extension: " + extension.getClass().getName(), exp);
            }
        });
    }

    /*
     * Application
     */

    protected void onAppProperties(CompositeProperty properties) {}

    protected void onAppPreload() throws Throwable {}

    protected void onAppLoad() throws Throwable {}

    protected void onConditionMapSetup(final IConditionMap conditionMap) {}

    protected void onAppReady() throws Throwable {}

    protected void onAppExecute() throws Throwable {}

    protected void onAppShutdown() throws Throwable {}

    protected void onAppError(final AppPhase phase, final Throwable error) {
        logger.error(String.format("Failed to %s %s part:", phase.name(), phase.isApp() ? "plugin" : "core"), error);
    }

    /*
     * Getter
     */

    public final ISimpleLogger logger() {
        return logger;
    }

    public final Path jarRoot() {
        return jarRoot;
    }

    public final Path dataRoot() {
        return dataRoot;
    }

    public final ResourceManager resourceManager() {
        return resourceManager;
    }

    public final SimpleInstanceInvoker invoker() {
        return invoker;
    }

    public final SharedInstances<IExtension> sharedExtensions() {
        return sharedExtensions;
    }

    public final SignalManager signalManager() {
        return signalManager;
    }

    public final MessageManager messageManager() {
        return messageManager;
    }

    public final ConfigWrapper<StartupConfig> startupConfig() {
        return startupConfig;
    }

    public final IConditionMap conditionMap() {
        return conditionMap;
    }

    public final IOManager ioManager() {
        return ioManager;
    }

    public final ConfigMigrator configMigrator() {
        return configMigrator;
    }

    public final ConfigManager configManager() {
        return configManager;
    }

    public final DataMigrator dataMigrator() {
        return dataMigrator;
    }

    public final DataManager dataManager() {
        return dataManager;
    }

}
