package io.github.linsminecraftstudio.polymer.itemstack;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.linsminecraftstudio.polymer.utils.IterableUtil;
import io.github.linsminecraftstudio.polymer.utils.ObjectConverter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public final class ItemStackConverter {
    /**
     * Serialize an item stack from the given configuration section
     * @param section the configuration section
     * @return an item stack
     */
    public static ItemStack toItemStack(ConfigurationSection section){
        String mat = section.getString("material","STONE");
        Material material = Material.getMaterial(mat);
        if (material == null){
            material = Material.STONE;
        }

        ItemStackBuilder builder = new ItemStackBuilder(material);
        if (section.contains("amount")) {
            builder.amount(section.getInt("amount", 1));
        }
        if (section.contains("displayname")) {
            builder.name(ObjectConverter.toComponent(section.getString("displayname", "")));
        }
        if (section.contains("lore")) {
            builder.lore(IterableUtil.stringListToComponentList(section.getStringList("lore")));
        }
        if (section.contains("customModelData")){
            builder.customModelData(section.getInt("customModelData"));
        }
        if (section.contains("unbreakable")){
            builder.unbreakable(section.getBoolean("unbreakable"));
        }
        ConfigurationSection section2 = section.getConfigurationSection("enchants");
        if (section2 != null) {
            for (String enchant : section2.getKeys(false)){
                ConfigurationSection section3 = section2.getConfigurationSection(enchant);
                if (section3 == null) {continue;}
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.fromString(enchant));
                if (enchantment != null) {
                    int level = section3.getInt(enchant);
                    builder.enchantment(enchantment, level);
                }
            }
        }
        ConfigurationSection section4 = section.getConfigurationSection("nbt");
        if (section4 != null) {
            for (String nbtKey : section4.getKeys(false)){
                if (section4.isString(nbtKey)) {
                    builder.nbt(nbtKey, section4.getString(nbtKey));
                } else if (section4.isInt(nbtKey)) {
                    builder.nbt(nbtKey, section4.getInt(nbtKey));
                } else if (section4.isBoolean(nbtKey)) {
                    builder.nbt(nbtKey, section4.getBoolean(nbtKey));
                } else if (section4.isDouble(nbtKey)) {
                    builder.nbt(nbtKey, section4.getDouble(nbtKey));
                } else if (section4.isLong(nbtKey)) {
                    builder.nbt(nbtKey, section4.getLong(nbtKey));
                }
            }
        }
        return builder.build();
    }

    /**
     * Make an item stack to a map
     * @param item the item stack
     * @return a map
     */
    public static Map<String,Object> asMap(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        Map<String,Object> map = new HashMap<>();
        map.put("material", item.getType().toString());
        map.put("amount", item.getAmount());
        if(meta != null){
            Component displayName = meta.displayName();
            if(displayName != null){
                map.put("displayname", displayName);
            }
            if(meta.hasLore()){
                map.put("lore", meta.lore());
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

    /**
     * Check the given stack has nbt or not
     * @param stack the stack to check
     * @return true if the stack has nbt or not
     */
    public static boolean hasNBT(ItemStack stack){
        return new NBTItem(stack).hasNBTData();
    }
}
