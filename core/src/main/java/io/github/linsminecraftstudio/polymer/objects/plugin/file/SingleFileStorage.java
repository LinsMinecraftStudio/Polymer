package io.github.linsminecraftstudio.polymer.objects.plugin.file;

import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import lombok.Getter;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import java.util.Set;

@Getter
public class SingleFileStorage extends YamlConfiguration {
    private final PolymerPlugin plugin;
    private final File file;
    private YamlConfiguration configuration;

    public SingleFileStorage(PolymerPlugin plugin, File file){
        super();
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

    @Override
    public @NotNull String saveToString() {
        return configuration.saveToString();
    }

    @Override
    public void loadFromString(@NotNull String contents) throws InvalidConfigurationException {
        configuration.loadFromString(contents);
    }

    @Override
    public @NotNull YamlConfigurationOptions options() {
        return configuration.options();
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        configuration.set(path, value);
    }

    @Override
    public void save(@NotNull File file) throws IOException {
        configuration.save(file);
    }

    @Override
    public void save(@NotNull String file) throws IOException {
        configuration.save(file);
    }

    @Override
    public void load(@NotNull File file) throws IOException, InvalidConfigurationException {
        configuration.load(file);
    }

    @Override
    public void load(@NotNull Reader reader) throws IOException, InvalidConfigurationException {
        configuration.load(reader);
    }

    @Override
    public void addDefault(@NotNull String path, @Nullable Object value) {
        configuration.addDefault(path, value);
    }

    @Override
    public void addDefaults(@NotNull Map<String, Object> defaults) {
        configuration.addDefaults(defaults);
    }

    @Override
    public void addDefaults(@NotNull Configuration defaults) {
        configuration.addDefaults(defaults);
    }

    @Override
    @Nullable
    public Configuration getDefaults() {
        return configuration.getDefaults();
    }

    @Override
    public void setDefaults(@NotNull Configuration defaults) {
        configuration.setDefaults(defaults);
    }

    @Override
    public void load(@NotNull String file) throws IOException, InvalidConfigurationException {
        configuration.load(file);
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return configuration.getKeys(deep);
    }

    @Override
    public Map<String, Object> getValues(boolean deep) {
        return configuration.getValues(deep);
    }
}
