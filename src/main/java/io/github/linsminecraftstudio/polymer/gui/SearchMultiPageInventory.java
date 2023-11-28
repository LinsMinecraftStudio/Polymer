package io.github.linsminecraftstudio.polymer.gui;

import com.google.common.collect.Lists;
import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.objects.other.LockableValue;
import io.github.linsminecraftstudio.polymer.utils.IterableUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.linsminecraftstudio.polymer.objects.PolymerConstants.*;

class SearchMultiPageInventory<T> extends MultiPageInventoryHandler<T> {
    private final String input;
    private final MultiPageInventoryHandler<T> instance;
    private final LockableValue<Player> lockableValue;
    private int page = 1;


    public SearchMultiPageInventory(List<T> data, MultiPageInventoryHandler<T> instance, String input) {
        super(data);
        this.instance = instance;
        this.input = input;
        this.lockableValue = new LockableValue<>();
    }

    @Override
    public void openInventory(Player p, int page) {
        lockableValue.set(p);
        lockableValue.lock();
        List<Inventory> inventories = generateInventories(p);
        if (inventories.isEmpty()) {
            Polymer.INSTANCE.getMessageHandler().sendTitle(p, "GUI.SearchNoResult");
            Polymer.INSTANCE.getMessageHandler().sendSubTitle(p, "GUI.SearchNoResultSub");

            Polymer.INSTANCE.getScheduler().scheduleDelay(() -> instance.openInventory(p), 5 * 20L);
        }else {
            Inventory inventory = inventories.get(0);
            Bukkit.getPluginManager().registerEvents(new Listener(), Polymer.INSTANCE);
            p.openInventory(inventory);
        }
    }

    @Override
    List<Inventory> generateInventories(Player p) {
        List<List<T>> partedData = Lists.partition(find(), 28);
        List<Inventory> inventories = new ArrayList<>();

        if (partedData.isEmpty()) {
            return inventories;
        }

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

    @Override
    public Component title(Player p) {
        return Polymer.INSTANCE.getMessageHandler().getColored(p, "GUI.SearchResultTitle");
    }

    @Override
    public Component search(Player p) {
        return instance.search(p);
    }

    @Override
    public void buttonHandle(Player p, int slot, T data) {
        instance.buttonHandle(p, slot, data);
    }

    @Override
    public ItemStack getItemStackButton(Player p, int slot, T data) {
        return instance.getItemStackButton(p, slot, data);
    }

    @Override
    public String toSearchableText(T data) {
        return instance.toSearchableText(data);
    }

    private List<T> find() {
        List<T> find = new ArrayList<>();

        for (T data : instance.data) {
            String text = instance.toSearchableText(data);

            if (input.equalsIgnoreCase(text)) {
                find.add(data);
                continue;
            }

            if (text.contains(input)) {
                find.add(data);
            }
        }

        return find;
    }

    private Component doParse(Component msg, int current) {
        int max = Lists.partition(find(), 28).size();
        return msg.replaceText(TextReplacementConfig.builder().match(CURRENT_PAGE_VAR).replacement(String.valueOf(current)).build())
                .replaceText(TextReplacementConfig.builder().match(MAX_PAGE_VAR).replacement(String.valueOf(max)).build());
    }

    private class Listener implements org.bukkit.event.Listener {
        @EventHandler
        public void overListen(InventoryClickEvent e) {
            Player pl = (Player) e.getWhoClicked();
            Component title = e.getView().title();
            List<Inventory> inventories = generateInventories(pl);
            List<T> data = find();
            if (title.equals(doParse(title(pl), page))) {
                handleClick(pl, e, inventories, data);
            }
        }

        private void handleClick(Player pl, InventoryClickEvent e, List<Inventory> inventories, List<T> data) {
            int slot = e.getRawSlot();
            if (e.getRawSlot() == SEARCH_BUTTON_SLOT) {
                e.setCancelled(true);
                doSearch(pl);
            } else if (slot == NEXT_PAGE_SLOT) {
                int p = page;
                e.setCancelled(true);
                if (inventories.size() == p) {
                    return;
                }
                p += 1;
                pl.closeInventory();
                pl.openInventory(inventories.get(p - 1));
                page = p;
            } else if (slot == PREV_PAGE_SLOT) {
                int p = page;
                if (p > 1) {
                    p -= 1;
                    pl.closeInventory();
                    pl.openInventory(inventories.get(p - 1));
                    page = p;
                }
                e.setCancelled(true);
            } else if (slot == CLOSE_BUTTON_SLOT) {
                e.setCancelled(true);
                pl.closeInventory();
                HandlerList.unregisterAll(this);
            } else if (Arrays.stream(BOARDER_SLOTS).anyMatch(i -> i == slot)) {
                e.setCancelled(true);
            } else {
                int index = (CONTENTS_SLOTS.length * (page - 1)) + IterableUtil.indexOf(CONTENTS_SLOTS, slot);
                try {
                    T t = data.get(index);
                    if (t != null && e.getCurrentItem() != null) {
                        buttonHandle(pl, e.getRawSlot(), t);
                    }
                    e.setCancelled(true);
                } catch (IndexOutOfBoundsException ignored) {}
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e) {
            Player pl = (Player) e.getPlayer();
            Component title = e.getView().title();
            if (title.equals(doParse(title(pl), page))) {
                HandlerList.unregisterAll(this);
            }
        }
    }
}
