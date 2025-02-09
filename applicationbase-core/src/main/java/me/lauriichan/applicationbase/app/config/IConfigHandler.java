package me.lauriichan.applicationbase.app.config;

import me.lauriichan.applicationbase.app.resource.source.IDataSource;

public interface IConfigHandler {

    void load(Configuration configuration, IDataSource source, boolean onlyRaw) throws Exception;

    void save(Configuration configuration, IDataSource source) throws Exception;

}
