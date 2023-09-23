package io.github.linsminecraftstudio.polymer.objects.plugin.file;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultiFileStorage<T> extends SingleFileStorage{
    private final Map<String, YamlConfiguration> configurations = new HashMap<>();
    private final File folder;

    public MultiFileStorage(File folder, Plugin plugin) {
        super(plugin);
        this.folder = folder;
    }

    @Override
    protected YamlConfiguration handleConfig(String fileName) {
        return handleConfig(new File(folder, fileName));
    }

    @Override
    protected YamlConfiguration handleConfig(File file) {
        YamlConfiguration configuration = super.handleConfig(file);
        configurations.put(file.getName(), configuration);
        return configuration;
    }

    protected void write(String fileName, String key, T data) throws IOException {
        YamlConfiguration configuration = configurations.get(fileName);
        configuration.set(key, data);
        configuration.save(new File(folder, fileName));
    }

    protected <V> V get(String fileName, String key, @Nullable V def) {
        YamlConfiguration configuration = configurations.get(fileName);
        return configuration.getObject(key, (Class<V>) def.getClass(), def);
    }
}
