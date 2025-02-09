package me.lauriichan.applicationbase.app.translation.config.advanced;

import me.lauriichan.applicationbase.app.BaseApp;
import me.lauriichan.applicationbase.app.ConditionConstant;
import me.lauriichan.applicationbase.app.config.Configuration;
import me.lauriichan.applicationbase.app.config.IConfigHandler;
import me.lauriichan.applicationbase.app.extension.Extension;
import me.lauriichan.applicationbase.app.extension.ExtensionCondition;
import me.lauriichan.applicationbase.app.translation.config.TranslationConfig;
import me.lauriichan.laylib.localization.MessageManager;
import me.lauriichan.laylib.localization.MessageProvider;
import me.lauriichan.laylib.logger.ISimpleLogger;

@Extension
@ExtensionCondition(name = ConditionConstant.USE_MULTILANG_CONFIG, condition = true)
public class AdvancedTranslationConfig extends TranslationConfig {

    private final MessageManager messageManager;

    public AdvancedTranslationConfig(final BaseApp app) {
        this.messageManager = app.messageManager();
    }

    @Override
    public String path() {
        return "data://translation/";
    }

    @Override
    public IConfigHandler handler() {
        return LanguageConfigHandler.LANGUAGE;
    }

    @Override
    public void onLoad(final ISimpleLogger logger, final Configuration configuration) throws Exception {
        if (!configuration.contains(DEFAULT_LANGUAGE)) {
            configuration.getConfiguration(DEFAULT_LANGUAGE, true);
        }
        final MessageProvider[] providers = messageManager.getProviders();
        for (final String key : configuration.keySet()) {
            loadMessages(configuration.getConfiguration(key), key, providers);
        }
    }

    @Override
    public void onSave(final ISimpleLogger logger, final Configuration configuration) throws Exception {
        if (!configuration.contains(DEFAULT_LANGUAGE)) {
            configuration.getConfiguration(DEFAULT_LANGUAGE, true);
        }
        final MessageProvider[] providers = messageManager.getProviders();
        for (final String key : configuration.keySet()) {
            saveMessages(configuration.getConfiguration(key), key, providers);
        }
    }

}
