package com.newestaf.enhancedenchantment.table;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.LongSupplier;

public abstract class TableEnchantListener implements Listener {

    private final Random random = new Random();
    private final Plugin plugin;
    private final NamespacedKey key;

    protected TableEnchantListener(Plugin plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(this.plugin, "enchanting_table_seed");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public final void afterAnyEnchant(EnchantItemEvent event) {
        event.getEnchanter().getPersistentDataContainer().remove(key);
    }

    @EventHandler
    public final void onPreparedItemEnchant(PrepareItemEnchantEvent event) {

    }

    @EventHandler
    public final void onEnchantItem(EnchantItemEvent event) {

    }

    protected boolean isEnchantable(Player player, ItemStack enchanted) {
        return enchanted.getAmount() == 1
                || !isEligible(player, enchanted)
                || enchanted.getEnchantments().isEmpty();
    }

    protected abstract boolean isEligible(Player player, ItemStack enchanted);

    protected abstract EnchantingTable getTable(Player player, ItemStack enchanted);

    private long getSeed(Player player, int buttonIndex) {
        return getEnchantmentSeed(player, TableEnchantListener::getRandomSeed) + buttonIndex;
    }

    private long getEnchantmentSeed(Player player, LongSupplier supplier) {
        var seed = player.getPersistentDataContainer().get(key, PersistentDataType.LONG);

        if (seed == null) {
            seed = supplier.getAsLong();
            player.getPersistentDataContainer().set(key, PersistentDataType.LONG, seed);
        }

        return seed;
    }

    private static long getRandomSeed() {
        return ThreadLocalRandom.current().nextLong();
    }
}
