package com.newestaf.enhancedenchantment.enchant;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;

public final class EnchantmentUtil {

    public static Map<Enchantment, Integer> getEnchants(ItemMeta itemMeta) {
        if (itemMeta == null) {
            return Map.of();
        }

        if (itemMeta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
            enchantmentStorageMeta.getStoredEnchants();
        }

        return itemMeta.getEnchants();
    }


    public static void addEnchants(
            ItemMeta itemMeta,
            Map<Enchantment, Integer> enchants
    ) {
        if (itemMeta == null) {
            return;
        }

        BiConsumer<Enchantment, Integer> addEnchant;

        if (itemMeta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
            addEnchant = (enchant, level) -> enchantmentStorageMeta.addStoredEnchant(enchant, level, true);
        }
        else {
            addEnchant = (enchant, level) -> itemMeta.addEnchant(enchant, level, true);
        }

        enchants.forEach(addEnchant);

    }

    private EnchantmentUtil() {
        throw new IllegalStateException();
    }

}
