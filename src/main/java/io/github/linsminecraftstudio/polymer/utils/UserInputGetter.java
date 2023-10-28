package io.github.linsminecraftstudio.polymer.utils;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class UserInputGetter {
    public static @Nullable String getUserInput(Component message, Player p) {
        AtomicReference<String> str = new AtomicReference<>(null);
        new InputListener(message, p, str::set);
        return str.get();
    }

    private record InputListener(Component message, Player player, Consumer<String> handler) implements Listener {
       private InputListener(Component message, Player player, Consumer<String> handler) {
           this.message = message.appendNewline()
                   .append(Polymer.INSTANCE.getMessageHandler().getColored(player, "Info.InputQuit"));
           this.player = player;
           this.handler = handler;
           Bukkit.getPluginManager().registerEvents(this, Polymer.INSTANCE);
       }

       @EventHandler
       public void chatEvent(AsyncChatEvent e) {
          Player p = e.getPlayer();
          if (player.getUniqueId().equals(p.getUniqueId())) {
              String input = ObjectConverter.serializer.serialize(e.message());
              if (!input.equals("##QUIT")) {
                  handler.accept(input);
                  e.setCancelled(true);
                  return;
              }
              e.setCancelled(true);
              HandlerList.unregisterAll(this);
          }
       }
    }
}
