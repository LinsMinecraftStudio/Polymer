package io.github.linsminecraftstudio.polymer.objects.plugin.file;

import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class SingleFileStorage extends YamlConfiguration {
    private final PolymerPlugin plugin;
    private final File file;

    public SingleFileStorage(PolymerPlugin plugin, File file){
        this.plugin = plugin;
        this.file = file;

        handleConfig(file);
        try {
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleConfig(File file) {
        if (!file.exists()) {
            InputStream is = plugin.getResource(file.getName());
            if (is != null) {
                plugin.saveResource(file.getName(), false);
            } else {
                try {file.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    protected void reload() {
        plugin.getScheduler().scheduleAsync(() -> {
            try {
                save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
