package me.lauriichan.applicationbase.app.util.http.type;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;

import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import me.lauriichan.applicationbase.app.util.http.UrlEncoder;

final class UrlEncodedContentType extends HttpContentType<Object2ObjectArrayMap<String, String>> {

    public static final UrlEncodedContentType URL_ENCODED = new UrlEncodedContentType();

    private UrlEncodedContentType() {
        super("application/x-www-form-urlencoded", "application/x-www-form-urlencoded", Object2ObjectArrayMap.class);
        if (URL_ENCODED != null) {
            throw new UnsupportedOperationException("Singleton");
        }
    }

    @Override
    public Object2ObjectArrayMap<String, String> read(FastByteArrayInputStream inputStream) throws IOException {
        String string = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        String[] params = string.split("&"), param;
        Object2ObjectArrayMap<String, String> map = new Object2ObjectArrayMap<>(params.length);
        UrlEncoder encoder = UrlEncoder.UTF_8;
        for (int i = 0; i < params.length; i++) {
            param = params[i].split("=", 3);
            if (param.length != 2) {
                continue;
            }
            map.put(encoder.decode(param[0]), encoder.decode(param[1]));
        }
        return map;
    }

    @Override
    public void write(FastByteArrayOutputStream outputStream, Object2ObjectArrayMap<String, String> value) throws IOException {
        UrlEncoder encoder = UrlEncoder.UTF_8;
        StringBuilder builder = new StringBuilder();
        for (Object2ObjectArrayMap.Entry<String, String> entry : value.object2ObjectEntrySet()) {
            if (entry.getValue() == null || entry.getValue().isBlank()) {
                continue;
            }
            builder.append(encoder.encode(entry.getKey())).append('=').append(encoder.encode(entry.getValue())).append('&');
        }
        try (WritableByteChannel channel = Channels.newChannel(outputStream)) {
            channel.write(StandardCharsets.UTF_8.encode(builder.toString()));
        }
    }

}
