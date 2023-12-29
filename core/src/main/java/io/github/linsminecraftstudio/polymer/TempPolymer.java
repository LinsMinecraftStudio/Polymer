package io.github.linsminecraftstudio.polymer;

import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import io.github.linsminecraftstudio.polymer.objectutils.LockableValue;
import org.apache.commons.lang3.Validate;

/**
 * Store the instance of polymer.
 */
public abstract class TempPolymer extends PolymerPlugin {
    private static final LockableValue<TempPolymer> instance = new LockableValue<>();

    public static TempPolymer getInstance() {
        return instance.getValue();
    }

    public static void setInstance(TempPolymer plugin) {
        Validate.notNull(plugin, "Plugin instance cannot be null");
        if (plugin.getPluginName().equals("Polymer")) {
            instance.set(plugin);
            instance.lock();
        }
    }

    public abstract boolean isDebug();
}
