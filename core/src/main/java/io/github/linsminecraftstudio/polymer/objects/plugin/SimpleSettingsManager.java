package io.github.linsminecraftstudio.polymer.objects.plugin;

import io.github.linsminecraftstudio.polymer.objects.PolymerConstants;
import io.github.linsminecraftstudio.polymer.utils.ObjectConverter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SimpleSettingsManager {
    private YamlConfiguration configuration;
    private final File file;

    public SimpleSettingsManager(@NotNull File file) {
        configuration = YamlConfiguration.loadConfiguration(file);
        this.file = file;
    }

    public SimpleSettingsManager(@NotNull Plugin plugin) {
        configuration = (YamlConfiguration) plugin.getConfig();
        file = new File(plugin.getDataFolder(), "config.yml");
    }

    public int getInt(String key){
        return configuration.getInt(key, PolymerConstants.ERROR_CODE);
    }

    public String getString(String key){
        return configuration.getString(key,"");
    }

    public Component getComponent(String key, boolean colorize){
        return colorize ? ObjectConverter.toComponent(getString(key)) : Component.text(getString(key));
    }

    public boolean getBoolean(String key){
        return configuration.getBoolean(key);
    }

    public long getLong(String key) {
        return configuration.getLong(key, PolymerConstants.ERROR_CODE);
    }

    public Material getMaterial(String key) {
        return Material.getMaterial(configuration.getString(key,"STONE"), true);
    }

    public List<Integer> getIntList(String key) {
        return configuration.getIntegerList(key);
    }

    public ConfigurationSection getSection(String key) {
        return configuration.getConfigurationSection(key);
    }

    public char getChar(String key) {
        return configuration.getObject(key, char.class,' ');
    }

    public List<String> getStrList(String key) {
        return configuration.getStringList(key);
    }

    public void set(String key,@Nullable Object value){
        if (value instanceof Location loc){
            setLocation(key, loc);
            return;
        }
        if (value instanceof UUID u) {
            configuration.set(key, u.toString());
            return;
        }
        configuration.set(key, value);
        save();
    }

    private void save() {
        try {
            CompletableFuture.runAsync(() -> {
                try {
                    configuration.save(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        configuration = YamlConfiguration.loadConfiguration(file);
    }

    private void setLocation(String path, Location loc){
        ConfigurationSection cs = configuration.getConfigurationSection(path);
        if (cs == null){
            cs = configuration.createSection(path);
        }
        cs.set("world", loc.getWorld());
        cs.set("x", loc.getX());
        cs.set("y", loc.getY());
        cs.set("z", loc.getZ());
        cs.set("pitch", loc.getPitch());
        cs.set("yaw", loc.getYaw());
        save();
    }

    @Nullable
    public Location getLocation(@NotNull String path) {
        return getLocation(path, null);
    }

    @Nullable
    public Location getLocation(@NotNull String path, @Nullable Location def) {
        ConfigurationSection cs = configuration.getConfigurationSection(path);
        if (cs != null){
            double x = cs.getDouble("x");
            double y = cs.getDouble("y");
            double z = cs.getDouble("z");
            float p = Float.parseFloat(cs.getString("pitch", "0.000"));
            float y2 = Float.parseFloat(cs.getString("yaw", "0.000"));
            World w = Bukkit.getWorld(cs.getString("world",""));
            if (w != null) {
                return new Location(w, x, y, z, p, y2);
            }
            return def;
        }
        return def;
    }

    @Nullable
    public UUID getUUID(@NotNull String path) {
        return getUUID(path, null);
    }

    @Nullable
    public UUID getUUID(@NotNull String path, @Nullable UUID def) {
        String strUUID = configuration.getString(path);
        if (strUUID != null && !strUUID.isBlank()) {
            return UUID.fromString(strUUID);
        }else {
            return def;
        }
    }
}
