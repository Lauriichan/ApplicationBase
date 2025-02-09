package me.lauriichan.applicationbase.app.data;

import me.lauriichan.applicationbase.app.extension.ExtensionPoint;
import me.lauriichan.applicationbase.app.extension.IExtension;

@ExtensionPoint
public interface IMultiDataExtension<K, E, T, D extends IFileDataExtension<T>> extends IExtension {
    
    K getDataKey(E element);

    String path(E element);
    
    D create(E element);
    
}
