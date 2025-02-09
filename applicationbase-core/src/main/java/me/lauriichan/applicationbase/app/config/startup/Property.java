package me.lauriichan.applicationbase.app.config.startup;

import me.lauriichan.applicationbase.app.config.Configuration;

public abstract class Property {
    
    protected final String path;
    protected final String description;
    
    public Property(final String path, final String description) {
        this.path = path;
        this.description = description;
    }
    
    public final String path() {
        return path;
    }
    
    public final String description() {
        return description;
    }
    
    public abstract void load(Configuration configuration);
    
    public abstract void save(Configuration configuration);

}
