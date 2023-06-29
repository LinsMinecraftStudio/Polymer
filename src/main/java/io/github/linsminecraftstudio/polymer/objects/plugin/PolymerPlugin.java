package io.github.linsminecraftstudio.polymer.objects.plugin;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
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
    @Override
    public void onEnable() {
        if (!OtherUtils.isPolymerVersionAtLeast(requireVersion())) {
            Polymer.INSTANCE.getLogger().log(Level.SEVERE, """
                    Plugin %1$s requires Polymer version %2$s.
                    But the version is %3$s instead.
                    It will disable automatically.
                    """.formatted(getPluginMeta().getName(), requireVersion(), Polymer.INSTANCE.getPluginMeta().getVersion()));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        onEnable();
        for (PolymerCommand command : registerCommands()) {
            if (command.requirePlugin() != null && !command.requirePlugin().isBlank()) {
                if (Bukkit.getPluginManager().isPluginEnabled(command.requirePlugin())){
                    if (Polymer.isDebug()) Polymer.INSTANCE.getLogger().warning("Registering command: "+command.getLabel()+
                            ", plugin: " + getPluginMeta().getName());
                    Bukkit.getCommandMap().register(getPluginMeta().getName(), command);
                }
            }else {
                if (Polymer.isDebug()) Polymer.INSTANCE.getLogger().warning("Registering command: "+command.getLabel()+
                        ", plugin: " + getPluginMeta().getName());
                if (Bukkit.getCommandMap().getCommand(command.getLabel()) != null) {
                    Bukkit.getCommandMap().getKnownCommands().remove(command.getLabel());
                }
                Bukkit.getCommandMap().register(getPluginMeta().getName(), command);
            }
        }
    }
    @Override
    public void onDisable() {
        Polymer.INSTANCE.getLogger().info("Disabled plugin "+getPluginMeta().getName());
    }
    public abstract List<PolymerCommand> registerCommands();
    public abstract String requireVersion();
    public void suggestPaper(){
        if (!Polymer.isPaper()) {
            getLogger().log(Level.WARNING,"""
                    ============================================================
                     {} works better if you use Paper as your server software.
                     
                     Paper offers significant performance improvements,
                     bug fixes, security enhancements and optional
                     features for server owners to enhance their server.
                     
                     Paper includes Timings v2, which is significantly
                     better at diagnosing lag problems over v1.
                     
                     All of your plugins should still work, and the
                     Paper community will gladly help you fix any issues.
                     
                     Join the Paper Community @ https://papermc.io
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
