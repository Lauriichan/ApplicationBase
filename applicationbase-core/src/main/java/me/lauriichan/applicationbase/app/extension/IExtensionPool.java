package me.lauriichan.applicationbase.app.extension;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public interface IExtensionPool<T extends IExtension> {

    Class<T> type();

    List<T> extensions();

    boolean instantiated();
    
    int count();

    List<Class<? extends T>> extensionClasses();

    default void callInstances(final Consumer<T> call) {
        Objects.requireNonNull(call);
        for (final T extension : extensions()) {
            call.accept(extension);
        }
    }

    default void callClasses(final Consumer<Class<? extends T>> call) {
        Objects.requireNonNull(call);
        for (final Class<? extends T> extension : extensionClasses()) {
            call.accept(extension);
        }
    }

}
