package me.lauriichan.applicationbase.app.ui.component.layout;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class LayoutElement {
    
    private final LayoutElement parent;
    private final ObjectArrayList<LayoutElement> children = new ObjectArrayList<>();
    
    private volatile float x, y;
    private volatile float width, height;
    
    private volatile float paddingLeft, paddingTop, paddingRight, paddingBottom;
    private volatile float childGap;
    
    public LayoutElement(LayoutElement parent) {
        this.parent = parent;
    }
    
    public void open() {
        
    }
    
    public void close() {
        if (parent == null) {
            return;
        }
        parent.width += width;
        parent.height += height;
    }
    
    

}
