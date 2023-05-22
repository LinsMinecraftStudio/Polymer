package io.github.linsminecraftstudio.polymer.itemstack;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class ItemStackConverter {
    public static ItemStack toItemStack(ConfigurationSection section){
        String mat = section.getString("material","STONE");
        Material material = Material.getMaterial(mat);
        if (material == null){
            material = Material.STONE;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (section.contains("amount")) {
            item.setAmount(section.getInt("amount", 1));
        }
        if (meta != null) {
            if (section.contains("displayname")) {
                meta.setDisplayName(section.getString("displayname"));
            }
            if (section.contains("lore")) {
                meta.setLore(section.getStringList("lore"));
            }
            if (section.contains("customModelData")){
                meta.setCustomModelData(section.getInt("customModelData"));
            }
            ConfigurationSection section2 = section.getConfigurationSection("enchants");
            if (section2 != null) {
                for (String enchant : section2.getKeys(false)){
                    ConfigurationSection section3 = section2.getConfigurationSection(enchant);
                    if (section3 == null) {continue;}
                    Enchantment enchantment = Enchantment.getByKey(
                            NamespacedKey.fromString(section3.getString("key","")));
                    if (enchantment != null) {
                        int level = section3.getInt(enchant);
                        item.addUnsafeEnchantment(enchantment, level);
                    }
                }
            }
            ConfigurationSection section4 = section.getConfigurationSection("nbt");
            if (section4 != null) {
                item.setItemMeta(meta);
                NBTItem nbtItem = new NBTItem(item);
                for (String nbtKey : section4.getKeys(false)){
                    if (section4.isString(nbtKey)) {
                        nbtItem.setString(nbtKey, section4.getString(nbtKey));
                    } else if (section4.isInt(nbtKey)) {
                        nbtItem.setInteger(nbtKey, section4.getInt(nbtKey));
                    } else if (section4.isBoolean(nbtKey)) {
                        nbtItem.setBoolean(nbtKey, section4.getBoolean(nbtKey));
                    } else if (section4.isDouble(nbtKey)) {
                        nbtItem.setDouble(nbtKey, section4.getDouble(nbtKey));
                    } else if (section4.isLong(nbtKey)) {
                        nbtItem.setLong(nbtKey, section4.getLong(nbtKey));
                    } else if (section4.isItemStack(nbtKey)) {
                        nbtItem.setItemStack(nbtKey, section4.getItemStack(nbtKey));
                    }
                }
                return nbtItem.getItem();
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public static Map<String,Object> asMap(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        Map<String,Object> map = new HashMap<>();
        map.put("material", item.getType().toString());
        map.put("amount", item.getAmount());
        if(meta != null){
            if(meta.hasDisplayName()){
                map.put("displayname", meta.getDisplayName());
            }
            if(meta.hasLore()){
                map.put("lore", meta.getLore());
            }
            if(meta.hasEnchants()){
                Map<String,Integer> enchantsMap = new HashMap<>();
                Map<Enchantment, Integer> enchants = meta.getEnchants();
                for (Enchantment enchantment : enchants.keySet()) {
                    NamespacedKey key = enchantment.getKey();
                    enchantsMap.put(key.toString(), enchants.get(enchantment));
                }
                map.put("enchants", enchantsMap);
            }
            if (meta.hasCustomModelData()){
                map.put("customModelData", meta.getCustomModelData());
            }
            map.put("unbreakable", meta.isUnbreakable());
            if (hasNBT(item)) {
                NBTItem nbtItem = new NBTItem(item);
                Map<String,Object> nbtMap = new HashMap<>();
                for (String key : nbtItem.getKeys()) {
                    nbtMap.put(key, nbtItem.getOrNull(key, Object.class));
                }
                map.put("nbt", nbtMap);
            }
        }
        return map;
    }

    public static boolean hasNBT(ItemStack stack){
        return new NBTItem(stack).hasNBTData();
    }
}
