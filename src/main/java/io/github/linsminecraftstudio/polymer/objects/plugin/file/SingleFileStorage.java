package io.github.linsminecraftstudio.polymer.objects.plugin.file;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class SingleFileStorage {
    private final Plugin plugin;
    private final File file;
    private YamlConfiguration configuration;

    public SingleFileStorage(Plugin plugin, File file){
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

    protected void reload() {
        try {
            configuration.save(file);
            configuration = handleConfig(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
