package io.github.linsminecraftstudio.polymer.objects.plugin;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.objects.PolymerConstants;
import io.github.linsminecraftstudio.polymer.objects.plugin.message.PolymerMessageHandler;
import io.github.linsminecraftstudio.polymer.schedule.BFScheduler;
import io.github.linsminecraftstudio.polymer.utils.FileUtil;
import io.github.linsminecraftstudio.polymer.utils.Metrics;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

/**
 * Created for tag polymer plugin and make useful methods
 */
public abstract class PolymerPlugin extends JavaPlugin {
    private Metrics metrics;

    private @Getter PolymerMessageHandler messageHandler;

    private @Getter BFScheduler scheduler;

    @Override
    public final void onEnable() {
        if (requireVersion() != null && !requireVersion().isBlank()) {
            if (!OtherUtils.isPolymerVersionAtLeast(requireVersion())) {
                getLogger().log(Level.SEVERE, """
                        \n
                        This plugin requires Polymer version %1$s or higher.
                        It will disable automatically.
                        """.formatted(requireVersion()));
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }
        if (requireApiVersion() > 0) {
            if (PolymerConstants.API_VERSION != requireApiVersion()) {
                getLogger().log(Level.SEVERE, """
                        \n
                        This plugin %1$s requires Polymer API version %2$d.
                        But the api version is %3$d instead.
                        It will disable automatically.
                        Try to use newer Polymer version or older Polymer version.
                        """.formatted(getPluginName(), requireApiVersion(), PolymerConstants.API_VERSION));
            }
        }

        completeDefaultConfig();
        scheduler = new BFScheduler(this);
        messageHandler = new PolymerMessageHandler(this);
        onPlEnable();

        for (PolymerCommand command : registerCommands()) {
            if (isDebug())
                getLogger().warning("Registering command: " + command.getLabel() + ", plugin: " + getPluginName());
            if (command.requirePlugin() != null && !command.requirePlugin().isBlank()) {
                if (Bukkit.getPluginManager().isPluginEnabled(command.requirePlugin())){
                    Bukkit.getCommandMap().register(getPluginName(), command);
                }
            } else {
                if (Bukkit.getCommandMap().getCommand(command.getLabel()) != null) {
                    Bukkit.getCommandMap().getKnownCommands().remove(command.getLabel());
                }
                Bukkit.getCommandMap().register(getPluginName(), command);
            }
        }
    }

    @Override
    public final void onDisable() {
        if (metrics != null) {
            metrics.shutdown();
        }
        scheduler.stopAllTask();
        onPlDisable();
    }

    /**
     * Makes old api compatibility
     * @return the plugin name
     */
    public String getPluginName() {
        try {
            Object pluginMeta = this.getClass().getMethod("getPluginMeta").invoke(this);
            return pluginMeta.getClass().getMethod("getName").invoke(pluginMeta).toString();
        } catch (Exception e) {
            return getDescription().getName();
        }
    }

    public String getPluginVersion() {
        try {
            Object pluginMeta = this.getClass().getMethod("getPluginMeta").invoke(this);
            return pluginMeta.getClass().getMethod("getVersion").invoke(pluginMeta).toString();
        } catch (Exception e) {
            return getDescription().getVersion();
        }
    }

    protected final void startMetrics(int pluginId) {
        metrics = new Metrics(this, pluginId);
    }

    public static <T extends PolymerPlugin> PolymerPlugin getPolymerPlugin(@NotNull Class<T> clazz) {
        try {
            return getPlugin(clazz);
        } catch (Exception e) {
            return null;
        }
    }

    //Needs impl
    public abstract void onPlEnable();
    public abstract void onPlDisable();
    public abstract List<PolymerCommand> registerCommands();
    public abstract String requireVersion();

    /**
     * Requires a Polymer API version of the plugin.<br>
     * Set 0 or lower for no API requirement.
     * @return the required Polymer API version.
     */
    public abstract int requireApiVersion();
    /////

    public final void suggestSpark(){
        if (!getServer().getPluginManager().isPluginEnabled("spark")) {
            getLogger().log(Level.WARNING,"""
                    \n
                    ============================================================
                     We recommend you install Spark!!
                    
                     Spark is a plugin similar to Timings v2, but it has better analyzers and
                     viewing of TPS/MSPT/CPU usage and other functions.
                     
                     And in the Pufferfish/Purpur server core, the development team has disabled Timings v2
                     (although PufferFish/Paper can still be used, there will still be a warning).
                     
                     Spark's performance monitor GUI is more comfortable than Timings v2,
                     and many plugins now use the results of this monitor as one of their submission issues.
                     If you really need to check the server's recent usage, just install the Spark plugin.
                     
                     Download Spark plugin @ https://spark.lucko.me/
                    ============================================================
                    """, getPluginName());
        }
    }

    protected final void completeDefaultConfig(){
        FileUtil.completeFile(this, "config.yml");
    }

    public void reload() {
        reloadConfig();
        completeDefaultConfig();
        messageHandler.reload();
    }

    private boolean isDebug() {
        File folder = new File(getDataFolder().getParentFile(), "Polymer");
        File file = new File(folder, "config.yml");
        return YamlConfiguration.loadConfiguration(file).getBoolean("debug", false);
    }
}
