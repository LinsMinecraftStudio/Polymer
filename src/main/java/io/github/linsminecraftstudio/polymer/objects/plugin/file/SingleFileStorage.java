package io.github.linsminecraftstudio.polymer.objects.plugin.file;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SingleFileStorage {
    protected final Plugin plugin;

    public SingleFileStorage(Plugin plugin){
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
        return YamlConfiguration.loadConfiguration(file);
    }

    public void reload(){}
}
