package me.lauriichan.applicationbase.app.translation.config.basic;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import me.lauriichan.applicationbase.app.config.Configuration;
import me.lauriichan.applicationbase.app.config.IConfigHandler;
import me.lauriichan.applicationbase.app.config.handler.JsonConfigHandler;
import me.lauriichan.applicationbase.app.resource.source.IDataSource;
import me.lauriichan.laylib.json.IJson;
import me.lauriichan.laylib.json.JsonArray;
import me.lauriichan.laylib.json.JsonObject;
import me.lauriichan.laylib.json.io.JsonParser;
import me.lauriichan.laylib.json.io.JsonWriter;

public final class TranslationConfigHandler implements IConfigHandler {

    public static final TranslationConfigHandler TRANSLATION = new TranslationConfigHandler();

    private final JsonWriter jsonWriter = JsonConfigHandler.WRITER;

    private TranslationConfigHandler() {}

    @Override
    public void load(final Configuration configuration, final IDataSource source, final boolean onlyRaw) throws Exception {
        IJson<?> element;
        try (BufferedReader reader = source.openReader()) {
            element = JsonParser.fromReader(reader);
        }
        if (!element.isObject()) {
            throw new IllegalStateException("Config source doesn't contain a JsonObject");
        }
        loadToConfig(element.asJsonObject(), configuration);
    }

    private void loadToConfig(final JsonObject object, final Configuration configuration) {
        for (final String key : object.keySet()) {
            final IJson<?> element = object.get(key);
            if (element.isNull()) {
                continue;
            }
            if (element.isObject()) {
                loadToConfig(element.asJsonObject(), configuration.getConfiguration(key, true));
                continue;
            }
            if (element.isArray()) {
                final StringBuilder builder = new StringBuilder();
                boolean first = true;
                for (final IJson<?> arrayElement : element.asJsonArray()) {
                    if (arrayElement.isNull() || !arrayElement.type().isPrimitive()) {
                        continue;
                    }
                    if (first) {
                        first = false;
                    } else {
                        builder.append("\n");
                    }
                    builder.append(arrayElement.value().toString());
                }
                configuration.set(key, builder.toString());
                continue;
            }
            configuration.set(key, element.value().toString());
        }
    }

    @Override
    public void save(final Configuration configuration, final IDataSource source) throws Exception {
        final JsonObject root = new JsonObject();
        saveToObject(root, configuration);
        try (BufferedWriter writer = source.openWriter()) {
            jsonWriter.toWriter(root, writer);
        }
    }

    private void saveToObject(final JsonObject object, final Configuration configuration) {
        String value;
        String[] lines;
        for (final String key : configuration.keySet()) {
            if (configuration.isConfiguration(key)) {
                final JsonObject child = new JsonObject();
                saveToObject(child, configuration.getConfiguration(key));
                object.put(key, child);
                continue;
            }
            value = configuration.get(key, String.class);
            if (value == null) {
                continue;
            }
            if (value.contains("\n")) {
                lines = value.split("\n");
                final JsonArray array = new JsonArray();
                for (final String line : lines) {
                    array.addAny(line);
                }
                object.put(key, array);
                continue;
            }
            object.put(key, value);
        }
    }

}
