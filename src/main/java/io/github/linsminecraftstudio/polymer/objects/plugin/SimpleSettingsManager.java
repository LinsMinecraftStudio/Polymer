package io.github.linsminecraftstudio.polymer.objects.plugin;

import io.github.linsminecraftstudio.polymer.utils.ObjectConverter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

public class SimpleSettingsManager {
    private final FileConfiguration configuration;

    public SimpleSettingsManager(@NotNull FileConfiguration configuration) {
        this.configuration = configuration;
    }

    public int getInt(String key){
        return configuration.getInt(key, -100);
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
        return configuration.getLong(key);
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

    public void set(String key, Object value){
        if (value instanceof Location){
            setLocation(key, (Location) value);
            return;
        }
        configuration.set(key, value);
    }
    @ParametersAreNonnullByDefault
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
    }

    @Nullable
    public Location getLocation(String path) {
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
            return null;
        }
        return null;
    }

    @Nullable
    public UUID getUUID(String path) {
        String strUUID = configuration.getString(path);
        if (strUUID != null && !strUUID.isBlank()) {
            return UUID.fromString(strUUID);
        }else {
            return null;
        }
    }
}
