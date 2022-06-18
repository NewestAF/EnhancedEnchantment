package com.newestaf.enhancedenchantment.anvil;

import com.newestaf.enhancedenchantment.enchant.EnchantData;
import com.newestaf.enhancedenchantment.enchant.EnchantRarity;
import com.newestaf.enhancedenchantment.enchant.EnchantmentUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

abstract class CombineEnchantments implements AnvilFunction {

    @Override
    public boolean canApply(AnvilOperation operation, AnvilOperationState state) {
        return operation.canCombineEnchant(state.getBase().getItemStack(), state.getAddition().getItemStack());
    }

    @Override
    public final AnvilFunctionResult getResult(AnvilOperation operation, AnvilOperationState state) {
        Map<Enchantment, Integer> baseEnchants = EnchantmentUtil.getEnchants(state.getBase().getMeta());
        Map<Enchantment, Integer> additionEnchants = EnchantmentUtil.getEnchants(state.getAddition().getMeta());

        if (additionEnchants.isEmpty()) {
            return AnvilFunctionResult.EMPTY;
        }

        Map<Enchantment, Integer> newEnchants = new HashMap<>(baseEnchants);
        boolean isFromBook = state.getAddition().getItemStack().getType() == Material.ENCHANTED_BOOK;

        int levelCost = 0;

        for (Entry<Enchantment, Integer> enchantEntry : additionEnchants.entrySet()) {
            Enchantment newEnchantment = enchantEntry.getKey();
            int oldLevel = baseEnchants.getOrDefault(newEnchantment, 0);
            int baseCost = getAnvilCost(newEnchantment, isFromBook);

            if (operation.canApplyEnchant(newEnchantment, state.getBase().getItemStack())
                    && baseEnchants.keySet().stream().noneMatch(existingEnchant ->
                            !existingEnchant.getKey().equals(newEnchantment.getKey())
                                    && operation.isEnchantConflict(existingEnchant, newEnchantment))) {

                int addedLevel = enchantEntry.getValue();
                int newLevel = oldLevel == addedLevel ? addedLevel + 1 : Math.max(oldLevel, addedLevel);
                newLevel = Math.min(newLevel, operation.getEnchantMaxLevel(newEnchantment));
                newEnchants.put(newEnchantment, newLevel);

                levelCost += getTotalCost(baseCost, oldLevel, newLevel);
            } else {
                levelCost += getNonApplicableCost();
            }
        }

        if (levelCost < 0) {
            levelCost = state.getAnvilInventory().getMaximumRepairCost();
        }

        int totalLevelCost = levelCost;

        return new AnvilFunctionResult() {
            @Override
            public int getLevelCostIncrease() {
                return totalLevelCost;
            }

            @Override
            public void modifyResult(ItemMeta itemMeta) {
                EnchantmentUtil.addEnchants(itemMeta, newEnchants);
            }
        };
    };

    protected EnchantRarity getRarity(Enchantment enchantment) {
        return EnchantData.of(enchantment).getRarity();
    }

    private int getAnvilCost(Enchantment enchantment, boolean isFromBook) {
        int value = getRarity(enchantment).getAnvilValue();
        return isFromBook ? Math.max(1, value / 2) : value;
    }

    protected abstract int getTotalCost(int baseCost, int oldLevel, int newLevel);

    protected abstract int getNonApplicableCost();

}
