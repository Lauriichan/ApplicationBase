package me.lauriichan.applicationbase.app.ui;

import me.lauriichan.applicationbase.app.ui.component.property.PropFloat;
import me.lauriichan.applicationbase.app.util.color.SimpleColor;

public final class DefaultConstants {

    private DefaultConstants() {
        throw new UnsupportedOperationException();
    }
    
    public static final PropFloat ITEM_SPACING = new PropFloat(4f);

    public static final SimpleColor WINDOW_BACKGROUND_COLOR = SimpleColor.sRGB("#0");
    public static final SimpleColor TEXT_COLOR = SimpleColor.sRGB("#f");

}
