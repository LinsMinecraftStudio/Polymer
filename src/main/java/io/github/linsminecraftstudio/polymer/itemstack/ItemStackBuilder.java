package io.github.linsminecraftstudio.polymer.itemstack;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.linsminecraftstudio.polymer.Polymer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
        itemStack.addUnsafeEnchantment(enchantment, lvl);
    }

    public void amount(int amount){
        itemStack.setAmount(amount);
    }

    public void flag(ItemFlag... flags){
        itemMeta.addItemFlags(flags);
    }

    public void customModelData(int customModelData){
        itemMeta.setCustomModelData(customModelData);
    }

    public void setNameInConfig(Plugin plugin, String node){
        LegacyComponentSerializer serializer = Polymer.serializer;
        itemMeta.displayName(serializer.deserialize(plugin.getConfig().getString(node,"")));
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

    public void nbt(String key, ItemStack stack){
        nbtItem.setItemStack(key, stack);
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
