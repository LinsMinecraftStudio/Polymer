package io.github.linsminecraftstudio.polymer.objects.plugin;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractFileStorage {
    private static Plugin plugin;
    private static File cfg;

    public AbstractFileStorage(Plugin plugin){
        AbstractFileStorage.plugin = plugin;
    }

    protected YamlConfiguration handleConfig(String fileName) {
        File f = new File(plugin.getDataFolder(), fileName);
        if (!f.exists()) {
            InputStream is = plugin.getResource(fileName);
            if (is != null) {
                plugin.saveResource(fileName, false);
            }else {
                try {f.createNewFile();
                } catch (IOException e) {throw new RuntimeException(e);}
            }
        }
        cfg = f;
        return YamlConfiguration.loadConfiguration(f);
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
        cfg = file;
        return YamlConfiguration.loadConfiguration(file);
    }

    public void reload(){

    }
}
