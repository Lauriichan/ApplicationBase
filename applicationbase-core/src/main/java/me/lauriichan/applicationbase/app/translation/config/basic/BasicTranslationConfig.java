package me.lauriichan.applicationbase.app.translation.config.basic;

import me.lauriichan.applicationbase.app.BaseApp;
import me.lauriichan.applicationbase.app.ConditionConstant;
import me.lauriichan.applicationbase.app.config.Configuration;
import me.lauriichan.applicationbase.app.config.IConfigHandler;
import me.lauriichan.applicationbase.app.extension.Extension;
import me.lauriichan.applicationbase.app.extension.ExtensionCondition;
import me.lauriichan.applicationbase.app.translation.config.TranslationConfig;
import me.lauriichan.laylib.localization.MessageManager;
import me.lauriichan.laylib.logger.ISimpleLogger;

@Extension
@ExtensionCondition(name = ConditionConstant.USE_MULTILANG_CONFIG, condition = false, activeByDefault = true)
public class BasicTranslationConfig extends TranslationConfig {

    private final MessageManager messageManager;

    public BasicTranslationConfig(final BaseApp app) {
        this.messageManager = app.messageManager();
    }

    @Override
    public String path() {
        return "data://translation.json";
    }

    @Override
    public IConfigHandler handler() {
        return TranslationConfigHandler.TRANSLATION;
    }

    @Override
    public void onLoad(final ISimpleLogger logger, final Configuration configuration) throws Exception {
        loadMessages(configuration, DEFAULT_LANGUAGE, messageManager.getProviders());
    }

    @Override
    public void onSave(final ISimpleLogger logger, final Configuration configuration) throws Exception {
        saveMessages(configuration, DEFAULT_LANGUAGE, messageManager.getProviders());
    }

}
