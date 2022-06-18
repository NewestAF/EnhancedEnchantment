package com.newestaf.enhancedenchantment.anvil;

import org.bukkit.inventory.meta.ItemMeta;

public interface AnvilFunctionResult {

    AnvilFunctionResult EMPTY = new AnvilFunctionResult() {};

    default int getLevelCostIncrease() {
        return 0;
    }

    default int getMaterialCostIncrease() {
        return 0;
    }

    default void modifyResult(ItemMeta itemMeta) {}

}
