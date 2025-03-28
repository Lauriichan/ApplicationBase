package me.lauriichan.applicationbase.app.ui.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import me.lauriichan.applicationbase.app.ui.component.Component;
import me.lauriichan.applicationbase.app.util.tick.AbstractTickTimer;

public final class ComponentTickTimer extends AbstractTickTimer {

    public static final double SECOND_RATIO = 1000000000;

    private final ObjectList<Component<?>> components = ObjectLists.synchronize(new ObjectArrayList<>());
    
    public boolean add(Component<?> component) { 
        if (components.contains(component)) {
            return false;
        }
        return components.add(component);
    }
    
    public boolean remove(Component<?> component) {
        return components.remove(component);
    }
    
    public void reset() {
        components.clear();
    }

    @Override
    protected void tick(long delta) {
        double deltaSecond = delta / SECOND_RATIO;
        for (Component<?> component : components) {
            component.update(deltaSecond);
        }
    }

}
