package io.github.linsminecraftstudio.polymer.objects.plugin;

import com.google.common.base.Strings;
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

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

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

    public void set(String key, Object value){
        if (value instanceof Location){
            setLocation(key, (Location) value);
            return;
        }
        configuration.set(key, value);
        save();
    }

    private void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        configuration = YamlConfiguration.loadConfiguration(file);
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
        save();
    }

    @Nullable
    public Location getLocation(@NotNull String path) {
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
    public UUID getUUID(@NotNull String path) {
        String strUUID = configuration.getString(path);
        if (strUUID != null && !strUUID.isBlank()) {
            return UUID.fromString(strUUID);
        }else {
            return null;
        }
    }
}
