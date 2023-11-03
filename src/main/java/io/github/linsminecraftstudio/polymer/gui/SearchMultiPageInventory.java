package io.github.linsminecraftstudio.polymer.gui;

import com.google.common.collect.Lists;
import io.github.linsminecraftstudio.polymer.Polymer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static io.github.linsminecraftstudio.polymer.objects.PolymerConstants.CONTENTS_SLOTS;
import static io.github.linsminecraftstudio.polymer.objects.PolymerConstants.SEARCH_BUTTON_SLOT;

public class SearchMultiPageInventory<T> extends MultiPageInventoryHandler<T> {
    private final String input;
    private final MultiPageInventoryHandler<T> instance;
    private Player p;

    public SearchMultiPageInventory(List<T> data, MultiPageInventoryHandler<T> instance, String input) {
        super(data);
        this.instance = instance;
        this.input = input;

    }

    @Override
    public void openInventory(Player p, int page) {
        if (this.p == null) {
            this.p = p;
        } else {
            if (this.p != p) {
                throw new IllegalArgumentException("Player is not the same as the one this inventory was opened for, and it only support one player to open the inventory");
            }
        }
        List<Inventory> inventories = generateInventories(p);
        Inventory inventory = inventories.get(0);
        if (inventory == null) {
            Polymer.INSTANCE.getMessageHandler().sendTitle(p, "GUI.SearchNoResult");
            Polymer.INSTANCE.getMessageHandler().sendSubTitle(p, "GUI.SearchNoResultSub");
            CountDownLatch latch = new CountDownLatch(5);
            latch.countDown();
            try {
                latch.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance.openInventory(p);
        }else {
            p.openInventory(inventory);
            Bukkit.getPluginManager().registerEvents(new MultiPageInventoryHandler.Listener(), Polymer.INSTANCE);
            Bukkit.getPluginManager().registerEvents(new SearchMultiPageInventory.Listener(), Polymer.INSTANCE);
        }
    }

    @Override
    List<Inventory> generateInventories(Player p) {
        List<List<T>> partedData = Lists.partition(find(), 28);
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


    private class Listener implements org.bukkit.event.Listener {
        @EventHandler
        public void overListen(InventoryClickEvent e) {
            Player p = (Player) e.getWhoClicked();
            Component title = e.getView().title();
            if (title == doParse(title(p), getPage(p.getUniqueId()))) {
                if (e.getRawSlot() == SEARCH_BUTTON_SLOT) {
                    doSearch(p);
                    HandlerList.unregisterAll(this);
                }
            }
        }
    }
}
