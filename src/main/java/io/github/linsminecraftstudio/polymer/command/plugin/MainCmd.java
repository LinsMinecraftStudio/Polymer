package io.github.linsminecraftstudio.polymer.command.plugin;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.SubCommand;
import io.github.linsminecraftstudio.polymer.command.presets.SubReloadCommand;
import io.github.linsminecraftstudio.polymer.objects.MapBuilder;
import io.github.linsminecraftstudio.polymer.utils.ListUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class MainCmd extends PolymerCommand {
    public MainCmd(@NotNull String name) {
        super(name);
        this.registerSubCommand(new SubReloadCommand(Polymer.INSTANCE));
        this.registerSubCommand(new TestCmd("sfr"));
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

    private static class TestCmd extends SubCommand {
        public TestCmd(@NotNull String name) {
            super(name);
        }

        @Override
        public boolean enabled() {
            return true;
        }

        @Override
        public Map<Integer, List<String>> tabCompletion(CommandSender sender) {
            return new MapBuilder<Integer, List<String>>()
                    .put(0, List.of("test"))
                    .build();
        }

        @Override
        public void execute(CommandSender sender, String alias) {
            sender.sendMessage("oo");
        }
    }
}
