package com.newestaf.enhancedenchantment.anvil;

import com.newestaf.enhancedenchantment.util.ItemUtil;
import com.newestaf.enhancedenchantment.util.MetaCachedStack;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class AnvilOperationState {

    private final AnvilOperation operation;
    private final AnvilInventory inventory;
    private final MetaCachedStack base;
    private final MetaCachedStack addition;
    protected final MetaCachedStack result;
    private int levelCost = 0;
    private int materialCost = 0;

    public AnvilOperationState(AnvilOperation operation, AnvilInventory inventory) {
        this.operation = operation;
        this.inventory = inventory;
        this.base = new MetaCachedStack(this.inventory.getItem(0));
        this.addition = new MetaCachedStack(this.inventory.getItem(1));
        this.result = new MetaCachedStack(this.base.getItemStack().clone());
    }

    public AnvilInventory getAnvilInventory() {
        return this.inventory;
    }

    public MetaCachedStack getBase() {
        return this.base;
    }

    public MetaCachedStack getAddition() {
        return this.addition;
    }

    public int getLevelCost() {
        return this.levelCost;
    }

    public void setLevelCost(int levelCost) {
        this.levelCost = levelCost;
    }

    public int getMaterialCost() {
        return this.materialCost;
    }

    public void setMaterialCost(int materialCost) {
        this.materialCost = materialCost;
    }

    public boolean apply(AnvilFunction function) {
        if (function.canApply(this.operation, this)) {
            return false;
        }

        AnvilFunctionResult anvilResult = function.getResult(this.operation, this);

        anvilResult.modifyResult(this.result.getMeta());
        this.levelCost += anvilResult.getLevelCostIncrease();
        this.materialCost += anvilResult.getMaterialCostIncrease();

        return true;
    }

    public AnvilResult forge() {
        ItemMeta baseMeta = this.getBase().getMeta();
        ItemMeta resultMeta = this.result.getMeta();

        if (baseMeta == null) {
            return AnvilResult.EMPTY;
        }

        if (resultMeta == null) {
            return AnvilResult.EMPTY;
        }

        this.result.getItemStack().setItemMeta(resultMeta);

        resultMeta = resultMeta.clone();

        if (baseMeta instanceof Repairable baseRepairable
                && resultMeta instanceof Repairable resultRepairable) {
            resultRepairable.setRepairCost(baseRepairable.getRepairCost());
        }

        if (!ItemUtil.isEmpty(this.addition.getItemStack())) {
            resultMeta.displayName(baseMeta.displayName());
        }

        if (baseMeta.equals(resultMeta)) {
            return AnvilResult.EMPTY;
        }

        return new AnvilResult(this.result.getItemStack(), this.levelCost, this.materialCost);

    }

}
