package com.newestaf.enhancedenchantment.anvil;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

public class AnvilOperation {

    private BiPredicate<Enchantment, ItemStack> canApplyEnchant;
    private BiPredicate<Enchantment, Enchantment> isEnchantConflict;
    private ToIntFunction<Enchantment> enchantMaxLevel;
    private BiPredicate<ItemStack, ItemStack> canRepairItemBy;
    private BiPredicate<ItemStack, ItemStack> canCombineEnchant;

    public AnvilOperation() {
        this.canApplyEnchant = Enchantment::canEnchantItem;
        this.isEnchantConflict = Enchantment::conflictsWith;
        this.enchantMaxLevel = Enchantment::getMaxLevel;
        this.canRepairItemBy =RepairMaterial::isRepairable;
        this.canCombineEnchant = (base, addition) ->
                base.getType() == addition.getType()
                || addition.getType() == Material.ENCHANTED_BOOK;
    }

    public boolean canApplyEnchant(Enchantment enchantment, ItemStack itemStack) {
        return this.canApplyEnchant.test(enchantment, itemStack);
    }

    public void setCanApplyEnchant(
            BiPredicate<Enchantment, ItemStack> canApplyEnchant
    ) {
        this.canApplyEnchant = canApplyEnchant;
    }

    public boolean isEnchantConflict(Enchantment enchantment1, Enchantment enchantment2) {
        return this.isEnchantConflict.test(enchantment1, enchantment2);
    }

    public void setIsEnchantConflict(
            BiPredicate<Enchantment, Enchantment> isEnchantConflict
    ) {
        this.isEnchantConflict = isEnchantConflict;
    }

    public int getEnchantMaxLevel(Enchantment enchantment) {
        return this.enchantMaxLevel.applyAsInt(enchantment);
    }

    public void setEnchantMaxLevel(ToIntFunction<Enchantment> enchantMaxLevel) {
        this.enchantMaxLevel = enchantMaxLevel;
    }

    public boolean canRepairItemBy(ItemStack repaired, ItemStack repairMaterial) {
        return this.canRepairItemBy.test(repaired, repairMaterial);
    }

    public void setCanRepairItemBy(BiPredicate<ItemStack, ItemStack> canRepairItemBy) {
        this.canRepairItemBy = canRepairItemBy;
    }

    public boolean canCombineEnchant(ItemStack base, ItemStack addition) {
        return this.canCombineEnchant.test(base, addition);
    }

    public void setCanCombineEnchant(BiPredicate<ItemStack, ItemStack> canCombineEnchant) {
        this.canCombineEnchant = canCombineEnchant;
    }





}
