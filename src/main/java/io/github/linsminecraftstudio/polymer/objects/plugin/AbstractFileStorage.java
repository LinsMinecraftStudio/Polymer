package io.github.linsminecraftstudio.polymer.objects.plugin;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractFileStorage {
    private final Plugin plugin;

    public AbstractFileStorage(Plugin plugin){
        this.plugin = plugin;
    }

    protected YamlConfiguration handleConfig(String fileName) {
        File f = new File(plugin.getDataFolder(), fileName);
        return handleConfig(f);
    }

    protected YamlConfiguration handleConfig(File file) {
        if (!file.exists()) {
            InputStream is = plugin.getResource(file.getName());
            if (is != null) {
                plugin.saveResource(file.getName(), false);
            } else {
                try {file.createNewFile();
                } catch (IOException e) {throw new RuntimeException(e);}
            }
        }
        YamlConfiguration cfg = new YamlConfiguration();
        try {
            cfg.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        return cfg;
    }

    public void reload(){

    }
}
