package me.lauriichan.applicationbase.app.translation.provider;

import me.lauriichan.laylib.localization.source.IProviderFactory;

public final class SimpleMessageProviderFactory implements IProviderFactory {

    @Override
    public SimpleMessageProvider build(final String id, final String fallback) {
        return new SimpleMessageProvider(id, fallback);
    }

}