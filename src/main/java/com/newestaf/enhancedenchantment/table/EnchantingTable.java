package com.newestaf.enhancedenchantment.table;

import com.newestaf.enhancedenchantment.enchant.EnchantData;
import com.newestaf.enhancedenchantment.util.WeightedRandom;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

public class EnchantingTable {

    private final Collection<Enchantment> enchantments;
    private final Enchantability enchantability;
    private BiPredicate<Enchantment, Enchantment> incompatibility;
    private ToIntFunction<Enchantment> maxLevel;

    public EnchantingTable(
            Collection<Enchantment> enchantments,
            Enchantability enchantability
    ) {
        this.enchantments = enchantments;
        this.enchantability = enchantability;
        this.incompatibility = (ench1, ench2) -> ench1.equals(ench2) || ench2.equals(ench1);
        this.maxLevel = Enchantment::getMaxLevel;
    }

    public void setIncompatibility(
            BiPredicate<Enchantment, Enchantment> incompatibility
    ) {
        this.incompatibility = (e1, e2) -> e1.equals(e2) || incompatibility.test(e1, e2);
    }

    public void setMaxLevel(ToIntFunction<Enchantment> maxLevel) {
        this.maxLevel = maxLevel;
    }

    public Map<Enchantment, Integer> apply(Random random, int enchantLevel) {
        if (this.enchantments.isEmpty() || enchantLevel <1) {
            return Collections.emptyMap();
        }

        int enchantQuality = getEnchantQuality(random, enchantLevel);

        Map<EnchantData, Integer> available = getAvailableResults(enchantQuality);

        Map<Enchantment, Integer> selected =new HashMap<>();

        addEnchant(random, selected, available);

        while (!available.isEmpty() && random.nextInt(50) < enchantQuality) {
            addEnchant(random, selected, available);
            enchantQuality /= 2;
        }

        return selected;

    }

    public EnchantmentOffer getOffer(
            Random random,
            int enchantLevel
    ) {
        if (enchantLevel < 1) {
            return null;
        }

        var rolledEnchants = this.apply(random, enchantLevel);

        return rolledEnchants.entrySet().stream().findFirst()
                .map(entry -> new EnchantmentOffer(entry.getKey(), entry.getValue(), enchantLevel))
                .orElse(null);
    }

    public static int[] getButtonLevels(Random random, int shelves) {
        shelves = Math.min(shelves, 15);
        int[] levels = new int[3];

        for (int button = 0; button < 3; ++button) {
            levels[button] = getButtonLevel(random, button, shelves);
        }

        return levels;
    }

    public static void updateButtons(Plugin plugin, Player player, EnchantmentOffer[] offers) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (int i = 1; i <=3; i++) {
                EnchantmentOffer offer = offers[i - 1];
                if (offer != null) {
                    player.setWindowProperty(
                            InventoryView.Property.valueOf("ENCHANT_BUTTON" + i),
                            offer.getCost()
                    );
                    player.setWindowProperty(
                            InventoryView.Property.valueOf("ENCHANT_LEVEL" + i),
                            offer.getEnchantmentLevel()
                    );
                    player.setWindowProperty(
                            InventoryView.Property.valueOf("ENCHANT_ID" + i),
                            getEnchantmentId(offer.getEnchantment())
                    );
                }
            }
        }, 1L);
    }


    private Map<EnchantData, Integer> getAvailableResults(int enchantQuality) {
        Map<EnchantData, Integer> available = new HashMap<>();

        for (Enchantment enchantment : this.enchantments) {
            EnchantData data = EnchantData.of(enchantment);

            for (int lvl = maxLevel.applyAsInt(enchantment); lvl >= enchantment.getStartLevel(); --lvl) {
                if (enchantQuality >= data.getMinCost(lvl) && enchantQuality <= data.getMaxCost(lvl)) {
                    available.put(data, lvl);
                    break;
                }
            }
        }
        return available;
    }

    private int getEnchantQuality(Random random, final int enchantLevel) {
        int enchantQuality = 2 * ((enchantability.value() / 4) + 1) - 1;
        enchantQuality = 1 + random.nextInt(enchantQuality);
        enchantQuality += enchantLevel;

        float bonus = (random.nextFloat(2) - 1F) * 0.15F;
        enchantQuality = Math.round(enchantQuality + enchantQuality * bonus);

        return Math.max(1, enchantQuality);
    }

    private void addEnchant(
            Random random,
            Map<Enchantment, Integer> selected,
            Map<EnchantData, Integer> available
    ) {
        if (available.isEmpty()) {
            return;
        }

        EnchantData enchantData = WeightedRandom.choose(random, available.keySet());

        selected.put(enchantData.getEnchantment(), available.remove(enchantData));

        available.keySet().removeIf(
                data -> this.incompatibility.test(data.getEnchantment(), enchantData.getEnchantment()));

    }

    private static int getButtonLevel(Random random, int button, int shelves) {
        int level = random.nextInt(8) + 1 + (shelves >> 1) + random.nextInt(shelves + 1);

        if (button == 0) {
            level = Math.max(level / 3, 1);
        } else if (button == 1) {
            level = level * 2 / 3 + 1;
        } else {
            level = Math.max(level, shelves * 2);
        }

        return level >= button + 1 ? level : 0;
    }

    private static int getEnchantmentId(Enchantment enchantment) {
        NamespacedKey enchantmentKey = enchantment.getKey();

        enchantment = Enchantment.getByKey(enchantmentKey);
        if (enchantment == null) {
            return 0;
        }

        try {
            Class<?> clazzRegistry = Class.forName("net.minecraft.core.IRegistry");
            // NMSREF net.minecraft.core.Registry#ENCHANTMENT
            Object enchantmentRegistry = clazzRegistry.getDeclaredField("W").get(null);
            // NMSREF net.minecraft.core.Registry#getId(java.lang.Object)
            Method methodRegistryGetId = clazzRegistry.getDeclaredMethod("a", Object.class);

            Method getHandle = enchantment.getClass().getDeclaredMethod("getHandle");

            return (int) methodRegistryGetId.invoke(enchantmentRegistry, getHandle.invoke(enchantment));
        } catch (ReflectiveOperationException | ClassCastException ignored) {

        }

        try {
            int enchantmentIndex = 0;

            for (Field field : Enchantment.class.getFields()) {
                if (Modifier.isStatic(field.getModifiers()) && Enchantment.class.equals(field.getType())) {
                    Enchantment enchantField = (Enchantment) field.get(null);
                    if (enchantField.getKey().equals(enchantmentKey)) {
                        return enchantmentIndex;
                    }
                    ++enchantmentIndex;
                }
            }
        } catch (IllegalAccessException ignored) {

        }

        Enchantment[] enchantments =Enchantment.values();
        for (int i = 0; i < enchantments.length; i++) {
            if (enchantments[i].getKey().equals(enchantmentKey)) {
                return i;
            }
        }

        return 0;

    }

}
