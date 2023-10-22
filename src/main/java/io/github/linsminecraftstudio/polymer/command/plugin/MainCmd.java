package io.github.linsminecraftstudio.polymer.command.plugin;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.presets.SubReloadCommand;
import io.github.linsminecraftstudio.polymer.utils.ListUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MainCmd extends PolymerCommand {
    public MainCmd(@NotNull String name) {
        super(name);
        this.registerSubCommand(new SubReloadCommand(Polymer.INSTANCE));
    }

    @Override
    public String requirePlugin() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String alias) {
        sendMessage("Info.Plugin", Polymer.INSTANCE.getPluginMeta().getVersion(),
                ListUtil.asString(Polymer.INSTANCE.getPluginMeta().getAuthors()),
                Bukkit.getMinecraftVersion());
    }
}
