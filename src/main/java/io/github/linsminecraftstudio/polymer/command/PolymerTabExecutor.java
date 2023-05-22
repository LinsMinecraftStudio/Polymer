package io.github.linsminecraftstudio.polymer.command;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public interface PolymerTabExecutor extends PolymerCommandExecutor, TabCompleter {
    @Override
    default void register(JavaPlugin plugin) {
        String require = requirePlugin();
        if (require == null){
            require = "";
        }
        if (!require.isBlank()){
            if (Bukkit.getPluginManager().isPluginEnabled(require)){
                try {
                    PluginCommand cmd = plugin.getCommand(name());
                    cmd.setExecutor(this);
                    cmd.setTabCompleter(this);
                }catch (Exception e) {
                    plugin.getLogger().warning("Failed to register command '"+name()+"' : "+e.getMessage());
                }

            }
        }else {
            try {
                PluginCommand cmd = plugin.getCommand(name());
                cmd.setExecutor(this);
                cmd.setTabCompleter(this);
            }catch (Exception e) {
                plugin.getLogger().warning("Failed to register command '"+name()+"' : "+e.getMessage());
            }
        }
    }

    default List<String> getPlayerNames(){
        List<String> list = new ArrayList<>();
        for (Player p: Bukkit.getOnlinePlayers()){
            list.add(p.getName());
        }
        return list;
    }
}
