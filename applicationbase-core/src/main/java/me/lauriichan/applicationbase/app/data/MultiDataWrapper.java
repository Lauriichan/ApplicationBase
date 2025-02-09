package me.lauriichan.applicationbase.app.data;

import java.util.Objects;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import me.lauriichan.applicationbase.app.BaseApp;

public final class MultiDataWrapper<K, E, T, D extends IFileDataExtension<T>, M extends IMultiDataExtension<K, E, T, D>> {

    private final Object2ObjectArrayMap<K, DataWrapper<T, D>> data = new Object2ObjectArrayMap<>();

    private final BaseApp plugin;
    private final M extension;

    public MultiDataWrapper(BaseApp plugin, M extension) {
        this.plugin = plugin;
        this.extension = extension;
    }

    public DataWrapper<T, D> wrapper(E element) {
        return data.get(extension.getDataKey(Objects.requireNonNull(element)));
    }
    
    public DataWrapper<T, D> wrapperOrCreate(E element) {
        K key = extension.getDataKey(Objects.requireNonNull(element));
        DataWrapper<T, D> wrapper = data.get(key);
        if (wrapper == null) {
            wrapper = new DataWrapper<>(plugin, extension.create(element), extension.path(element));
            wrapper.reload();
            data.put(key, wrapper);
        }
        return wrapper;
    }
    
    public D data(E element) {
        DataWrapper<T, D> wrapper = wrapper(element);
        if (wrapper == null) {
            return null;
        }
        return wrapper.data();
    }
    
    public D dataOrCreate(E element) {
        return wrapperOrCreate(element).data();
    }
    
    public ObjectCollection<DataWrapper<T, D>> wrappers() {
        return data.values();
    }
    
}
