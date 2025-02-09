package me.lauriichan.applicationbase.app.signal;

@FunctionalInterface
public interface ISignalFunction<S extends ISignal> {
    
    void onSignal(SignalContext<S> context) throws Throwable;
    
}
