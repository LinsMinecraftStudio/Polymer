package io.github.linsminecraftstudio.polymer.command.presets;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.interfaces.IListCommand;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class ListCommand<T> extends PolymerCommand implements IListCommand<T> {
    private CommandSender sender;

    public ListCommand(@NotNull String name, @NotNull PolymerPlugin plugin) {
        this(name, plugin, new ArrayList<>());
    }

    public ListCommand(@NotNull String name, @NotNull PolymerPlugin plugin, @NotNull List<String> aliases) {
        super(name, plugin, aliases);
    }

    @Override
    public String requirePlugin() {
        return null;
    }

    @Override
    public final @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args){
        return tabCompletes(commandSender);
    }

    @Override
    public final void execute(@NotNull CommandSender sender, @NotNull String alias){
        this.sender = sender;
        if (hasPermission()){
            if (isArgEmpty()){
                sendMessages(1);
            } else if (argSize() == 1){
                sendMessages(getArgAsDoubleOrInt(0, true, false).getB().intValue());
            } else {
                sendPolymerMessage(sender,"Command.ArgError");
            }
        }
    }

    private void sendMessages(int page){
        sendMessages(sender, page);
    }
}
