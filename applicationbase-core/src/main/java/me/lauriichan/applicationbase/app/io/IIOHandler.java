package me.lauriichan.applicationbase.app.io;

import me.lauriichan.applicationbase.app.extension.ExtensionPoint;
import me.lauriichan.applicationbase.app.extension.IExtension;

@ExtensionPoint
public interface IIOHandler<B, V> extends IExtension {
    
    Class<B> bufferType();
    
    Class<V> valueType();

}
