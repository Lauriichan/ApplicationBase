package me.lauriichan.applicationbase.app.config;

import java.util.Objects;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import me.lauriichan.applicationbase.app.BaseApp;

public final class MultiConfigWrapper<K, T, C extends IConfigExtension, E extends IMultiConfigExtension<K, T, C>>
    implements IConfigWrapper<C> {

    private final Object2ObjectArrayMap<K, ConfigWrapper<C>> configs = new Object2ObjectArrayMap<>();

    private final BaseApp app;
    private final E extension;

    public MultiConfigWrapper(BaseApp app, E extension) {
        this.app = app;
        this.extension = extension;
    }

    public E extension() {
        return extension;
    }

    public ConfigWrapper<C> wrapper(T element) {
        return configs.get(extension.getConfigKey(Objects.requireNonNull(element)));
    }

    public ConfigWrapper<C> wrapperOrCreate(T element) {
        K key = extension.getConfigKey(Objects.requireNonNull(element));
        ConfigWrapper<C> wrapper = configs.get(key);
        if (wrapper == null) {
            wrapper = new ConfigWrapper<>(app, extension.create(), extension.path(element));
            wrapper.reload(false, false);
            configs.put(key, wrapper);
        }
        return wrapper;
    }

    public C config(T element) {
        ConfigWrapper<C> wrapper = wrapper(element);
        if (wrapper == null) {
            return null;
        }
        return wrapper.config();
    }

    public C configOrCreate(T element) {
        return wrapperOrCreate(element).config();
    }

    public ObjectCollection<ConfigWrapper<C>> wrappers() {
        return configs.values();
    }

    @Override
    public Class<C> configType() {
        return extension.type();
    }

    @Override
    public int[] reload(final boolean forceReload, final boolean wipeAfterLoad) {
        try {
            extension.onLoad(app.logger());
        } catch (RuntimeException exp) {
            app.logger().warning("Something went wrong while loading multi config '{0}'", exp, getClass().getName());
        }
        int index = 0;
        int[] states = new int[configs.size()];
        for (ConfigWrapper<C> wrapper : configs.values()) {
            states[index++] = wrapper.reloadSingle(forceReload, wipeAfterLoad);
        }
        return states;
    }

    @Override
    public int[] save(final boolean forceSave) {
        try {
            extension.onSave(app.logger());
        } catch (RuntimeException exp) {
            app.logger().warning("Something went wrong while saving multi config '{0}'", exp, getClass().getName());
        }
        int index = 0;
        int[] states = new int[configs.size()];
        for (ConfigWrapper<C> wrapper : configs.values()) {
            states[index++] = wrapper.saveSingle(forceSave);
        }
        return states;
    }

}
