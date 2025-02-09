package me.lauriichan.applicationbase.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import me.lauriichan.applicationbase.app.extension.ExtensionCondition;
import me.lauriichan.applicationbase.app.extension.ExtensionPoint;
import me.lauriichan.applicationbase.app.extension.IConditionMap;
import me.lauriichan.applicationbase.app.extension.IExtension;
import me.lauriichan.applicationbase.app.extension.IExtensionPool;
import me.lauriichan.applicationbase.app.extension.processor.ExtensionProcessor;
import me.lauriichan.applicationbase.app.resource.source.IDataSource;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.laylib.reflection.ClassUtil;

final class ExtensionPoolImpl<T extends IExtension> implements IExtensionPool<T> {

    private static final String ORIGINAL_PACKAGE = "me.lauriichan!applicationbase.app".replace('!', '.');
    private static final String SHADED_PACKAGE = ExtensionPoolImpl.class.getPackageName();

    private static final boolean IS_SHADED = !SHADED_PACKAGE.equals(ORIGINAL_PACKAGE);

    public static String resolveToClassPath(final String path) {
        if (!IS_SHADED || !path.startsWith(ORIGINAL_PACKAGE)) {
            return path;
        }
        return SHADED_PACKAGE + path.substring(ORIGINAL_PACKAGE.length());
    }

    public static String resolveFromClassPath(final String path) {
        if (!IS_SHADED || !path.startsWith(SHADED_PACKAGE)) {
            return path;
        }
        return ORIGINAL_PACKAGE + path.substring(SHADED_PACKAGE.length());
    }

    public static boolean isExtendable(final Class<?> type) {
        return ClassUtil.getAnnotation(type, ExtensionPoint.class) != null;
    }

    static final class ConditionMapImpl implements IConditionMap {

        private final Object2BooleanOpenHashMap<String> map = new Object2BooleanOpenHashMap<>();
        private volatile boolean locked = false;

        public ConditionMapImpl() {
            map.defaultReturnValue(false);
        }

        @Override
        public boolean value(final String property) {
            return map.getBoolean(property);
        }

        @Override
        public void value(final String property, final boolean value) {
            if (locked) {
                return;
            }
            map.put(property, value);
        }

        @Override
        public boolean set(final String property) {
            return map.containsKey(property);
        }

        @Override
        public void unset(final String property) {
            if (locked) {
                return;
            }
            map.removeBoolean(property);
        }

        @Override
        public boolean locked() {
            return locked;
        }

        void lock() {
            locked = true;
        }

    }

    private final Class<T> type;
    private final boolean instantiated;
    private final List<T> extensions;
    private final List<Class<? extends T>> extensionClasses;

    ExtensionPoolImpl(final BaseApp app, final Class<T> type, final boolean instantiate) {
        this(app, type, type, instantiate);
    }

    ExtensionPoolImpl(final BaseApp app, final Class<? extends IExtension> extensionType, final Class<T> type,
        final boolean instantiate) {
        Objects.requireNonNull(app, "App can not be null!");
        this.instantiated = instantiate;
        this.type = Objects.requireNonNull(type, "Extension type can not be null!");
        final String typeName = resolveFromClassPath(extensionType.getName());
        if (!isExtendable(extensionType)) {
            throw new IllegalArgumentException("The class '" + typeName + "' is not extendable!");
        }
        if (!extensionType.isAssignableFrom(type)) {
            throw new IllegalArgumentException("The class '" + resolveFromClassPath(type.getName()) + "' can not be casted to '" + typeName + "'");
        }
        final ISimpleLogger logger = app.logger();
        logger.debug("Processing extension '{0}'", typeName);
        final IDataSource source = app.resource("jar://" + ExtensionProcessor.extensionPath(typeName));
        if (!source.exists() || !source.isReadable()) {
            this.extensions = Collections.emptyList();
            this.extensionClasses = Collections.emptyList();
        } else {
            List<T> extensions = null;
            List<Class<? extends T>> extensionClasses = null;
            try (BufferedReader reader = source.openReader()) {
                if (instantiate) {
                    extensions = new ArrayList<>();
                }
                extensionClasses = new ArrayList<>();
                String line;
                readLoop:
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        break;
                    }
                    final Class<?> clazz = ClassUtil.findClass(resolveToClassPath(line));
                    if (clazz == null) {
                        logger.debug("Couldn't find classs '{0}'", line);
                        continue;
                    }
                    if (!type.isAssignableFrom(clazz)) {
                        logger.debug("Class '{0}' is not assignable from '{1}'", clazz.getName(), typeName);
                        continue;
                    }
                    final Class<? extends T> extensionClazz = clazz.asSubclass(type);
                    if (app.conditionMap() != null) {
                        final IConditionMap map = app.conditionMap();
                        final ExtensionCondition[] conditions = ClassUtil.getAnnotations(extensionClazz, ExtensionCondition.class);
                        for (final ExtensionCondition condition : conditions) {
                            if (map.set(condition.name()) && map.value(condition.name()) != condition.condition()
                                || !map.set(condition.name()) && !condition.activeByDefault()) {
                                logger.debug(
                                    "Extension implementation '{0}' for extension '{1}' is disabled because condition '{2}' is not set to '{3}'",
                                    extensionClazz.getName(), typeName, condition.name(), condition.condition());
                                continue readLoop;
                            }
                        }
                    }
                    if (extensions == null) {
                        logger.debug("Found extension '{0}'", extensionClazz.getName());
                        extensionClasses.add(extensionClazz);
                        continue;
                    }
                    T extension = null;
                    try {
                        extension = app.sharedExtensions().get(extensionClazz);
                    } catch (Throwable exp) {
                        logger.debug("Failed to load instance '{0}' for extension '{1}'", exp, extensionClazz.getName(), typeName);
                        continue;
                    }
                    if (extension == null) {
                        logger.debug("Failed to load instance '{0}' for extension '{1}'", extensionClazz.getName(), typeName);
                        continue;
                    }
                    logger.debug("Found extension '{0}'", extensionClazz.getName());
                    extensions.add(extension);
                    extensionClasses.add(extensionClazz);
                }
            } catch (final IOException exp) {
                logger.debug("Couldn't load instances for extension '{0}'", typeName);
            }
            this.extensions = extensions == null ? Collections.emptyList() : Collections.unmodifiableList(extensions);
            this.extensionClasses = extensionClasses == null ? Collections.emptyList() : Collections.unmodifiableList(extensionClasses);
        }
        logger.debug("Found {1} extension(s) for '{0}'", typeName, this.extensionClasses.size());
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public List<T> extensions() {
        return extensions;
    }
    
    @Override
    public int count() {
        return instantiated ? extensions.size() : extensionClasses.size();
    }

    @Override
    public boolean instantiated() {
        return instantiated;
    }

    @Override
    public List<Class<? extends T>> extensionClasses() {
        return extensionClasses;
    }

}
