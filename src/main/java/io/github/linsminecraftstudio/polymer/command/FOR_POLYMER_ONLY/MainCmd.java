package io.github.linsminecraftstudio.polymer.command.FOR_POLYMER_ONLY;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.presets.sub.SubReloadCommand;
import io.github.linsminecraftstudio.polymer.utils.IterableUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public final class MainCmd extends PolymerCommand {
    public MainCmd() {
        super("polymer");
        this.registerSubCommand(new SubReloadCommand(Polymer.INSTANCE));
    }

    @Override
    public String requirePlugin() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String alias) {
        sendMessage("Info.Plugin", Polymer.INSTANCE.getPluginVersion(),
                IterableUtil.asString(Polymer.INSTANCE.getPluginMeta().getAuthors()),
                Bukkit.getMinecraftVersion());
    }
}
