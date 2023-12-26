package io.github.linsminecraftstudio.polymer.utils;

import io.github.linsminecraftstudio.polymer.TempPolymer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class UserInputGetter {
    public static @Nullable String getUserInput(Component message, Player p) {
        return getUserInput(message, p, "##QUIT");
    }

    public static @Nullable String getUserInput(Component message, Player p, String quit) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            AtomicReference<String> str = new AtomicReference<>(null);
            InputListener inputListener = new InputListener(message, p, str::set, quit);
            synchronized (inputListener) {
                while (!inputListener.getResult()) {
                    try {
                        inputListener.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                return str.get();
            }
        });
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull List<String> getUserInputMultiply(Component message, Player p, String quit) {
        List<String> list = new ArrayList<>();
        AtomicReference<String> userInput = new AtomicReference<>(null);
        do {
            userInput.set(getUserInput(message, p, quit));
            if (userInput.get() != null) {
                list.add(userInput.get());
            }
        } while (userInput.get() == null);
        return list;
    }

    private record InputListener(Component message, Player player, Consumer<String> handler, String quitMsg) implements Listener {
        private static boolean result;

        private InputListener(Component message, Player player, Consumer<String> handler, String quitMsg) {
            this.quitMsg = quitMsg;
            this.message = message.appendNewline().append(
                    TempPolymer.getInstance().getMessageHandler().getColored(player, "Info.InputQuit").replaceText(builder ->
                            builder.match("%s").replacement(this.quitMsg)));
            this.player = player;
            this.handler = handler;

            player.sendMessage(this.message);
            result = false;
            Bukkit.getPluginManager().registerEvents(this, TempPolymer.getInstance());
        }

        @EventHandler
        public void chatEvent(AsyncChatEvent e) {
            Player p = e.getPlayer();
            if (player.getUniqueId().equals(p.getUniqueId())) {
                String input = ObjectConverter.serializer.serialize(e.message());
                if (!input.equals(quitMsg)) {
                    handler.accept(input);
                }
                result = true;
                e.setCancelled(true);
                HandlerList.unregisterAll(this);

                synchronized (this) {
                    this.notify();
                }
            }
        }

        public boolean getResult() {
            return result;
        }
    }
}
