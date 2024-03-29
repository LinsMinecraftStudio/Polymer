package io.github.linsminecraftstudio.polymer.gui;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.github.linsminecraftstudio.polymer.TempPolymer;
import io.github.linsminecraftstudio.polymer.itemstack.ItemStackBuilder;
import io.github.linsminecraftstudio.polymer.utils.IterableUtil;
import io.github.linsminecraftstudio.polymer.utils.UserInputGetter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

import static io.github.linsminecraftstudio.polymer.objects.PolymerConstants.*;

public abstract class MultiPageInventoryHandler<T> {
    @RegExp
    public static String CURRENT_PAGE_VAR = "%page%";
    @RegExp
    public static String MAX_PAGE_VAR = "%max%";

    @Setter List<T> data;
    private List<Inventory> inventoryCache = new ArrayList<>();

    ///Implements
    public abstract Component title(Player p);
    public abstract Component search(Player p);
    public abstract void buttonHandle(Player p, int slot, T data);
    public abstract ItemStack getItemStackButton(Player p, int slot, T data);
    public abstract String toSearchableText(T data);
    ///

    private final Map<UUID, Integer> map = new HashMap<>();

    public MultiPageInventoryHandler(List<T> data) {
        Preconditions.checkNotNull(data, "data shouldn't be null");
        this.data = data;
    }

    public final void openInventory(Player p) {
        openInventory(p, getPage(p.getUniqueId()));
    }

    public void openInventory(Player p, int page) {
        if (isDataChanged()) {
            inventoryCache.clear();
        }

        List<Inventory> inventories = getOrGenerateInventories(p);

        if (inventories.isEmpty()) {
            TempPolymer.getInstance().getMessageHandler().sendMessage(p, "GUI.DataEmpty");
            return;
        }

        Inventory inventory = inventories.get(page - 1);
        if (inventory == null) {
            Inventory first = inventories.get(0);
            if (first == null) {
                TempPolymer.getInstance().getMessageHandler().sendMessage(p, "GUI.DataEmpty");
                return;
            }
            p.openInventory(first);
        } else {
            Bukkit.getPluginManager().registerEvents(new Listener(), TempPolymer.getInstance());
            map.put(p.getUniqueId(), page);
            p.openInventory(inventory);
        }
    }

    public final int getPage(UUID u) {
        return map.getOrDefault(u, 1);
    }

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

    List<Inventory> generateInventories(Player p) {
        List<List<T>> partedData = Lists.partition(data, 28);
        List<Inventory> inventories = new ArrayList<>();

        for (int i = 0; i < partedData.size(); i++) {
            List<T> data = partedData.get(i);
            Inventory inventory = Bukkit.createInventory(null, 54, doParse(title(p), i+1));

            placeItems(p, inventory);

            int dataIndex = 0;

            for (int sl : CONTENTS_SLOTS) {
                if (dataIndex < data.size()) {
                    ItemStack itemStack = getItemStackButton(p, sl, data.get(dataIndex));
                    inventory.setItem(sl, itemStack);
                    dataIndex++;
                }
            }

            inventories.add(inventory);
        }

        return inventories;
    }

    private Component doParse(Component msg, int current) {
        int max = Lists.partition(data, 28).size();
        return msg.replaceText(TextReplacementConfig.builder().match(CURRENT_PAGE_VAR).replacement(String.valueOf(current)).build())
                .replaceText(TextReplacementConfig.builder().match(MAX_PAGE_VAR).replacement(String.valueOf(max)).build());
    }

