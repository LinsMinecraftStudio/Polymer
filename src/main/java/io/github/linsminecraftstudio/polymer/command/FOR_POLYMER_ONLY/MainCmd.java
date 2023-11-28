package io.github.linsminecraftstudio.polymer.command.FOR_POLYMER_ONLY;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.presets.sub.SubReloadCommand;
import io.github.linsminecraftstudio.polymer.utils.IterableUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class MainCmd extends PolymerCommand {
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
        sendMessage("Info.Plugin", Polymer.INSTANCE.getPluginVersion(),
                IterableUtil.asString(Polymer.INSTANCE.getPluginMeta().getAuthors()),
                Bukkit.getMinecraftVersion());
    }
}
