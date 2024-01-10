package io.github.linsminecraftstudio.polymer.objects.other;

import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import io.github.linsminecraftstudio.polymer.utils.IterableUtil;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

import java.util.Optional;

/**
 * For storing and get metadata.
 */
public class MetadataStore {
    private final Metadatable metadatable;
    private final PolymerPlugin plugin;

    public MetadataStore(Metadatable metadatable) {
        this.metadatable = metadatable;
        this.plugin = OtherUtils.findCallingPlugin();
    }

    public void setMetadata(String key, Object value) {
        metadatable.setMetadata(key, new FixedMetadataValue(plugin, value));
    }

    public void removeMetadata(String key) {
        metadatable.removeMetadata(key, plugin);
    }

    public Optional<MetadataValue> getMetadata(String key) {
        return IterableUtil.getIf(metadatable.getMetadata(key), value -> value.getOwningPlugin() == plugin);
    }
}
