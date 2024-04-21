package org.lins.mmmjjkx.polymer;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.presets.sub.SubReloadCommand;
import io.github.linsminecraftstudio.polymer.utils.IterableUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public final class MainCmd extends PolymerCommand {
    public MainCmd() {
        super("polymer", Polymer.INSTANCE);
        this.registerSubCommand(new SubReloadCommand(Polymer.INSTANCE));
    }

    @Override
    public String getHelpDescription() {
        return "?";
    }

    @Override
    public String requirePlugin() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String alias) {
        sendMessage("Info.Plugin", Polymer.INSTANCE.getPluginVersion(),
                IterableUtil.asString(Polymer.INSTANCE.getDescription().getAuthors()),
                Bukkit.getMinecraftVersion());
    }
}
