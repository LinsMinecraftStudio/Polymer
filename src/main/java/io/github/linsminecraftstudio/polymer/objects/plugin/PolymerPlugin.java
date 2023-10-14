package io.github.linsminecraftstudio.polymer.objects.plugin;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.utils.FileUtil;
import io.github.linsminecraftstudio.polymer.utils.Metrics;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;

/**
 * Created for tag polymer plugin and make useful methods
 */
public abstract class PolymerPlugin extends JavaPlugin {
    private Metrics metrics;

    @Override
    public final void onEnable() {
        if (!OtherUtils.isPolymerVersionAtLeast(requireVersion())) {
            Polymer.INSTANCE.getLogger().log(Level.SEVERE, """
                    Plugin %1$s requires Polymer version %2$s.
                    But the version is %3$s instead.
                    It will disable automatically.
                    """.formatted(getPluginMeta().getName(), requireVersion(), Polymer.INSTANCE.getPluginMeta().getVersion()));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        completeDefaultConfig();
        onPlEnable();
        for (PolymerCommand command : registerCommands()) {
            if (Polymer.isDebug()) Polymer.INSTANCE.getLogger().warning("Registering command: "+command.getLabel()+
                    ", plugin: " + getPluginMeta().getName());
            if (command.requirePlugin() != null && !command.requirePlugin().isBlank()) {
                if (Bukkit.getPluginManager().isPluginEnabled(command.requirePlugin())){
                    Bukkit.getCommandMap().register(getPluginMeta().getName(), command);
                }
            }else {
                if (Bukkit.getCommandMap().getCommand(command.getLabel()) != null) {
                    Bukkit.getCommandMap().getKnownCommands().remove(command.getLabel());
                }
                Bukkit.getCommandMap().register(getPluginMeta().getName(), command);
            }
        }
    }

    @Override
    public final void onDisable() {
        if (metrics != null) {
            metrics.shutdown();
        }
        onPlDisable();
        Polymer.INSTANCE.getLogger().info("Disabled plugin "+getPluginMeta().getName());
    }

    protected void startMetrics(int pluginId) {
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
    /////

    public void suggestSpark(){
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
                    """, getPluginMeta().getName());
        }
    }
    protected void completeDefaultConfig(){
        FileUtil.completeFile(this, "config.yml");
    }
    protected void completeLangFile(String... langNames){
        for (String lang : langNames){
            FileUtil.completeLangFile(this, "lang/"+lang+".yml");
        }
    }
}
