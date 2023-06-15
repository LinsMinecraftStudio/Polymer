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
        if (requireVersion() > OtherUtils.getPolymerVersionWorth()) {
            getLogger().log(Level.SEVERE, """
                    Plugin %1$s requires Polymer version %2$d.
                    But the version is %3$d instead.
                    It will disable automatically.
                    """.formatted(getPluginMeta().getName(), requireVersion(), OtherUtils.getPolymerVersionWorth()));
            Bukkit.getPluginManager().disablePlugin(this);
        }
        onPluginEnable();
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
        onPluginDisable();
        getLogger().info("Disabled plugin "+getPluginMeta().getName());
    }
    public abstract void onPluginEnable();
    public void onPluginDisable(){}
    public abstract List<PolymerCommand> registerCommands();
    public abstract int requireVersion();
    protected void completeDefaultConfig(){
        FileUtils.completeFile(this, "config.yml");
    }
    protected void completeLangFile(String... langNames){
        for (String lang : langNames){
            FileUtils.completeLangFile(this, "lang/"+lang+".yml");
        }
    }
}