    class Listener implements org.bukkit.event.Listener {
        @EventHandler
        public void onOpen(InventoryOpenEvent e) {
            handleOpen(e);
        }

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            Player player = (Player) e.getWhoClicked();
            UUID uuid = player.getUniqueId();
            if (e.getView().title().equals(doParse(title(player), getPage(uuid)))) {
                int slot = e.getRawSlot();
                List<Inventory> inventories = getOrGenerateInventories(player);
                if (slot == NEXT_PAGE_SLOT) {
                    int p = getPage(uuid);
                    e.setCancelled(true);
                    if (inventories.size() == p) {
                        return;
                    }
                    p += 1;
                    map.put(uuid,  p);
                    player.closeInventory();
                    player.openInventory(inventories.get(p - 1));
                } else if (slot == SEARCH_BUTTON_SLOT) {
                    e.setCancelled(true);
                    doSearch(player);
                    HandlerList.unregisterAll(this);
                } else if (slot == PREV_PAGE_SLOT){
                    int p = getPage(uuid);
                    if (p > 1) {
                        p -= 1;
                        map.put(uuid, p);
                        player.closeInventory();
                        player.openInventory(inventories.get(p - 1));
                    }
                    e.setCancelled(true);
                } else if (slot == CLOSE_BUTTON_SLOT) {
                    e.setCancelled(true);
                    player.closeInventory();
                } else if (Arrays.stream(BOARDER_SLOTS).anyMatch(i -> i == slot)){
                    e.setCancelled(true);
                } else {
                    int page = getPage(uuid);
                    int index = (CONTENTS_SLOTS.length * (page - 1)) + IterableUtil.indexOf(CONTENTS_SLOTS, slot);
                    try {
                        T t = data.get(index);
                        if (t != null && e.getCurrentItem() != null) {
                            buttonHandle(player, e.getRawSlot(), t);
                        }
                        e.setCancelled(true);
                    } catch (IndexOutOfBoundsException ignored) {}
                    e.setCancelled(true);
                }
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e) {
            Player player = (Player) e.getPlayer();
            UUID uuid = player.getUniqueId();
            if (e.getView().title().equals(doParse(title(player), getPage(uuid)))) {
                map.put(uuid, getPage(uuid));
            }
        }
    }

    @ApiStatus.OverrideOnly
    public void handleOpen(InventoryOpenEvent e) {
    }

    void placeItems(Player p, Inventory inventory) {
        ItemStackBuilder builder = new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE, 1).name(Component.empty());
        ItemStack boarder = builder.build();

        for (int sl : BOARDER_SLOTS) {
            inventory.setItem(sl, boarder);
        }

        ItemStackBuilder search = new ItemStackBuilder(Material.WRITABLE_BOOK, 1)
                .name(TempPolymer.getInstance().getMessageHandler().getColored(p, "GUI.Search"));
        ItemStack searchButton = search.build();

        ItemStackBuilder close = new ItemStackBuilder(Material.BARRIER, 1)
                .name(TempPolymer.getInstance().getMessageHandler().getColored(p, "GUI.Close"));
        ItemStack closeButton = close.build();

        ItemStackBuilder prev = new ItemStackBuilder(Material.PLAYER_HEAD, 1)
                .name(TempPolymer.getInstance().getMessageHandler().getColored(p, "GUI.Previous"))
                .head("http://textures.minecraft.net/texture/bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9");
        ItemStack prevButton = prev.build();

        ItemStackBuilder next = new ItemStackBuilder(Material.PLAYER_HEAD, 1)
                .name(TempPolymer.getInstance().getMessageHandler().getColored(p, "GUI.Next"))
                .head("http://textures.minecraft.net/texture/19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf");
        ItemStack nextButton = next.build();

        inventory.setItem(SEARCH_BUTTON_SLOT, searchButton);
        inventory.setItem(CLOSE_BUTTON_SLOT, closeButton);
        inventory.setItem(PREV_PAGE_SLOT, prevButton);
        inventory.setItem(NEXT_PAGE_SLOT, nextButton);
    }

    void doSearch(Player p) {
        p.closeInventory();
        String input = UserInputGetter.getUserInput(search(p), p);
        SearchMultiPageInventory<T> searchMultiPageInventory = new SearchMultiPageInventory<>(data, this, input);
        searchMultiPageInventory.openInventory(p);
    }
}
