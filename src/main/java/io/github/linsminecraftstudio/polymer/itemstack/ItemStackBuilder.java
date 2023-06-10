package io.github.linsminecraftstudio.polymer.itemstack;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.linsminecraftstudio.polymer.utils.ComponentConverter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used for creating item stacks
 */
public class ItemStackBuilder {
    private final NBTItem nbtItem;
    private final ItemStack itemStack;
    private final ItemMeta itemMeta;
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    private int amount = 1;

    public ItemStackBuilder(ItemStack item){
        this.itemStack = item;
        this.itemMeta = item.getItemMeta();
        this.nbtItem = new NBTItem(itemStack);
    }

    public ItemStackBuilder(Material material){
        this(material, 1);
    }

    public ItemStackBuilder(Material material, int amount){
        this.itemStack = new ItemStack(material);
        this.amount = amount;
        this.itemMeta = itemStack.getItemMeta();
        this.nbtItem = new NBTItem(itemStack);
    }

    public void name(Component itemName){
        itemMeta.displayName(itemName);
    }

    public void lore(List<Component> lore){
        itemMeta.lore(lore);
    }

    public void lore(Component... lore){
        itemMeta.lore(Arrays.stream(lore).toList());
    }

    public void unbreakable(boolean unbreakable){
        itemMeta.setUnbreakable(unbreakable);
    }

    public void enchantment(Enchantment enchantment, int lvl){
        enchantments.put(enchantment, lvl);
    }

    public void amount(int amount){
        this.amount = amount;
    }

    public void flag(ItemFlag... flags){
        itemMeta.addItemFlags(flags);
    }

    public void customModelData(int customModelData){
        itemMeta.setCustomModelData(customModelData);
    }

    public void nameInConfig(Plugin plugin, String node){
        itemMeta.displayName(ComponentConverter.toComponent(plugin.getConfig().getString(node,"")));
    }

    public void nbt(String key, String value){
        nbtItem.setString(key, value);
    }

    public void nbt(String key, int value){
        nbtItem.setInteger(key, value);
    }

    public void nbt(String key, double value){
        nbtItem.setDouble(key, value);
    }

    public void nbt(String key, boolean value){
        nbtItem.setBoolean(key, value);
    }

    public void nbt(String key, long value){
        nbtItem.setLong(key, value);
    }

    public void removeNbt(String key){
        nbtItem.removeKey(key);
    }

    /**
     * Returns the item stack
     * @return an item stack
     */
    public ItemStack build() {
        ItemStack stack = nbtItem.getItem();
        stack.setAmount(amount);
        stack.addUnsafeEnchantments(enchantments);
        stack.setItemMeta(itemMeta);
        return stack;
    }
}
