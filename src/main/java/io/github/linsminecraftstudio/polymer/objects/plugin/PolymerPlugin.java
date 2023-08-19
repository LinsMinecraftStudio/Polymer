package io.github.linsminecraftstudio.polymer.objects.plugin;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.objects.plugin.message.PolymerMessageHandler;
import io.github.linsminecraftstudio.polymer.utils.FileUtils;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;

/**
 * Created for tag polymer plugin and make useful methods
 */
public abstract class PolymerPlugin extends JavaPlugin {
    /**
     * The message handler
     * <h4>You should initialize it manually!</h4>
     */
    public static PolymerMessageHandler messageHandler;
    /**
     * The message handler
     * <h4>You should initialize it manually!</h4>
     */
    public static SimpleSettingsManager settings;

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
        onPlDisable();
        Polymer.INSTANCE.getLogger().info("Disabled plugin "+getPluginMeta().getName());
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
                    ============================================================
                     Spark is a plugin similar to Timings v2, but it has better analyzers and
                     viewing of TPS/MSPT/CPU usage and other functions.
                     
                     And in the Pufferfish/Purpur server core, the development team has disabled Timings v2
                     (although PufferFish can still be used, there will still be a warning).
                     
                     In order for you to better view the recent performance of the server,
                     we recommend using the Spark plugin.
                     
                     Download Spark plugin @ https://spark.lucko.me/
                    ============================================================
                    """, getPluginMeta().getName());
        }
    }
    protected void completeDefaultConfig(){
        FileUtils.completeFile(this, "config.yml");
    }
    protected void completeLangFile(String... langNames){
        for (String lang : langNames){
            FileUtils.completeLangFile(this, "lang/"+lang+".yml");
        }
    }
}
