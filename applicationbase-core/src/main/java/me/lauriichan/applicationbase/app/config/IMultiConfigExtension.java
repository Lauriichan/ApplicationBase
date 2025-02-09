package me.lauriichan.applicationbase.app.config;

import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.applicationbase.app.extension.ExtensionPoint;
import me.lauriichan.applicationbase.app.extension.IExtension;

@ExtensionPoint
public interface IMultiConfigExtension<K, T, C extends IConfigExtension> extends IExtension {
    
    Class<C> type();
    
    K getConfigKey(T element);

    String path(T element);
    
    C create();
    
    default void onLoad(ISimpleLogger logger) {}
    
    default void onSave(ISimpleLogger logger) {}
    
}
