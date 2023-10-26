package io.github.linsminecraftstudio.polymer.inventory;

import io.github.linsminecraftstudio.polymer.Polymer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public abstract class SimpleInventory {

    public abstract void placeButtons(Player p, Inventory inv);
    public abstract Component title();
    public abstract void doListen(InventoryActionType type, Player p, int slot, Inventory inventory);

    public SimpleInventory() {
        Bukkit.getPluginManager().registerEvents(new Listener(), Polymer.INSTANCE);
    }

    public final void open(Player p) {
        Inventory inventory = Bukkit.createInventory(null, 54, title());
        placeButtons(p, inventory);
        p.openInventory(inventory);
    }

    private class Listener implements org.bukkit.event.Listener {
        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (event.getView().title().equals(title())) {
                if (event.getRawSlot() >= 0) {
                    doListen(InventoryActionType.CLICK, (Player) event.getWhoClicked(), event.getRawSlot(), event.getInventory());
                    event.setCancelled(true);
                }
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            if (event.getView().title().equals(title())) {
                doListen(InventoryActionType.CLOSE, (Player) event.getPlayer(), -1, event.getInventory());
            }
        }
    }
}
