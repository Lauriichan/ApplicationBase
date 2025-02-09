package me.lauriichan.applicationbase.app.data;

import me.lauriichan.applicationbase.app.data.IDataHandler.Wrapper;
import me.lauriichan.laylib.logger.ISimpleLogger;

public interface IFileDataExtension<T> extends IDataExtension<T> {

    default boolean isModified() {
        // Consider data modified by default
        return true;
    }

    default void onPropergate(final ISimpleLogger logger, final Wrapper<T> value) {}

    default void onLoad(final ISimpleLogger logger, final Wrapper<T> value) throws Exception {}

    default void onSave(final ISimpleLogger logger, final Wrapper<T> value) throws Exception {}

}
