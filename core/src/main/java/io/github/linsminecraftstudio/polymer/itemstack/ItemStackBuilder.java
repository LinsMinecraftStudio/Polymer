package io.github.linsminecraftstudio.polymer.itemstack;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.base.Preconditions;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Use to create and edit item stacks
 */
public class ItemStackBuilder {
    private final NBTItem nbtItem;
    private final ItemMeta itemMeta;
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    private int amount;
    private String skull;

    public ItemStackBuilder(Material material){
        this(material, 1);
    }

    public ItemStackBuilder(Material material, int amount){
       this(new ItemStack(material, amount));
    }

    public ItemStackBuilder(ItemStack item){
        this.itemMeta = item.getItemMeta();
        this.nbtItem = new NBTItem(item);
        this.amount = item.getAmount();
    }


    public ItemStackBuilder name(Component itemName){
        return name(itemName, false);
    }

    public ItemStackBuilder name(Component itemName, boolean allowItalic){
        itemMeta.displayName(itemName.decoration(TextDecoration.ITALIC, allowItalic));
        return this;
    }

    public ItemStackBuilder lore(List<Component> lore){
        itemMeta.lore(lore);
        return this;
    }

    public ItemStackBuilder lore(Component... lore){
        itemMeta.lore(Arrays.stream(lore).toList());
        return this;
    }

    public ItemStackBuilder unbreakable(boolean unbreakable){
        itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemStackBuilder enchantment(Enchantment enchantment, int lvl){
        enchantments.put(enchantment, lvl);
        return this;
    }

    public ItemStackBuilder amount(int amount){
        this.amount = amount;
        return this;
    }

    public ItemStackBuilder flag(ItemFlag... flags){
        itemMeta.addItemFlags(flags);
        return this;
    }

    public ItemStackBuilder customModelData(int customModelData){
        itemMeta.setCustomModelData(customModelData);
        return this;
    }

    public ItemStackBuilder nbt(String key, String value){
        nbtItem.setString(key, value);
        return this;
    }

    public ItemStackBuilder nbt(String key, int value){
        nbtItem.setInteger(key, value);
        return this;
    }

    public ItemStackBuilder nbt(String key, double value){
        nbtItem.setDouble(key, value);
        return this;
    }

    public ItemStackBuilder nbt(String key, boolean value){
        nbtItem.setBoolean(key, value);
        return this;
    }

    public ItemStackBuilder nbt(String key, long value){
        nbtItem.setLong(key, value);
        return this;
    }

    public ItemStackBuilder nbt(String key, ItemStack value) {
        nbtItem.setItemStack(key, value);
        return this;
    }

    public ItemStackBuilder nbt(String key, int[] ints) {
        nbtItem.setIntArray(key, ints);
        return this;
    }

    public ItemStackBuilder removeNbt(String key){
        nbtItem.removeKey(key);
        return this;
    }

    public ItemStackBuilder head(String url){
        this.skull = url;
        return this;
    }

    /**
     * Returns the item stack
     * @return an item stack
     */
    public ItemStack build() {
        ItemStack stack = nbtItem.getItem();
        stack.setItemMeta(itemMeta);

        if (skull != null) {
            stack.setType(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) stack.getItemMeta();

            PlayerProfile profile = Bukkit.createProfileExact(UUID.randomUUID(), "fake");
            PlayerTextures textures = profile.getTextures();
            try {
                textures.setSkin(new URL(skull));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            profile.setTextures(textures);
            meta.setOwnerProfile(profile);

            syncMetaProperties(meta);

            stack.setItemMeta(meta);
        }

        stack.setAmount(amount);
        stack.addUnsafeEnchantments(enchantments);
        return stack;
    }

    private void syncMetaProperties(ItemMeta newMeta) {
        Preconditions.checkNotNull(newMeta, "new meta cannot be null");
        newMeta.setUnbreakable(itemMeta.isUnbreakable());
        if (itemMeta.hasDisplayName()) {
            newMeta.displayName(itemMeta.displayName());
        }
        if (itemMeta.hasLore()) {
            newMeta.lore(itemMeta.lore());
        }
        if (!itemMeta.getItemFlags().isEmpty()) {
            newMeta.addItemFlags(itemMeta.getItemFlags().toArray(new ItemFlag[]{}));
        }
        if (itemMeta.hasCustomModelData()) {
            newMeta.setCustomModelData(itemMeta.getCustomModelData());
        }
    }
}
