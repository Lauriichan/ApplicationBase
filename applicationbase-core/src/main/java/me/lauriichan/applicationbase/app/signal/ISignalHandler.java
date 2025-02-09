package me.lauriichan.applicationbase.app.signal;

public interface ISignalHandler {
    
    default SignalContainer newContainer() {
        throw new UnsupportedOperationException();
    }

}
