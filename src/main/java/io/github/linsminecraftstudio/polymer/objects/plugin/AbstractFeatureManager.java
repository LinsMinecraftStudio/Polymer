package io.github.linsminecraftstudio.polymer.objects.plugin;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractFeatureManager {
    public Plugin plugin;
    public AbstractFeatureManager(Plugin plugin){
        this.plugin = plugin;
    }

    public YamlConfiguration handleConfig(String fileName){
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
        return YamlConfiguration.loadConfiguration(f);
    }

    public abstract void reload();
}
