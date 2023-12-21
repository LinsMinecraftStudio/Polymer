package io.github.linsminecraftstudio.polymer.objects.plugin.file;

import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class SingleFileStorage {
    private final PolymerPlugin plugin;
    private final File file;
    private YamlConfiguration configuration;

    public SingleFileStorage(PolymerPlugin plugin, File file){
        this.plugin = plugin;
        this.file = file;
        this.configuration = handleConfig(file);
    }

    private YamlConfiguration handleConfig(File file) {
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
        return YamlConfiguration.loadConfiguration(file);
    }

    protected void reload(YamlConfiguration refresh) {
        configuration = refresh;
        plugin.getScheduler().scheduleAsync(() -> {
            try {
                configuration.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
