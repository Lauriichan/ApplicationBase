package me.lauriichan.applicationbase.app.config.startup;

import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import me.lauriichan.applicationbase.app.config.Configuration;
import me.lauriichan.applicationbase.app.config.IConfigHandler;
import me.lauriichan.applicationbase.app.config.ISingleConfigExtension;
import me.lauriichan.applicationbase.app.config.handler.JsonConfigHandler;
import me.lauriichan.laylib.logger.ISimpleLogger;

public final class StartupConfig implements ISingleConfigExtension {

    private final ObjectList<Property> properties;

    public StartupConfig(Consumer<ObjectArrayList<Property>> propergator) {
        ObjectArrayList<Property> properties = new ObjectArrayList<>();
        propergator.accept(properties);
        this.properties = ObjectLists.unmodifiable(properties);
    }

    @Override
    public String path() {
        return "data://properties.json";
    }

    @Override
    public IConfigHandler handler() {
        return JsonConfigHandler.JSON;
    }

    @Override
    public void onLoad(ISimpleLogger logger, Configuration configuration) throws Exception {
        for (Property property : properties) {
            property.load(configuration);
        }
    }

    @Override
    public void onSave(ISimpleLogger logger, Configuration configuration) throws Exception {
        for (Property property : properties) {
            property.save(configuration);
        }
    }

}
