package com.newestaf.enhancedenchantment.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MetaCachedStack {

    private final ItemStack itemStack;
    private final CachedValue<ItemMeta> metaCache;

    public MetaCachedStack(ItemStack itemStack) {
        this.itemStack = itemStack == null ? ItemUtil.AIR : itemStack;
        this.metaCache = new CachedValue<>(this.itemStack::getItemMeta);
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public ItemMeta getMeta() {
        return this.metaCache.get();
    }
}
