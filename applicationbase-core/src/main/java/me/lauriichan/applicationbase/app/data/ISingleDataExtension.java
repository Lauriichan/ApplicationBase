package me.lauriichan.applicationbase.app.data;

import me.lauriichan.applicationbase.app.extension.ExtensionPoint;
import me.lauriichan.applicationbase.app.extension.IExtension;

@ExtensionPoint
public interface ISingleDataExtension<T> extends IFileDataExtension<T>, IExtension {
    
    String path();

}
