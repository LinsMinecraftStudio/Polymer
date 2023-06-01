package io.github.linsminecraftstudio.polymer.objects;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public abstract class AbstractFeatureManager {
    public Plugin plugin;
    public AbstractFeatureManager(Plugin plugin){
        this.plugin = plugin;
    }

    public YamlConfiguration handleConfig(String fileName){
        File f = new File(plugin.getDataFolder(), fileName);
        if (!f.exists()) {
            plugin.saveResource(fileName,false);
        }
        return YamlConfiguration.loadConfiguration(f);
    }

    public abstract void reload();
}
