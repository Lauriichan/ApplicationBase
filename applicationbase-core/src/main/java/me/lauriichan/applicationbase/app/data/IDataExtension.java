package me.lauriichan.applicationbase.app.data;

import me.lauriichan.applicationbase.app.extension.IExtension;

public interface IDataExtension<T> extends IExtension {

    default String name() {
        return getClass().getSimpleName();
    }

    IDataHandler<T> handler();

}
