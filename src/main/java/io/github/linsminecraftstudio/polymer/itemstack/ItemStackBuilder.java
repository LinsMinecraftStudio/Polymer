package io.github.linsminecraftstudio.polymer.itemstack;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class ItemStackBuilder {
    private NBTItem nbtItem;
    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemStackBuilder(ItemStack item){
        this.itemStack = item;
        this.itemMeta = item.getItemMeta();
        this.nbtItem = new NBTItem(itemStack);
    }

    public ItemStackBuilder(Material material){
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public void name(String itemName){
        itemMeta.setDisplayName(itemName);
    }

    public void lore(List<String> lore){
        itemMeta.setLore(lore);
    }

    public void lore(String... lore){
        itemMeta.setLore(Arrays.stream(lore).toList());
    }

    public void unbreakable(boolean unbreakable){
        itemMeta.setUnbreakable(unbreakable);
    }

    public void enchantment(Enchantment enchantment, int lvl){
        itemStack.addUnsafeEnchantment(enchantment, lvl);
    }

    public void amount(int amount){
        itemStack.setAmount(amount);
    }

    public void flag(ItemFlag... flags){
        itemMeta.addItemFlags(flags);
    }

    public void setNameInConfig(Plugin plugin, String node){
        itemMeta.setDisplayName(plugin.getConfig().getString(node,""));
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

    public void nbt(String key, ItemStack stack){
        nbtItem.setItemStack(key, stack);
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
