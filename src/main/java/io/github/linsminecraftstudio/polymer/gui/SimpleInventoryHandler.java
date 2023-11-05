package io.github.linsminecraftstudio.polymer.gui;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.itemstack.ItemStackBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static io.github.linsminecraftstudio.polymer.objects.PolymerConstants.*;

public abstract class SimpleInventoryHandler {
    public static int[] BOARDER_SLOTS = new int[]{0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,45,46,47,48,50,51,52,53};

    public abstract void placeButtons(Player p, Inventory inv);
    public abstract Component title(Player p);
    public abstract void doListen(InventoryActionType type, Player p, int slot, Inventory inventory);

    public SimpleInventoryHandler() {
        Bukkit.getPluginManager().registerEvents(new Listener(), Polymer.INSTANCE);
    }

    public final void open(Player p) {
        Inventory inventory = Bukkit.createInventory(null, 54, title(p));
        placeRequired(p, inventory);
        placeButtons(p, inventory);
        p.openInventory(inventory);
    }

    private class Listener implements org.bukkit.event.Listener {
        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            Player p = (Player) event.getWhoClicked();
            if (event.getView().title().equals(title(p))) {
                if (event.getRawSlot() >= 0) {
                    doListen(InventoryActionType.CLICK, p, event.getRawSlot(), event.getInventory());
                    event.setCancelled(true);
                }
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            Player p = (Player) event.getPlayer();
            if (event.getView().title().equals(title(p))) {
                doListen(InventoryActionType.CLOSE, p, -1, event.getInventory());
            }
        }
    }

    private void placeRequired(Player p, Inventory inventory) {
        ItemStackBuilder builder = new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE, 1).name(Component.empty());
        ItemStack boarder = builder.build();

        for (int sl : BOARDER_SLOTS) {
            inventory.setItem(sl, boarder);
        }

        inventory.setItem(PREV_PAGE_SLOT, boarder);
        inventory.setItem(NEXT_PAGE_SLOT, boarder);
        inventory.setItem(SEARCH_BUTTON_SLOT, boarder);

        ItemStackBuilder close = new ItemStackBuilder(Material.BARRIER, 1)
                .name(Polymer.INSTANCE.getMessageHandler().getColored(p, "GUI.Close"));
        ItemStack closeButton = close.build();

        inventory.setItem(CLOSE_BUTTON_SLOT, closeButton);
    }
}
