package com.newestaf.enhancedenchantment.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;

public final class ItemUtil {

    public static final ItemStack AIR = new ItemStack(Material.AIR) {
        @Override
        public void setType(@NotNull Material type) {
            throw new UnsupportedOperationException("Cannot modify AIR constant.");
        }
    };

    public static boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getType() == Material.AIR || itemStack.getAmount() < 1;
    }

    public static int getRepairCost(ItemMeta meta) {
        if (meta instanceof Repairable repairable) {
            return repairable.getRepairCost();
        }
        return 0;
    }

    private ItemUtil() {
        throw new IllegalStateException("Cannot instantiate static helper method container");
    }

}
