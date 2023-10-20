package io.github.linsminecraftstudio.polymer.command.plugin;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.SubCommand;
import io.github.linsminecraftstudio.polymer.utils.ListUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class MainCmd extends PolymerCommand {
    public MainCmd(@NotNull String name) {
        super(name);
        this.registerSubCommand(new SubReload());
    }

    @Override
    public String requirePlugin() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String alias) {
        sendMessage("Info.Plugin", Polymer.INSTANCE.getPluginMeta().getName(),
                ListUtil.asString(Polymer.INSTANCE.getPluginMeta().getAuthors()),
                Bukkit.getMinecraftVersion());
    }

    public class SubReload extends SubCommand {

        public SubReload() {
            super("reload");
        }

        @Override
        public boolean enabled() {
            return true;
        }

        @Override
        public Map<Integer, List<String>> tabCompletion(CommandSender sender) {
            return null;
        }

        @Override
        public void execute(CommandSender sender, String alias) {
            if (hasPermission()) {
                Polymer.doReload();
                sendMessage("Info.ReloadSuccess");
            }
        }
    }
}
