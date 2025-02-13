package me.lauriichan.applicationbase.app.data;

import java.util.stream.Stream;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.lauriichan.applicationbase.app.BaseApp;
import me.lauriichan.applicationbase.app.ConditionConstant;

public final class DataManager {

    private final Object2ObjectArrayMap<Class<? extends ISingleDataExtension>, DataWrapper<?, ?>> data = new Object2ObjectArrayMap<>();
    private final Object2ObjectArrayMap<Class<? extends IMultiDataExtension>, MultiDataWrapper<?, ?, ?, ?, ?>> multiData = new Object2ObjectArrayMap<>();

    private final Object2ObjectArrayMap<Class<? extends IDirectoryDataExtension>, DirectoryDataWrapper<?, ?>> directoryData = new Object2ObjectArrayMap<>();

    public DataManager(final BaseApp app) {
        if (app.conditionMap().value(ConditionConstant.DISABLE_DATA)) {
            return;
        }
        app.extension(ISingleDataExtension.class, true).callInstances(extension -> {
            data.put(extension.getClass(), DataWrapper.single(app, extension));
        });
        app.extension(IMultiDataExtension.class, true).callInstances(extension -> {
            multiData.put(extension.getClass(), new MultiDataWrapper<>(app, extension));
        });
        app.extension(IDirectoryDataExtension.class, true).callInstances(extension -> {
            directoryData.put(extension.getClass(), DirectoryDataWrapper.create(app, extension));
        });
    }

    public int amount() {
        return data.size();
    }

    public Object2ObjectMap<IDataWrapper<?, ?>, int[]> reload() {
        return reload(false, false);
    }

    public Object2ObjectMap<IDataWrapper<?, ?>, int[]> reload(boolean force, boolean wipeAfterLoad) {
        ObjectList<IDataWrapper<?, ?>> wrappers = wrappers();
        Object2ObjectArrayMap<IDataWrapper<?, ?>, int[]> results = new Object2ObjectArrayMap<>(wrappers.size());
        wrappers.forEach(wrapper -> results.put(wrapper, wrapper.reload(force, wipeAfterLoad)));
        return Object2ObjectMaps.unmodifiable(results);
    }

    public Object2ObjectMap<IDataWrapper<?, ?>, int[]> save() {
        return save(false);
    }

    public Object2ObjectMap<IDataWrapper<?, ?>, int[]> save(boolean force) {
        ObjectList<IDataWrapper<?, ?>> wrappers = wrappers();
        Object2ObjectArrayMap<IDataWrapper<?, ?>, int[]> results = new Object2ObjectArrayMap<>(wrappers.size());
        wrappers.forEach(wrapper -> results.put(wrapper, wrapper.save(force)));
        return Object2ObjectMaps.unmodifiable(results);
    }

    public ObjectList<IDataWrapper<?, ?>> wrappers() {
        return Stream
            .concat(Stream.concat(data.values().stream(), multiData.values().stream().flatMap(config -> config.wrappers().stream())),
                directoryData.values().stream())
            .collect(ObjectArrayList.toList());
    }

    public <T, D extends ISingleDataExtension<T>> DataWrapper<T, D> wrapper(final Class<D> type) {
        final DataWrapper<?, ?> extension = data.get(type);
        if (extension == null) {
            return null;
        }
        return (DataWrapper<T, D>) extension;
    }

    public <D extends ISingleDataExtension<?>> D data(final Class<D> type) {
        final DataWrapper<?, ?> wrapper = data.get(type);
        if (wrapper == null) {
            return null;
        }
        return type.cast(wrapper.data());
    }

    public boolean has(final Class<? extends ISingleDataExtension<?>> type) {
        return data.containsKey(type);
    }

    public <T, D extends IDirectoryDataExtension<T>> DirectoryDataWrapper<T, D> directoryWrapper(final Class<D> type) {
        final DirectoryDataWrapper<?, ?> extension = directoryData.get(type);
        if (extension == null) {
            return null;
        }
        return (DirectoryDataWrapper<T, D>) extension;
    }

    public <D extends IDirectoryDataExtension<?>> D directoryData(final Class<D> type) {
        final DirectoryDataWrapper<?, ?> wrapper = directoryData.get(type);
        if (wrapper == null) {
            return null;
        }
        return type.cast(wrapper.data());
    }
    
    public boolean hasDirectory(final Class<? extends IDirectoryDataExtension<?>> type) {
        return directoryData.containsKey(type);
    }

    public ObjectCollection<MultiDataWrapper<?, ?, ?, ?, ?>> multiWrappers() {
        return multiData.values();
    }

    public <E, T, D extends IFileDataExtension<T>, M extends IMultiDataExtension<?, E, T, D>> MultiDataWrapper<?, E, T, D, M> multiWrapper(
        final Class<M> type) {
        MultiDataWrapper<?, ?, ?, ?, ?> multiWrapper = multiData.get(type);
        if (multiWrapper == null) {
            return null;
        }
        return (MultiDataWrapper<?, E, T, D, M>) multiWrapper;
    }

    public <E, T, D extends IFileDataExtension<T>, M extends IMultiDataExtension<?, E, T, D>> DataWrapper<T, D> multiWrapper(
        final Class<M> type, E element) {
        MultiDataWrapper<?, E, T, D, M> multiWrapper = multiWrapper(type);
        if (multiWrapper == null) {
            return null;
        }
        return multiWrapper.wrapper(element);
    }

    public <E, T, D extends IFileDataExtension<T>, M extends IMultiDataExtension<?, E, T, D>> DataWrapper<T, D> multiWrapperOrCreate(
        final Class<M> type, E element) {
        MultiDataWrapper<?, E, T, D, M> multiWrapper = multiWrapper(type);
        if (multiWrapper == null) {
            return null;
        }
        return multiWrapper.wrapperOrCreate(element);
    }

    public <E, T, D extends IFileDataExtension<T>, M extends IMultiDataExtension<?, E, T, D>> D multiData(final Class<M> type, E element) {
        DataWrapper<T, D> wrapper = multiWrapper(type, element);
        if (wrapper == null) {
            return null;
        }
        return wrapper.data();
    }

    public <E, T, D extends IFileDataExtension<T>, M extends IMultiDataExtension<?, E, T, D>> D multiDataOrCreate(final Class<M> type,
        E element) {
        return multiWrapperOrCreate(type, element).data();
    }

    public boolean hasMulti(final Class<? extends IMultiDataExtension<?, ?, ?, ?>> type) {
        return multiData.containsKey(type);
    }

}
