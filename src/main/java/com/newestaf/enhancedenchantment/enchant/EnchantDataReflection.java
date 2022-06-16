package com.newestaf.enhancedenchantment.enchant;

import com.newestaf.enhancedenchantment.util.ThrowingFunction;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.IntUnaryOperator;

public final class EnchantDataReflection {

    public static EnchantRarity getRarity(Enchantment enchantment) {
        return nmsHandler(enchantment, nmsEnchant -> {
            // NMSREF net.minecraft.world.item.enchantment.Enchantment#getRarity()
            Object enchantmentRarity = nmsEnchant.getClass().getDeclaredMethod("d").invoke(nmsEnchant);
            // NMSREF net.minecraft.world.item.enchantment.Enchantment$EnchantRarity#getWeight()
            int weight = (int) enchantmentRarity.getClass().getDeclaredMethod("a")
                    .invoke(enchantment);

            return EnchantRarity.of(weight);
        }, EnchantRarity.UNKNOWN);
    }

    public static IntUnaryOperator getMinCost(Enchantment enchantment) {
        // NMSREF net.minecraft.world.item.enchantment.Enchantment#getMinCost(int)
        return nmsIntUnaryOperator(enchantment, "a", EnchantDataReflection::defaultMinEnchantQuality);
    }

    public static IntUnaryOperator getMaxCost(Enchantment enchantment) {
        // NMSREF net.minecraft.world.item.enchantment.Enchantment#getMaxCost(int)
        return nmsIntUnaryOperator(enchantment, "b", EnchantDataReflection::defaultMaxEnchantQuality);
    }

    private static int defaultMinEnchantQuality(int level) {
        return 1 + level * 10;
    }

    private static int defaultMaxEnchantQuality(int level) {
        return defaultMinEnchantQuality(level) + 5;
    }

    private static IntUnaryOperator nmsIntUnaryOperator(
            Enchantment enchantment,
            String methodName,
            IntUnaryOperator defaultOperator
    ) {
        return nmsHandler(enchantment, nmsEnchant -> {
            Method method = nmsEnchant.getClass().getDeclaredMethod(methodName, int.class);

            return  level -> {
                try {
                    return (int) method.invoke(nmsEnchant, level);

                } catch (IllegalAccessException | InvocationTargetException e) {
                    return defaultOperator.applyAsInt(level);
                }
            };
        }, defaultOperator);
    }

    private static <T> T nmsHandler(
            Enchantment enchantment,
            ThrowingFunction<Object, T, ReflectiveOperationException> function,
            T defaultValue
    ) {
        try {
            Enchantment craftEnchant = Enchantment.getByKey(enchantment.getKey());

            if (craftEnchant == null) {
                return defaultValue;
            }

            Object nmsEnchant = craftEnchant.getClass().getDeclaredMethod("getHandle")
                    .invoke(craftEnchant);

            return function.apply(nmsEnchant);

        } catch (Exception e) {
            return defaultValue;
        }
    }

    private EnchantDataReflection() {

    }

}
