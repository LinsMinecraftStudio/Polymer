package io.github.linsminecraftstudio.polymer.command.presets;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.interfaces.IListCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class ListCommand<T> extends PolymerCommand implements IListCommand<T> {
    private CommandSender sender;

    public ListCommand(@NotNull String name) {
        this(name, new ArrayList<>());
    }

    public ListCommand(@NotNull String name, List<String> aliases) {
        super(name, aliases);
    }

    @Override
    public String requirePlugin() {
        return null;
    }

    @Override
    public final @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args){
        return super.tabComplete(commandSender, s, args);
    }

    @Override
    public final void execute(@NotNull CommandSender sender, @NotNull String alias){
        this.sender = sender;
        if (hasPermission()){
            if (isArgEmpty()){
                sendMessages(1);
            } else if (argSize() == 1){
                sendMessages((int) getArgAsDoubleOrInt(0, true, false));
            } else {
                sendPolymerMessage(sender,"Command.ArgError");
            }
        }
    }

    private void sendMessages(int page){
        sendMessages(sender, page);
    }
}
