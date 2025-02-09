package me.lauriichan.applicationbase.app.translation.config;

import me.lauriichan.applicationbase.app.config.Configuration;
import me.lauriichan.applicationbase.app.config.ISingleConfigExtension;
import me.lauriichan.applicationbase.app.translation.provider.SimpleMessage;
import me.lauriichan.applicationbase.app.translation.provider.SimpleMessageProvider;
import me.lauriichan.laylib.localization.IMessage;
import me.lauriichan.laylib.localization.MessageProvider;

public abstract class TranslationConfig implements ISingleConfigExtension {
    
    public static final String DEFAULT_LANGUAGE = "en-uk";

    protected void loadMessages(final Configuration configuration, final String language, final MessageProvider[] providers) {
        for (final MessageProvider provider : providers) {
            if (!(provider.getMessage(language) instanceof final SimpleMessage message)) {
                continue;
            }
            message.translation(configuration.get(provider.getId(), String.class));
        }
    }

    protected void saveMessages(final Configuration configuration, final String language, final MessageProvider[] providers) {
        IMessage message;
        for (final MessageProvider provider : providers) {
            message = provider.getMessage(language);
            if (message == null) {
                if (provider instanceof final SimpleMessageProvider simpleProvider) {
                    configuration.set(provider.getId(), simpleProvider.getFallback());
                }
                continue;
            }
            configuration.set(message.id(), message.value());
        }
    }

}
