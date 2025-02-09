package me.lauriichan.applicationbase.app.config;

import me.lauriichan.applicationbase.app.extension.ExtensionPoint;
import me.lauriichan.applicationbase.app.extension.IExtension;

@ExtensionPoint
public interface ISingleConfigExtension extends IConfigExtension, IExtension {

    String path();

}
