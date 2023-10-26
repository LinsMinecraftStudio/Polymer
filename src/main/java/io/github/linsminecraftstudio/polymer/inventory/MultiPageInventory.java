package io.github.linsminecraftstudio.polymer.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.itemstack.ItemStackBuilder;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.RegExp;

import java.util.*;

import static io.github.linsminecraftstudio.polymer.objects.PolymerConstants.*;

public abstract class MultiPageInventory<T> {
    @RegExp
    public static String CURRENT_PAGE_VAR = "%page%";
    @RegExp
    public static String MAX_PAGE_VAR = "%max%";

    @Setter
    private List<T> data;
    private List<Inventory> inventoryCache = new ArrayList<>();

    public abstract Component title();

    /**
     * offer args: {@link Player} player, {@link Integer} slot and {@link ItemStack} stack
     * @return a ciconsumer
     */
    public abstract void buttonHandle(Player p, int slot, ItemStack button);
    public abstract ItemStack getItemStackButton(Player p, int slot, T data);

    private final Map<UUID, Integer> map = new HashMap<>();

    public MultiPageInventory(List<T> data) {
        Preconditions.checkNotNull(data, "data shouldn't be null");
        Preconditions.checkState(!data.isEmpty(), "data shouldn't be empty");
        this.data = data;
    }

    public final void openInventory(Player p) {
        openInventory(p, 1);
    }

    public final void openInventory(Player p, int page) {
        if (isDataChanged()) {
            inventoryCache.clear();
        }

        List<Inventory> inventories = getOrGenerateInventories(p);
        Inventory inventory = inventories.get(page - 1);
        Bukkit.getPluginManager().registerEvents(new Listener(), Polymer.INSTANCE);
        map.put(p.getUniqueId(), page);
        p.openInventory(inventory);
    }

    public int getPage(UUID u) {
        return map.getOrDefault(u, ERROR_CODE);
    }

//private methods
    private boolean isDataChanged() {
        return inventoryCache.equals(data);
    }

    private List<Inventory> getOrGenerateInventories(Player p) {
        if (!inventoryCache.isEmpty() && !isDataChanged()) {
            return inventoryCache;
        }

        List<Inventory> inventories = generateInventories(p);
        inventoryCache = inventories;
        return inventories;
    }

    private List<Inventory> generateInventories(Player p) {
        List<List<T>> partedData = Lists.partition(data, 51 - BOARDER_SLOTS.length);
        List<Inventory> inventories = new ArrayList<>();
        for (int i = 0; i < partedData.size(); i++) {
            List<T> data = partedData.get(i);
            ItemStackBuilder builder = new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE, 1);
            builder.name(Component.empty());
            ItemStack boarder = builder.build();
            Inventory inventory = Bukkit.createInventory(null, 54, doParse(i+1));

            for (int sl : BOARDER_SLOTS) {
                inventory.setItem(sl, boarder);
            }

            ItemStackBuilder close = new ItemStackBuilder(Material.BARRIER, 1);
            close.name(Polymer.INSTANCE.getMessageHandler().getColored(p, "GUI.Close"));
            ItemStack closeButton = close.build();
            inventory.setItem(CLOSE_BUTTON_SLOT, closeButton);

            ItemStackBuilder prev = new ItemStackBuilder(Material.PLAYER_HEAD, 1);
            prev.name(Polymer.INSTANCE.getMessageHandler().getColored(p, "GUI.Previous"));
            prev.head("MHF_ArrowLeft");
            ItemStack prevButton = prev.build();

            ItemStackBuilder next = new ItemStackBuilder(Material.PLAYER_HEAD, 1);
            next.name(Polymer.INSTANCE.getMessageHandler().getColored(p, "GUI.Next"));
            next.head("MHF_ArrowRight");
            ItemStack nextButton = next.build();

            inventory.setItem(PREV_PAGE_SLOT, prevButton);
            inventory.setItem(NEXT_PAGE_SLOT, nextButton);

            int dataIndex = 0;

            for (int sl : CONTENTS_SLOTS) {
                if (dataIndex < data.size()) {
                    // 如果data仍有项目可用，将其添加到Inventory
                    ItemStack itemStack = getItemStackButton(p, sl, data.get(dataIndex));
                    inventory.setItem(sl, itemStack);
                    dataIndex++;
                }
            }

            inventories.add(inventory);
        }

        return inventories;
    }

    private Component doParse(int current) {
        int max = Lists.partition(data, 51 - BOARDER_SLOTS.length).size();
        return title().replaceText(TextReplacementConfig.builder().match(CURRENT_PAGE_VAR).replacement(String.valueOf(current)).build())
                .replaceText(TextReplacementConfig.builder().match(MAX_PAGE_VAR).replacement(String.valueOf(max)).build());
    }

    private class Listener implements org.bukkit.event.Listener {
        @EventHandler
        public void onClick(InventoryClickEvent e) {
            Player player = (Player) e.getWhoClicked();
            UUID uuid = player.getUniqueId();
            if (e.getView().title().equals(doParse(map.getOrDefault(uuid, 1)))) {
                int next = NEXT_PAGE_SLOT;
                int prev = PREV_PAGE_SLOT;
                int close = CLOSE_BUTTON_SLOT;
                int slot = e.getRawSlot();
                List<Inventory> inventories = getOrGenerateInventories(player);
                if (slot == next) {
                    int p = map.getOrDefault(uuid, 1);
                    e.setCancelled(true);
                    if (inventories.size() == map.getOrDefault(uuid, 1)) {
                        return;
                    }
                    p += 1;
                    map.put(uuid,  p);
                    player.closeInventory();
                    player.openInventory(inventories.get(p - 1));
                } else if (slot == prev){
                    int p = map.getOrDefault(uuid, 1);
                    if (p > 1) {
                        p -= 1;
                        map.put(uuid, p);
                        player.closeInventory();
                        player.openInventory(inventories.get(p - 1));
                    }
                    e.setCancelled(true);
                } else if (slot == close) {
                    e.setCancelled(true);
                    HandlerList.unregisterAll(this);
                    player.closeInventory();
                } else if (Arrays.stream(BOARDER_SLOTS).anyMatch(i -> i == slot)){
                    e.setCancelled(true);
                } else {
                    buttonHandle(player, e.getRawSlot(), e.getCurrentItem());
                    e.setCancelled(true);
                }
            }
        }
    }
}
