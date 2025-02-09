package me.lauriichan.applicationbase.app.resource;

import me.lauriichan.applicationbase.app.BaseApp;
import me.lauriichan.applicationbase.app.resource.source.IDataSource;

public interface ISourceProvider {

    /**
     * Provides a data source related to the path
     * 
     * @param  app  the resource owner
     * @param  path the path
     * 
     * @return      the data source
     */
    IDataSource provide(BaseApp app, String path);

}
